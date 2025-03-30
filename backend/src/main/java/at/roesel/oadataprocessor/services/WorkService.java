/*
 *  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package at.roesel.oadataprocessor.services;

import at.roesel.oadataprocessor.services.common.IngestState;
import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.services.impexp.export.DownloadCache;
import at.roesel.oadataprocessor.services.openapc.OpenApcService;
import at.roesel.oadataprocessor.services.publicationsource.PublicationSourceService;
import at.roesel.oadataprocessor.services.websocket.WebSocketMessage;
import at.roesel.oadataprocessor.services.websocket.WebSocketService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class WorkService {

    private final Logger logger = LoggerFactory.getLogger(WorkService.class);

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private PublicationSourceService publicationSourceService;

    @Autowired
    private ClassifyService classifyService;

    @Autowired
    private PublisherIdentificationService publisherIdentificationService;

    @Autowired
    private PublicationAccessService publicationAccessService;

    @Autowired
    @Qualifier("DefaultTaskScheduler")
    private TaskScheduler taskScheduler;

    @Autowired
    private DownloadCache downloadCache;

    @Autowired
    private DataService dataService;

    @Autowired
    private OpenApcService openApcService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private WebSocketService webSocketService;

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicReference<IngestState> state = new AtomicReference<>(IngestState.IDLE); // Current state

    public WorkService(Environment environment) {
        // Do not start the executor in test mode
        if (!environment.acceptsProfiles(Profiles.of("test"))) {
            executor.submit(this::processQueue);
        }
    }

    // Processes tasks in the queue one by one
    private void processQueue() {
        while (true) {
            try {
                Runnable task = taskQueue.take();
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore the interrupt flag
                logger.debug("WorkService: Queue processing interrupted.");
                break; // Exit the loop if interrupted
            }
        }
    }

    private void setState(IngestState newState) {
        state.set(newState);
        sendStateToClient();
    }

    public IngestState getState() {
        return state.get();
    }

    public void sendStateToClient() {
        WebSocketMessage<String> msg = new WebSocketMessage<>("process_state");
        msg.data = getState().toString();
        webSocketService.sendMessageToTopic(msg);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow(); // Stop accepting new tasks and interrupt running tasks
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.info("WorkService.Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // enqueue the ingestPublications task, so that only one is run at a time
    public void ingestPublications() {
        synchronized (taskQueue) {
            // only add the task if there is no task waiting
            if (taskQueue.isEmpty()) {
                taskQueue.offer(() -> {
                    try {
                        performIngestPublications();
                    } catch (Exception e) {
                        setState(IngestState.ERROR);
                        logger.error(e.getMessage(), e);
                    } finally {
                        // Reset to IDLE after task finishes
                        if (taskQueue.isEmpty()) {
                            setState(IngestState.IDLE);
                        }
                    }
                });
            }
        }
    }

    /*
     * collectPublications
     * - fetches publications from the services of the activated institutions
     * - ingests the publications
     */
    public void collectPublications() {
        logger.info("collectPublications() started");
        publicationSourceService.fetchFromActiveInstitutions();

        ingestPublications();

        logger.info("collectPublications() finished");
    }

    /*
     * ingestPublications
     * - updates the publications from sources
     * - classifies the open access status of publications
     * - identifies journals for known issns in publications and identifies publisher of journal if possible
     * - identifies publishers
     */
    private void performIngestPublications() {
        setState(IngestState.STARTING);
        logger.info("ingestPublications() started");

        setState(IngestState.UPDATE_PUBLICATIONS);
        publicationSourceService.updatePublications(null);

        // COAT classification
        setState(IngestState.COAT_CLASSIFICATION);
        classifyService.classifyAllPublications();

        // search journal from issn(s) for publications
        logger.info("identifyJournal() started");
        setState(IngestState.IDENTIFY_JOURNAL);
        publisherIdentificationService.identifyJournal();

        // search publishers for publications
        logger.info("identifyPublisher() started");
        setState(IngestState.IDENTIFY_PUBLISHER);
        publisherIdentificationService.identifyPublisher();

        logger.info("identifyMainPublisher() started");
        setState(IngestState.IDENTIFY_MAIN_PUBLISHER);
        publisherIdentificationService.identifyMainPublisher();

        clearCaches();

        // update publications in Elasticsearch
        setState(IngestState.UPDATE_ELASTICSEARCH);
        publicationAccessService.storePublicationsInElastic();

        logger.info("ingestPublications() finished");
        setState(IngestState.COMPLETED);
    }

    @PostConstruct
    public void scheduleUpdaters() {
        // update Publications from institutions
        String cronExpression = appSettings.getUpdatePublicationsCronExpression();
        taskScheduler.schedule(() -> collectPublications(), new CronTrigger(cronExpression));

        // update OpenAPC
        String cronOpenApc = appSettings.getUpdateOpenApcCronExpression();
        taskScheduler.schedule(() -> openApcService.updateOpenAPCData(), new CronTrigger(cronOpenApc));
    }

    public void clearCaches() {
        // clear publication download cache
        downloadCache.clear();
        // clear data caches;
        dataService.clearAllCaches();
    }


    public void classifyPublications() {
        classifyService.classifyAllPublications();
        clearCaches();
    }

    /*
     * if there is new data available in Wikidata for journals and publishers, this method can be called to
     * reset the unknown placeholder ids in publications
     */
    public void resetUnknownJournalsAndPublishersInPublications() {
        // reset ids
        publicationService.resetUnknownJournalId();
        publicationService.resetUnknownPublisherId();
    }
}
