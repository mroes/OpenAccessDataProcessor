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
import at.roesel.oadataprocessor.model.*;
import at.roesel.oadataprocessor.persistance.JournalRepository;
import at.roesel.oadataprocessor.services.publisher.*;
import at.roesel.oadataprocessor.services.wikidata.JournalUpdater;
import at.roesel.oadataprocessor.services.wikidata.WikidataService;
import at.roesel.oadataprocessor.support.IssnSupport;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.model.Publication.*;
import static at.roesel.oadataprocessor.model.Publisher.RELEVANT;
import static at.roesel.oadataprocessor.support.PublicationSupport.propsFrom;

@Component
public class PublisherIdentificationService {

    @Value("${journalUpdateAge:86400000}")
    private int journalUpdateAge;

    private final Logger logger = LoggerFactory.getLogger(PublisherIdentificationService.class);
    // special logger for information about missing data for identification (usually from WikiData)
    private final Logger loggerDataMissing = LoggerFactory.getLogger("publisher-data-missing");
    // special logger for quickstatements for WikiData
    // https://quickstatements.toolforge.org/#/
    private final Logger loggerQuickstatements = LoggerFactory.getLogger("quickstatements");

    private final PublicationService publicationService;
    private final PublisherServiceImpl publisherService;
    private final WikidataService wikidataService;
    private final JournalRepository journalRepository;

    private final JournalUpdater journalUpdater = new JournalUpdater();

    public PublisherIdentificationService(PublicationService publicationService,
                                          PublisherServiceImpl publisherService,
                                          JournalRepository journalRepository,
                                          WikidataService wikidataService) {
        this.publicationService = publicationService;
        this.publisherService = publisherService;
        this.journalRepository = journalRepository;
        this.wikidataService = wikidataService;
    }

    public void identifyPublisher() {

        logger.info("Identify publisher");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        loggerDataMissing.info(String.format("Identify publisher %s", LocalDateTime.now().format(formatter)));

        PublisherMap publisherPerWikiId = new PublisherMap();
        publisherPerWikiId.fillByWikiId(publisherService.readPublishers());

        PublisherForJournal publisherForJournal = new PublisherForJournal(publisherPerWikiId);

        HashMap<String, UnidentifiedPublisher> unIdentifiedPublishers = new HashMap<>();
        HashMap<Journal, AtomicInteger> journalsWithoutPublishers = new HashMap<>();
        HashMap<String, Journal> journalCache = new HashMap<>();

        AtomicInteger count = new AtomicInteger();
        AtomicInteger count_identified = new AtomicInteger();
        AtomicInteger count_unidentified = new AtomicInteger();
        AtomicBoolean stop = new AtomicBoolean(false);

        publicationService.visitAll(0,
                new PublicationProvider() {
                    @Override
                    public Page<Publication> provide(PageRequest pageRequest) {
                        if (stop.get()) {
                            return Page.empty();
                        }
                        // find all publications with an empty publisherId
                        return publicationService.findAllByPublisherId(pageRequest, "");
                    }

                    @Override
                    public boolean isIncrementPage() {
                        return false;
                    }
                },
                publication -> {
                    count.getAndIncrement();
//                    logger.debug(String.format("%d\tSearching publisher for PublicationId=%s", count.get(), publication.getId()));
                    /*
                    if (count.get() > 100) {
                        stop.set(true);
                        return;
                    }
                     */
/*
                    if (hasValue(publisher.getPublisherId())) {
                        continue;
                    }
*/

                    /*
                    String doi = publication.getDoi();
                    String prefix = null;
                    if (hasValue(doi)) {
                        prefix = DoiSupport.prefix(doi);
                    }

                     */

                    // the higher the score, the higher is the trust in the correctness of the identified publisher
                    int score = 0;

                    // if the institution provides a publisher name, try to identify this publisher
                    int scoreInstitution = 0;
                    String publisherName = publication.getPublisher();
                    Publisher publisherFromInstitution = null;
                    PublisherIdentifyResult publisherFromInstitutionResult = null;
                    if (hasValue(publisherName)) {
                        logger.debug("{}\tPublisher from Institution: {}\t{}", count.get(), publication.getId(), publisherName);
                        publisherFromInstitutionResult = publisherService.identifyPublisher(publisherName);
                        publisherFromInstitution = publisherFromInstitutionResult.getPublisher();
                        scoreInstitution = calcScore(publisherName, publisherFromInstitutionResult);
                    }

                    PublicationProps props = propsFrom(publication);
                    // if there is a publisher from Crossref, try to identify this publisher
                    String publisherNameCr = props.publisherCr;
                    int scoreCrossref = 0;
                    Publisher publisherFromCrossref = null;
                    PublisherIdentifyResult publisherFromCrossrefResult = null;
                    if (hasValue(publisherNameCr)) {
                        logger.debug("{}\tPublisher from Crossref: {}\t{}", count.get(), publication.getId(), publisherNameCr);
                        publisherFromCrossrefResult = publisherService.identifyPublisher(publisherNameCr);
                        publisherFromCrossref = publisherFromCrossrefResult.getPublisher();
                        scoreCrossref = calcScore(publisherNameCr, publisherFromCrossrefResult);
                        if (!hasValue(publisherName)) {
                            publisherName = publisherNameCr;
                        }
                    }


                    Publisher publisherFromJournal = null;
                    // Is there a publisher from the journal?
                    Journal journal = null;
                    String journalId = publication.getJournalId();
                    if (hasValue(journalId) && !journalId.startsWith(UNKNOWN_PREFIX)) {
                        journal = journalCache.get(journalId);
                        if (journal == null) {
                            Optional<Journal> journalOpt = journalRepository.findById(journalId);
                            journal = journalOpt.orElse(null);
                            if (journal != null) {
                                journalCache.put(journalId, journal);
                            }
                        }
                    }
                    if (journal != null) {
                        String publisherId = journal.publisherId(publication.publicationDate());
                        if (!hasValue(publisherId)) {
                            // try to update journal
                            publisherForJournal.setPublisher(journal);
                            publisherId = journal.publisherId(publication.publicationDate());
                        }
                        if (hasValue(publisherId)) {
                            Publisher pub = publisherService.findById(publisherId);
                            if (pub != null) {
                                publisherFromJournal = pub;
                                logger.debug(String.format("%d\tFound publisher %s for Journal %s", count.get(), pub.getName(), journal.getName()));
                                score += 1000;
                            } else {
                                logger.error(String.format("%d\tNo publisher found for journalId=%s with publisherId=%s", count.get(), journal.getId(), publisherId));
                            }
                        } else {
                            logger.debug(String.format("Journal %s (%s) has no publisher (wikiPublisherId=%s)", journal.getId(), journal.getName(), journal.getWikiPublisherId()));
                        }
                        if (publisherFromJournal == null) {
                            AtomicInteger jcount = journalsWithoutPublishers.computeIfAbsent(journal, (k) -> new AtomicInteger(0));
                            jcount.incrementAndGet();
                        }
                    }

                    Publisher publisher = publisherFromJournal;
                    // first choice is the publisher from the Journal
                    if (publisher != null) {
                        if (publisherFromInstitution != null && !publisher.equals(publisherFromInstitution)) {
                            logger.warn(String.format("PublicationId=%s\tFound publisher %s for Journal, but different publisher %s from institution for %s", publication.getId(), publisher.getName(), publisherFromInstitution.getName(), publication.getPublisher()));
                        } else {
                            score += scoreInstitution;
                        }
                        if (publisherFromCrossref != null && !publisher.equals(publisherFromCrossref)) {
                            logger.warn(String.format("PublicationId=%s\tFound publisher %s for Journal, but different publisher %s from Crossref for %s", publication.getId(), publisher.getName(), publisherFromCrossref.getName(), publisherNameCr));
                        } else {
                            score += scoreCrossref;
                        }
                    } else {
                        // second choice is the publisher from Crossref
                        if (publisherFromCrossref != null) {
                            publisher = publisherFromCrossref;
                            score = scoreCrossref;
                        } else if (publisherFromInstitution != null) {
                            publisher = publisherFromInstitution;
                            score = scoreInstitution;
                        }
                        if (publisherFromInstitution != null && publisherFromCrossref != null) {
                            if (!publisherFromCrossref.equals(publisherFromInstitution)) {
                                logger.warn(String.format("PublicationId=%s\tFound publisher %s from institution, but different publisher %s from crossref for %s and %s", publication.getId(), publisherFromInstitution.getName(), publisherFromCrossref.getName(), publication.getPublisher(), publisherNameCr));
                                if (scoreInstitution > scoreCrossref) {
                                    publisher = publisherFromInstitution;
                                    score = scoreInstitution;
                                }
                            } else {
                                score += scoreInstitution;
                            }
                        }
                    }

                    if (publisher != null) {
                        count_identified.getAndIncrement();
                        logger.debug(String.format("PublicationId=%s\tFound publisher\t%s\tfor\t%s\tscore: %d", publication.getId(), publisher.getName(), publisherName, score));
                        publicationService.updatePublisherId(publication.getId(), publisher.getId());
                    } else {
                        publicationService.updatePublisherId(publication.getId(), Publisher.UNKNOWN_PUBLISHER);
                        count_unidentified.getAndIncrement();
                        if (hasValue(publisherName)) {
                            UnidentifiedPublisher record = unIdentifiedPublishers.computeIfAbsent(publisherName, (k) -> new UnidentifiedPublisher(null));
                            record.count++;
                            record.publicationIds.add(publication.getId());
                        }
                    }

                });

        logger.info(String.format("Publishers: Identified: %d, not identified: %d", count_identified.get(), count_unidentified.get()));
        // Log missing data into a dedicated file to serve as a basis for future data entry.
        if (loggerDataMissing.isInfoEnabled()) {
            loggerDataMissing.info("Unidentified Publishers");
            try {
                loggerDataMissing.info("PublisherName\tOccurrences\tPublicationIds\tPossible Publishers");
                for (Map.Entry<String, UnidentifiedPublisher> entrySet : unIdentifiedPublishers.entrySet()) {
                    // logger.debug(name);
                    UnidentifiedPublisher record = entrySet.getValue();
                    loggerDataMissing.info(String.format("%s\t%d\t%s\t%s", entrySet.getKey(), record.count, record.publicationIds(), record.candidateNames()));
                }

                loggerDataMissing.info("JournalId\tJournalName\tOccurrences\tWikiId\tWikiPublisherId");
                for (Map.Entry<Journal, AtomicInteger> entrySet : journalsWithoutPublishers.entrySet()) {
                    Journal journal = entrySet.getKey();
                    loggerDataMissing.info(String.format("%s\t%s\t%d\t%s\t%s", journal.getId(), journal.getName(), entrySet.getValue().get(), journal.getWikiId(), journal.getWikiPublisherId()));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private int calcScore(String publisherName, PublisherIdentifyResult result) {
        int score = 0;
        if (result != null && result.getPublisher() != null) {
            publisherName = publisherName.toLowerCase();
            int ratio = FuzzySearch.ratio(publisherName, result.getPublisher().getName().toLowerCase());
            score = ratio;
            if (score < 100) {
                for (String name : result.getPublisher().getAlternateNames()) {
                    ratio = FuzzySearch.ratio(publisherName, name.toLowerCase());
                    if (ratio > score) {
                        score = ratio;
                        if (score == 100) {
                            break;
                        }
                    }
                }
            }
        }
        return score;
    }


    /*
     * set journalId in publications,
     * check if publisher is available for journal in Wikidata,
     * try to fetch publisher from Wikidata if not available in own database yet
     */
    public void identifyJournal() {

        PublisherMap publisherPerWikiId = new PublisherMap();
        publisherPerWikiId.fillByWikiId(publisherService.readPublishers());
        PublisherForJournal publisherForJournal = new PublisherForJournal(publisherPerWikiId);

        JournalMap journalPerIssnCache = new JournalMap();

        logger.info("Identify journal");
        AtomicInteger count = new AtomicInteger();
        publicationService.visitAll(0,
                new PublicationProvider() {
                    @Override
                    public Page<Publication> provide(PageRequest pageRequest) {
                        // find all publications with an empty journalId
                        return publicationService.findAllEmptyJournalId(pageRequest);
                    }

                    @Override
                    public boolean isIncrementPage() {
                        return false;
                    }
                }, publication -> {

                    count.getAndIncrement();

                    String journalId = publication.getJournalId();

                    // if there is already a journalId then exit
                    if (journalId != null && !journalId.isEmpty()) {
                        return;
                    }
//            logger.debug(count + "\t" + publication.getId() + "\t" + publication.getPublisher());

                    Set<String> issns = new HashSet<>(publication.getIssn());

                    PublicationProps props = propsFrom(publication);
                    if (hasValue(props.issnCr)) {
                        issns.addAll(props.issnCr);
                    }

                    if (hasValue(props.issnL)) {
                        if (!issns.contains(props.issnL)) {
//                    logger.debug("issns {} don't include IssnL {} of publication id {}", issns, props.issnL, publication.getId());
                            issns.add(props.issnL);
                        }
                    }

                    if (issns.isEmpty()) {
//                logger.debug(String.format("no issns for publication id %s", publication.getId()));
                        // set a value for the journalId in the publication, so that this record is not considered until the data in the record is changed
                        publicationService.updateJournalId(publication.getId(), NO_ISSN_AVAILABLE);
                        return;
                    }

                    List<String> issnList = new ArrayList<>(issns);
                    // check issns
                    for (int i = 0; i < issnList.size(); i++) {
                        String issn = issnList.get(i);
                        String issnNorm = IssnSupport.normalizeIssn(issn);
                        if (!issn.equals(issnNorm)) {
                            logger.warn("publication {} has issn {} in wrong format", publication.getId(), issn);
                            issnList.set(i, issnNorm);
                        }
                    }

                    // is the journal already in the cache?
                    Journal journal = journalPerIssnCache.getJournal(issnList);
                    if (journal == null) {
//                logger.debug(String.format("Searching for issn: %s in Database and Wikidata", issnList));
                        journal = publisherService.searchAndSaveJournal(issnList);
                        if (journal != null) {
//                    logger.debug(String.format("Found: Journal %s in Database or Wikidata", journal.getName()));
                            // add to cache in journalIdentifier
                            List<String> journalIssns = journal.issnsAsList();
                            if (hasValue(journal.getIssnl())) {
                                journalPerIssnCache.addJournal(journal.getIssnl(), journal);
                            }
                            for (String issn : journalIssns) {
                                journalPerIssnCache.addJournal(issn, journal);
                            }
                        }
                    }

                    if (journal != null) {
                        publisherForJournal.setPublisher(journal);
                        journalId = journal.getId();
                    } else {
                        loggerDataMissing.info(String.format("No Journal found for publicationId for issn\t%s\t%s", publication.getId(), issns));
                        journalId = UNKNOWN_JOURNAL;
                    }
                    publicationService.updateJournalId(publication.getId(), journalId);

                });
        logger.info("Identify journal finished");

    }


    /*
     * called if a publisher with the wikiPublisherId is already existing in the database
     * Duplicate entry could exist if the publisher is ignored in the database (flag = 1)
     */
    private Publisher handleSpecialPublisher(String wikiPublisherId) {
        Publisher publisher;
        Publisher ignoredPublisher = publisherService.findByWikiId(wikiPublisherId);
        if (ignoredPublisher != null) {
            String mainId = ignoredPublisher.getMainId();
            // shall the publisher be replaced with another publisher?
            // e.g. Q34433 Universtät Oxford
            if (mainId != null && !mainId.isEmpty()) {
                publisher = publisherService.findById(mainId);
                return publisher;
            }
            // is the publisher ignored?
            else if (ignoredPublisher.isIgnored()) {
                // yes, then activate it
                logger.info("Activating publisher {} in database", wikiPublisherId);
                ignoredPublisher.setFlag(RELEVANT);
                try {
                    publisher = publisherService.save(ignoredPublisher);
                    return publisher;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }


    /*
     * search the main publishers for the publishers of a publication
     */
    public void identifyMainPublisher() {

        logger.info("Identify main publisher");

        AtomicInteger count = new AtomicInteger();
        AtomicInteger countMain = new AtomicInteger();
        AtomicBoolean stop = new AtomicBoolean(false);

        int year = 0;  // take the year from the publishing date of the publication itself
        Collection<Publisher> publishers = publisherService.readPublishers();
        MainPublisherSupplier supplier = new MainPublisherSupplier(publishers, year);

        publicationService.visitAll(0,
                new PublicationProvider() {
                    @Override
                    public Page<Publication> provide(PageRequest pageRequest) {
                        if (stop.get()) {
                            return Page.empty();
                        }
                        // find all publications with an empty mainPublisherId
                        return publicationService.findAllEmptyMainPublisherId(pageRequest);
                    }

                    @Override
                    public boolean isIncrementPage() {
                        return false;
                    }
                },
                publication -> {
                    count.getAndIncrement();
//                    logger.debug(String.format("%d\tSearching main publisher for PublicationId=%s", count.get(), publication.getId()));
                    /*
                    if (count.get() > 100) {
                        stop.set(true);
                        return;
                    }
                     */

                    String mainPublisherId = null;
                    String publisherId = publication.getPublisherId();
                    if (hasValue(publisherId) && !publisherId.equals(Publisher.UNKNOWN_PUBLISHER)) {
                        Publisher mainPublisher = supplier.findMainPublisher(publisherId, publication.getYear());
                        if (mainPublisher != null) {
                            countMain.getAndIncrement();
//                        logger.debug(String.format("PublicationId=%s\tFound main publisher\t%s\t%s", publication.getId(), mainPublisher.getName(), mainPublisher.getId()));
                            mainPublisherId = mainPublisher.getId();
                        } else {
                            logger.debug(String.format("PublicationId=%s\tNo main publisher found", publication.getId()));
                        }
                    }
                    if (mainPublisherId == null) {
                        mainPublisherId = Publisher.UNKNOWN_PUBLISHER;
                    }
                    if (!mainPublisherId.equals(publication.getMainPublisherId())) {
                        publicationService.updateMainPublisherId(publication.getId(), mainPublisherId);
                    }
                });

    }

    private class PublisherForJournal {

        private PublisherMap publisherPerWikiId;

        public PublisherForJournal(PublisherMap publisherPerWikiId) {
            this.publisherPerWikiId = publisherPerWikiId;
        }

        private void setPublisher(Journal journal) {
            boolean saveJournal = false;
            if (!journal.hasWikiPublisherId()) {
                // if the journal update from Wikidata is older than one day
                if (journal.getUpdatedWikidata() < SystemTime.currentTimeMillis() - journalUpdateAge) {
                    Journal updatedJournal = wikidataService.fetchJournal(journal.getWikiId());
                    if (updatedJournal != null) {
                        journalUpdater.updateJournal(journal, updatedJournal);
                        saveJournal = true;
                    }
                }
            }
            // resolve WikiIds
            if (hasValue(journal.getWikiPublisherId())) {
                String wikiPublisherId = journal.getWikiPublisherId();
                Publisher publisher = findPublisherForWikiId(wikiPublisherId);
                if (publisher != null) {
                    String publisherId = publisher.getId();
                    if (!Objects.equals(publisherId, journal.getPublisherId())) {
                        journal.setPublisherId(publisherId);
                        saveJournal = true;
                    }
                }
            }

            for (JournalVar var : journal.getVariable()) {
                String wikiPublisherId = var.getWikiPublisherId();
                if (hasValue(wikiPublisherId)) {
                    Publisher publisher = findPublisherForWikiId(wikiPublisherId);
                    if (publisher != null) {
                        String publisherId = publisher.getId();
                        if (!Objects.equals(publisherId, var.getPublisherId())) {
                            var.setPublisherId(publisherId);
                            saveJournal = true;
                        }
                    }
                }
            }

            if (saveJournal) {
                try {
                    publisherService.saveJournal(journal);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }

        private Publisher findPublisherForWikiId(String wikiPublisherId) {
            Publisher publisher = publisherPerWikiId.get(wikiPublisherId);
            if (publisher == null) {
                publisher = wikidataService.fetchPublisher(wikiPublisherId);
                if (publisher != null) {
                    // do we have a redirect?
                    Publisher existingPublisher = null;
                    if (!wikiPublisherId.equals(publisher.getWikiId())) {
                        existingPublisher = publisherPerWikiId.get(publisher.getWikiId());
                        if (existingPublisher != null) {
                            publisher = existingPublisher;
                        }
                    }
                    if (existingPublisher == null) {
                        try {
                            publisher = publisherService.save(publisher);
                        } catch (Exception e) {
                            // do we have a violation because a publisher with the wikidataId is already existing in the database?
                            if (e instanceof DataIntegrityViolationException) {
                                publisher = handleSpecialPublisher(wikiPublisherId);
                                if (publisher == null) {
                                    // still no publisher
                                    logger.error(e.getMessage(), e);
                                    return null;
                                }
                            } else {
                                logger.error(e.getMessage(), e);
                                return null;
                            }
                        }
                        publisherPerWikiId.put(wikiPublisherId, publisher);
                        // Quickstatement for Wikidata for adding the focuslist value to the publisher
                        loggerQuickstatements.info(String.format("%s|P5008|Q117222928", wikiPublisherId));
                    }
                }
            }
            if (publisher == null) {
                logger.debug(String.format("No publisher found for WikidataId %s", wikiPublisherId));
            }
            return publisher;
        }
    }
}