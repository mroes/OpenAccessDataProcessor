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

import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.Journal;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.wikidata.WikidataJsonEntity;
import at.roesel.oadataprocessor.persistance.JournalRepository;
import at.roesel.oadataprocessor.persistance.PublisherRepository;
import at.roesel.oadataprocessor.services.impexp.ImportType;
import at.roesel.oadataprocessor.services.impexp.LastImportService;
import at.roesel.oadataprocessor.services.publisher.*;
import at.roesel.oadataprocessor.services.wikidata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PublisherServiceImpl implements PublisherService, PublisherSource, JournalProvider, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(PublisherServiceImpl.class);

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    WikidataReplacementService wikidataReplacementService;

    @Autowired
    private WikidataService wikidataService;

    @Autowired
    private LastImportService lastImportService;

    private PublisherIdentifier publisherIdentifier;
    private final JournalUpdater journalUpdater = new JournalUpdater();


    @Override
    public void afterPropertiesSet() throws Exception {
        // to avoid a cyclic bean reference
        publisherIdentifier = new PublisherIdentifier(this);
    }

    @Override
    public Publisher findById(String id) {
        return publisherRepository.findById(id).orElse(null);
    }

    @Override
    public Publisher findByWikiId(String wikidataId) {
        return publisherRepository.findByWikiId(wikidataId);
    }


    @Override
    public Publisher save(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    @Override
    public List<Publisher> readPublishers() {
        return publisherRepository.findAllByFlag(Publisher.RELEVANT);
    }

    // read all publisher that are used in publications
    @Override
    public List<Publisher> readActivePublishers() {
        List<Publisher> mainPublishers = publisherRepository.findAllMainPublishersWithPublications();
        List<Publisher> directPublishers = publisherRepository.findAllDirectPublishersWithPublications();

        Set<Publisher> uniquePublishers = new HashSet<>();
        uniquePublishers.addAll(mainPublishers);
        uniquePublishers.addAll(directPublishers);
        /*
        for (Publisher publisher : mainPublishers) {
            boolean added = uniquePublishers.add(publisher);
            if (!added) {
                System.out.println("Duplicate publisher: " + publisher.getName());
            }
        }

        for (Publisher publisher : directPublishers) {
            boolean added = uniquePublishers.add(publisher);
            if (!added) {
                System.out.println("Duplicate publisher: " + publisher.getName());
            }
        }
         */

        return new ArrayList<>(uniquePublishers);
    }

    private PublisherMap buildPublisherMap() {
        PublisherMap publisherPerWikiId = new PublisherMap();
        publisherPerWikiId.fillByWikiId(publisherRepository.findAll());
        return publisherPerWikiId;
    }


    @Override
    public Journal readJournalById(String journalId) {
        return journalRepository.findById(journalId).orElse(null);
    }

    @Override
    public Journal readJournalByWikiId(String wikidataId) {
        return journalRepository.findByWikiId(wikidataId);
    }


    @Override
    public Journal saveJournal(Journal journal) {
        return journalRepository.save(journal);
    }

    @Override
    public List<Journal> readJournals() {
        return journalRepository.findAllByFlag(Journal.RELEVANT);
    }

    @Override
    public List<Journal> readJournalsOfPublications() {
        return journalRepository.findAllUsedJournals();
    }

    @Override
    public Journal searchJournal(List<String> issns) {
        logger.debug(String.format("Searching for issn: %s in Database", issns));
        Journal journal = searchJournalInDatabaseByIssn(issns);
        if (journal == null) {
            logger.debug(String.format("Searching for issn: %s in Wikidata", issns));
            journal = wikidataService.searchJournal(issns);
            if (journal != null) {
                // search again in database by WikidataId, journal may be existing with another issn
                Journal existingJournal = readJournalByWikiId(journal.getWikiId());
                if (existingJournal != null) {
                    journalUpdater.updateJournal(existingJournal, journal);
                    journal = existingJournal;
                }
                journal.setChanged(true);
            }
        }
        return journal;
    }

    public Journal searchAndSaveJournal(List<String> issns) {
        try {
            Journal journal = searchJournal(issns);
            if (journal != null) {
                // activate journal if not active
                if (journal.getFlag() != Journal.RELEVANT) {
                    journal.setFlag(Journal.RELEVANT);
                    journal.setChanged(true);
                }
                if (journal.isChanged()) {
                    journal = saveJournal(journal);
                    journal.setChanged(false);
                }
            }
            return journal;
        } catch (Exception e) {
            logger.error("error in searchAndSaveJournal()", e);
        }
        return null;
    }

    public Journal searchJournalInDatabaseByIssn(List<String> issns) {
        for (String issn : issns) {
            List<Journal> journals = journalRepository.findAllByIssn(issn);
            if (journals.size() > 1) {
                logger.warn("Found multiple journals {} in database with issn {}", journals, issn);
            }
            if (!journals.isEmpty()) {
                Journal journal = journals.get(0);
                logger.debug("Found journal {} with issn {} in database", journal.getName(), issn);
                return journal;
            }
        }
        return null;
    }

    public List<Publisher> findPublishers(String searchName) {
        searchName = "%" + searchName + "%";
        List<Publisher> publishers = publisherRepository.findAllByNameLikeAndFlag(searchName, Publisher.RELEVANT);
        return publishers;
    }


    @Override
    public PublisherIdentifyResult identifyPublisher(String publisherName) {
        Publisher publisher = publisherIdentifier.identify(publisherName);
        PublisherIdentifyResult result = new PublisherIdentifyResult(publisher);
        result.setCandidates(publisherIdentifier.candidates());
        return result;
    }

    @Override
    public void updatePublishersfromWikidata(LocalDate modifiedAfterDate) {

        logger.info("Update publishers from Wikidata");

        if (modifiedAfterDate == null) {
            modifiedAfterDate = lastImportService.loadLastImportDate(ImportType.WIKIDATA_PUBLISHER);
        }
        WikidataPublisherImporter importer = new WikidataPublisherImporter(appSettings, this, wikidataService::fetchPublisher);
        importer.updatePublishersfromWikidata(modifiedAfterDate);
        lastImportService.saveLastServiceImportDate(ImportType.WIKIDATA_PUBLISHER, LocalDate.now());

        replaceRedirectedPublishers(importer.getRedirectedPublishers());

        // Resolve wikidataIds to internal Ids
        PublisherMap publishers = buildPublisherMap();
        PublisherIdResolver publisherIdResolver = new PublisherIdResolver(publishers);

        // check all publishers
        for (Publisher publisher : publishers.values()) {
            boolean changed = publisherIdResolver.resolvePublisherIds(publisher);
            if (changed) {
                save(publisher);
            }
        }

        logger.info("Update publishers from Wikidata finished");

    }

    protected void replaceRedirectedPublishers(Map<Publisher, Publisher> redirectedPublishers) {
        try {
            if (!redirectedPublishers.isEmpty()) {
                redirectedPublishers.forEach((pub, newPub) -> wikidataReplacementService.replaceAndDeletePublisher(pub, newPub));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public void updateJournalsfromWikidata(boolean all) {

        logger.info("Update journals from Wikidata");

        List<Journal> journals;
        if (all) {
            journals = readJournals();
        } else {
            journals = readJournalsOfPublications();
        }
        int count = 0;
        int updated = 0;
        for (Journal journal : journals) {
            count++;
            if (journal.getUpdatedWikidata() > System.currentTimeMillis() - 86400 * 1000 * 7) {
                continue;
            }
            Journal newJournal = wikidataService.fetchJournal(journal.getWikiId());
            if (newJournal == null) {
                continue;
            }
            boolean isRedirected = !newJournal.getWikiId().equals(journal.getWikiId());
            if (isRedirected) {
                // replace journal in publications
                try {
                    wikidataReplacementService.replaceAndDeleteJournal(journal, newJournal);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                boolean changed = journalUpdater.updateJournal(journal, newJournal);
                if (changed) {
                    try {
                        saveJournal(journal);
                        updated++;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }

        logger.info("Update journals from Wikidata finished, updated {} of {}", updated, count);

    }

    public void removeRedirectedPublishers() {

        Iterable<Publisher> publishers = publisherRepository.findAll();
        PublisherMap publisherMap = new PublisherMap();
        publisherMap.fillByWikiId(publishers);

        Map<String, List<Publisher>> map = new HashMap<>();
        for (Publisher publisher : publishers) {
            List<Publisher> entry = map.computeIfAbsent(publisher.getName().toLowerCase(), (key) -> new ArrayList<>());
            entry.add(publisher);
        }
        for (Map.Entry<String, List<Publisher>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                logger.debug("{}\t{}", entry.getKey(), entry.getValue().stream().map(p -> p.getName() + " (" + p.getId() + ") " + p.getWikiId()).collect(Collectors.toList()));
                for (Publisher publisher : entry.getValue()) {
                    String wikidataId = publisher.getWikiId();
                    WikidataJsonEntity entity = wikidataService.fetchWikidataEntity(wikidataId);
                    if (entity != null) {
                        if (!entity.id.equals(wikidataId)) {
                            logger.debug("Publisher {} is replaced by {}", wikidataId, entity.id);
                            Publisher newPublisher = publisherMap.get(entity.id);
                            if (newPublisher != null) {
                                wikidataReplacementService.replaceAndDeletePublisher(publisher, newPublisher);
                            } else {
                                logger.error("Replacement Publisher {} not found in database", wikidataId);
                            }
                        }
                    } else {
                        logger.error("Entity {} not found in Wikidata", wikidataId);
                    }
                }
            }
        }
    }

}
