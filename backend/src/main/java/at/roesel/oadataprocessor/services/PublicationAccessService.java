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

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.components.controller.PublicationSearchFilter;
import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.config.ElasticsearchSettings;
import at.roesel.oadataprocessor.model.*;
import at.roesel.oadataprocessor.persistance.ElasticLogRepository;
import at.roesel.oadataprocessor.persistance.PublicationRepository;
import at.roesel.oadataprocessor.persistance.elastic.ElasticClient;
import at.roesel.oadataprocessor.persistance.elastic.ElasticClientImpl;
import at.roesel.oadataprocessor.persistance.elastic.ElasticCursor;
import at.roesel.oadataprocessor.persistance.elastic.ElasticResultHandler;
import at.roesel.oadataprocessor.services.publisher.PublisherMap;
import at.roesel.oadataprocessor.support.DoiSupport;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.util.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static at.roesel.oadataprocessor.persistance.elastic.QuerySupport.buildQueryForFilter;

/*
 * External services for publications
 * Storing publications in Elasticsearch for faster access to individual publications
 * Methods for querying publications for the UI, web services, and download services
*/

@Component
public class PublicationAccessService {

    private final Logger logger = LoggerFactory.getLogger(PublicationAccessService.class);

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private ElasticsearchSettings elasticsearchSettings;

    @Autowired
    private ElasticLogRepository elasticLogRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublisherServiceImpl publisherService;

    @Autowired
    private PublicationTypeService publicationTypeService;

    @Autowired
    private InstitutionService institutionService;


    public PublicationAccessService() {

    }

    public void storePublicationsInElastic() {
        logger.info("Start store publications in ElasticSearch");

        Long latestUpdateTime = elasticLogRepository.latestUpdateTime();
        if (latestUpdateTime == null) {
            latestUpdateTime = 0L;
        }
        long lastStartTime = latestUpdateTime;

        ElasticClient elasticClient = new ElasticClientImpl(elasticsearchSettings);

        PublicationFlattener publicationFlattener = createPublicationFlattener();

        // transfer publications that have an update timestamp after the timestamp of the last transfer
        long startTime = SystemTime.currentTimeMillis();
        int status = 0;
        String comment = null;
        AtomicLong lastPublicationUpdateTime = new AtomicLong(lastStartTime);
        AtomicInteger count = new AtomicInteger();
        AtomicInteger updateCount = new AtomicInteger();
        boolean[] stop = {false};   // used for debugging to be able to stop the run
        try {
            publicationService.visitAll(0,
                    new PublicationProvider() {
                        @Override
                        public Page<Publication> provide(PageRequest pageRequest) {
                            return publicationRepository.findAllByStatusAndUpdatedAfterOrderByUpdatedAsc(pageRequest, 0, lastStartTime);
                        }

                        @Override
                        public boolean isIncrementPage() {
                            return true;
                        }
                    },
                    publication -> {
                        count.getAndIncrement();
                        try {
                            PublicationFlat publicationFlat = publicationFlattener.flatten(publication);
                            IndexResponse response = elasticClient.store(publicationFlat, publication.getId(), appSettings.getElasticPublicationIndex());
                            logger.debug(String.format("Stored pubId=%s, response=%s, update=%d", publication.getId(), response.result(), publication.getUpdated()));
                            // remember the last update time, so that we can start the next run with this time
                            lastPublicationUpdateTime.set(publication.getUpdated());
                            updateCount.getAndIncrement();
                            // stop for debugging with a development environment
                            if (stop[0]) {
                                throw new Exception("stopped");
                            }
                        } catch (Exception e) {
                            String msg = String.format("Error on storing publicationId = %s, %s", publication.getId(), e.getMessage());
                            throw new RuntimeException(msg, e);
                        }
                    });
        } catch (Exception e) {
            status = -1;
            comment = e.getMessage();
            logger.error(e.getMessage()); // startTime = lastStartTime
        }
        ElasticLog elasticLog = new ElasticLog();
        if (status == 0) {
            // update time for the next run
            elasticLog.setUpdatetime(startTime);
        } else {
            // In case of an error, we will continue next time from the time of the last written publication.
            elasticLog.setUpdatetime(lastPublicationUpdateTime.get());
        }
        elasticLog.setStartTime(startTime);
        elasticLog.setEndTime(SystemTime.currentTimeMillis());
        elasticLog.setRecords(count.get());
        elasticLog.setComment(comment);
        elasticLog.setStatus(status);
        elasticLogRepository.save(elasticLog);
        logger.info("Stored {} publications in ElasticSearch", updateCount.get());
    }

    public IndexResponse storeSinglePublicationInElastic(String id) {
        ElasticClient elasticClient = new ElasticClientImpl(elasticsearchSettings);

        PublicationFlattener publicationFlattener = createPublicationFlattener();

        Publication publication = publicationService.readById(id);
        try {
            PublicationFlat publicationFlat = publicationFlattener.flatten(publication);
            IndexResponse response = elasticClient.store(publicationFlat, publication.getId(), appSettings.getElasticPublicationIndex());
            return response;
        } catch (Exception e) {
            String msg = String.format("pubId = %s, %s", publication.getId(), e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    private PublicationFlattener createPublicationFlattener() {
        Map<String, Institution> institutionMap = institutionService.institutionMap();
        Map<Integer, PublicationType> pubTypeMap = publicationTypeService.buildTable();
        PublisherMap publisherMap = new PublisherMap();
        publisherMap.fillById(publisherService.readPublishers());
        PublicationFlattener publicationFlattener = new PublicationFlattener(institutionMap, pubTypeMap, publisherMap, publisherService);
        return publicationFlattener;
    }

    private static ObjectBuilder<Property> buildPublisher(Property.Builder publisherBuilder) {
        return publisherBuilder
                .object(objectBuilder -> objectBuilder
                        .properties("name", nameBuilder -> nameBuilder
                                .text(nameText -> nameText.fields("keyword",
                                                nameKeyword -> nameKeyword.keyword(b -> b.index(true)))
                                        .index(true))
                        )
                        .properties("id", idBuilder -> idBuilder.keyword(keyword -> keyword.index(true)))
                        .properties("wikiDataId", builder -> builder.keyword(keyword -> keyword.index(true)))
                );
    }

    public CreateIndexResponse createElasticIndex() {
        ElasticClient elasticClient = new ElasticClientImpl(elasticsearchSettings);
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index(appSettings.getElasticPublicationIndex())
                .mappings(new TypeMapping.Builder()
                        .properties("id", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("doi", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("title", builder -> builder.text(builder1 -> builder1.index(true)))

                        .properties("year", builder -> builder.integer(builder1 -> builder1.index(true)))

                        .properties("type", typeBuilder -> typeBuilder
                                .object(objectBuilder -> objectBuilder
                                        .properties("id", builder -> builder.keyword(keyword -> keyword.index(true)))
                                        .properties("name", idBuilder -> idBuilder.keyword(keyword -> keyword.index(true)))
                                        .properties("coarId", builder -> builder.keyword(builder1 -> builder1.index(false)))
                                )
                        )

                        .properties("coat", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("color", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("colorUpw", builder -> builder.keyword(builder1 -> builder1.index(true)))

                        .properties("publisher", PublicationAccessService::buildPublisher)
                        .properties("mainPublisher", PublicationAccessService::buildPublisher)

                        .properties("journal", journalBuilder -> journalBuilder
                                .object(objectBuilder -> objectBuilder
                                        .properties("title", nameBuilder -> nameBuilder
                                                .text(nameText -> nameText.fields("keyword",
                                                                nameKeyword -> nameKeyword.keyword(b -> b.index(true)))
                                                        .index(true))
                                        )
                                        .properties("id", idBuilder -> idBuilder.keyword(keyword -> keyword.index(true)))
                                        .properties("wikiDataId", builder -> builder.keyword(keyword -> keyword.index(true)))
                                        .properties("issn", builder -> builder.keyword(builder1 -> builder1.index(true)))
                                )
                        )

                        .properties("authors", authorBuilder -> authorBuilder
                                .nested(nestedBuilder -> nestedBuilder
                                        .properties("firstName", nameBuilder -> nameBuilder
                                                .keyword(nameKeyword -> nameKeyword.index(true))
                                        )
                                        .properties("lastName", nameBuilder -> nameBuilder
                                                .keyword(nameKeyword -> nameKeyword.index(true))
                                        )
                                        .properties("orcid", orcidBuilder -> orcidBuilder
                                                .keyword(nameKeyword -> nameKeyword.index(true))
                                        )
                                        .properties("organisation", nameBuilder -> nameBuilder
                                                .text(nameKeyword -> nameKeyword.index(true))
                                        )
                                        .properties("role", nameBuilder -> nameBuilder
                                                .keyword(nameKeyword -> nameKeyword.index(false))
                                        )
                                        .properties("corr", nameBuilder -> nameBuilder
                                                .boolean_(nameKeyword -> nameKeyword.index(true))
                                        )
                                )
                        )

                        .properties("licence", jorunalBuilder -> jorunalBuilder
                                .object(objectBuilder -> objectBuilder
                                        .properties("licence", idBuilder -> idBuilder.keyword(keyword -> keyword.index(true)))
                                        .properties("url", builder -> builder.keyword(keyword -> keyword.index(false)))
                                        .properties("source", builder -> builder.keyword(builder1 -> builder1.index(false)))
                                )
                        )

                        .properties("sources", sourcesBuilder -> sourcesBuilder
                                .nested(nestedBuilder -> nestedBuilder
                                        .properties("institutionId", nameBuilder -> nameBuilder
                                                .keyword(nameKeyword -> nameKeyword.index(true))
                                        )
                                        .properties("nativeId", nameBuilder -> nameBuilder
                                                .keyword(nameKeyword -> nameKeyword.index(true))
                                        )
                                        .properties("corr", nameBuilder -> nameBuilder
                                                .boolean_(nameKeyword -> nameKeyword.index(true))
                                        )
                                )
                        )

                        .properties("costs", jorunalBuilder -> jorunalBuilder
                                .object(objectBuilder -> objectBuilder
                                        .properties("amount", idBuilder -> idBuilder.keyword(keyword -> keyword.index(false)))
                                        .properties("currency", builder -> builder.keyword(keyword -> keyword.index(false)))
                                        .properties("source", builder -> builder.keyword(builder1 -> builder1.index(false)))
                                )
                        )

                        .properties("embargoTime", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("embargoSource", builder -> builder.keyword(builder1 -> builder1.index(false)))
                        .properties("version", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("versionSource", builder -> builder.keyword(builder1 -> builder1.index(false)))
                        .properties("oaVersionLink", builder -> builder.keyword(builder1 -> builder1.index(false)))
                        .properties("oaPlace", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("oaPlaceSource", builder -> builder.keyword(builder1 -> builder1.index(false)))
                        .build())
                .build();
        CreateIndexResponse response = elasticClient.createIndexRequest(createIndexRequest);
        return response;
    }

    public void readPublicationsFromElastic(PublicationSearchFilter filter, ElasticResultHandler<PublicationFlat> handler) {
        ElasticClient elasticClient = new ElasticClientImpl(elasticsearchSettings);
        elasticClient.search(PublicationFlat.class, appSettings.getElasticPublicationIndex(), buildQueryForFilter(filter), handler);
    }

    public List<PublicationFlat> readPublicationsFromElastic(PublicationSearchFilter filter) {
        return readPublicationsFromElastic(filter, null, 1000);
    }

    public List<PublicationFlat> readPublicationsFromElastic(PublicationSearchFilter filter, ElasticCursor cursor, int limit) {
        ElasticClient elasticClient = new ElasticClientImpl(elasticsearchSettings);

        try {
            List<PublicationFlat> publications;
            if (cursor == null) {
                publications = elasticClient.search(PublicationFlat.class, appSettings.getElasticPublicationIndex(), buildQueryForFilter(filter));
            } else {
                publications = elasticClient.pagedSearch(PublicationFlat.class, appSettings.getElasticPublicationIndex(), buildQueryForFilter(filter),
                        limit, cursor);
            }
            return publications;

        } catch (Exception e) {
            throw e;
        }

    }

    public PublicationFlat readPublicationFromElastic(String publicationId) {
        ElasticClient elasticClient = new ElasticClientImpl(elasticsearchSettings);

        PublicationFlat publication = elasticClient.readById(PublicationFlat.class, appSettings.getElasticPublicationIndex(), publicationId);
        return publication;
    }

    public PublicationFlat searchPublicationFromElasticById(String searchId) {
        if (searchId == null || searchId.isEmpty()) {
            return null;
        }

        ElasticClient elasticClient = new ElasticClientImpl(elasticsearchSettings);

        PublicationFlat publication = null;
        // do we have a DOI as parameter?
        String doi = DoiSupport.parseDoi(searchId);
        if (doi != null) {
            List<PublicationFlat> publications = elasticClient.search(PublicationFlat.class, appSettings.getElasticPublicationIndex(), "doi", doi);
            if (!publications.isEmpty()) {
                publication = publications.get(0);
            }
        } else {
            // search by internal Id
            publication = elasticClient.readById(PublicationFlat.class, appSettings.getElasticPublicationIndex(), searchId);
        }
        return publication;
    }

}
