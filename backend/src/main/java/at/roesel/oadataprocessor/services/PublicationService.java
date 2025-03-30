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

import at.roesel.common.CollectionSupport;
import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.model.Author;
import at.roesel.oadataprocessor.model.Institution;
import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.crossref.CrossrefSource;
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;
import at.roesel.oadataprocessor.model.ui.PublicationColor;
import at.roesel.oadataprocessor.persistance.PublicationRepository;
import at.roesel.oadataprocessor.services.crossref.CrossrefDoiSearchResult;
import at.roesel.oadataprocessor.services.crossref.CrossrefService;
import at.roesel.oadataprocessor.services.openalex.OpenAlexService;
import at.roesel.oadataprocessor.services.unpaywall.UnpaywallService;
import at.roesel.oadataprocessor.support.AuthorComparator;
import at.roesel.oadataprocessor.support.CrossrefSupport;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.model.Publication.DOI_SOURCE_MANUAL;
import static at.roesel.oadataprocessor.support.DoiSupport.containsReservedCharacter;
import static at.roesel.oadataprocessor.support.DoiSupport.parseDoi;

/*
 * Internal service for publications
 * CRUD for publications
 * processing
 */

@Component
public class PublicationService {

    private final Logger logger = LoggerFactory.getLogger(PublicationService.class);

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PublicationTypeService publicationTypeService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private CrossrefService crossrefService;

    @Autowired
    private UnpaywallService unpaywallService;

    @Autowired
    private OpenAlexService openAlexService;


    private final AuthorComparator authorComparator;

    public PublicationService() {
        authorComparator = new AuthorComparator();
    }

    public PublicationDataEnhancer createPublicationDataEnhancer(Map<String, Institution> institutionMap) {
        return new PublicationDataEnhancer(crossrefService, unpaywallService, openAlexService, institutionMap);
    }


    public Publication save(Publication publication) {
        return publicationRepository.save(publication);
    }

    public Iterable<Publication> readAll() {
        return publicationRepository.findAll();
    }

    public Iterable<Publication> readAllByInstitution(String institution) {
        return publicationRepository.findAllByInstitution(institution);
    }

    public Page<Publication> findAllByClassificationStatus(Pageable pageable, int classification) {
        return publicationRepository.findAllByClassificationStatus(pageable, classification);
    }

    public Page<Publication> findAllByInstitutionAndClassificationStatus(Pageable pageable, String institution, int classification) {
        return publicationRepository.findAllByInstitutionAndClassificationStatus(pageable, institution, classification);
    }

    public Page<Publication> findAllByPublisherId(Pageable pageable, String publisherId) {
        return publicationRepository.findAllByPublisherId(pageable, publisherId);
    }

    public Page<Publication> findAllWithPublisherId(Pageable pageable) {
        return publicationRepository.findAllWithPublisherId(pageable);
    }

    public Page<Publication> findAllEmptyMainPublisherId(Pageable pageable) {
        return publicationRepository.findAllEmptyMainPublisherId(pageable);
    }

    public Page<Publication> findAllByJournalId(Pageable pageable, String journalId) {
        return publicationRepository.findAllByJournalId(pageable, journalId);
    }

    public Page<Publication> findAllEmptyJournalId(Pageable pageable) {
        return publicationRepository.findAllEmptyJournalId(pageable);
    }


    public int visitAll(Consumer<Publication> visitor) {
        return visitAll(0, visitor);
    }

    public int visitAll(int startPage, Consumer<Publication> visitor) {
        return visitAll(startPage, new PublicationProvider() {
            @Override
            public Page<Publication> provide(PageRequest pageRequest) {
                return publicationRepository.findAllByStatus(pageRequest, Publication.STATUS_ACTIVE);
            }

            @Override
            public boolean isIncrementPage() {
                return true;
            }
        }, visitor);
    }

    public int visitAll(int startPage, PublicationProvider publicationProvider, Consumer<Publication> visitor) {

        int page = startPage;
        int count = 0;  // counter for the loop iteration
        while (true) {
//            logger.debug(String.format("page: %d, count: %d", page, count));
            PageRequest pageRequest = PageRequest.of(page, 500);
            Page<Publication> publications = publicationProvider.provide(pageRequest);
            if (publications.isEmpty()) {
                break;
            }
            for (Publication publication : publications) {
                visitor.accept(publication);
            }
            count++;
            // if the visitor changes the status of the publication, so that provide() is influenced and delivers fewer results
            // then the page must not be incremented
            if (publicationProvider.isIncrementPage()) {
                page++;
            }
        }
        return count;
    }

    public Page<Publication> readAll(Pageable pageable) {
        return publicationRepository.findAll(pageable);
    }

    public Page<Publication> findByYear(Pageable pageable, int year) {
        return publicationRepository.findByYear(pageable, year);
    }

    private List<Publication> readAllByTitle(String title) {
        String searchTitle = "%" + simplifyTitle(title) + "%";
        return IterableUtils.toList(publicationRepository.findAllByTitleLike(searchTitle));
    }

    private List<Publication> readAllByTitleHash(int hash) {
        return IterableUtils.toList(publicationRepository.findAllByTitleHash(hash));
    }

    // For long titles, we take out a portion.
    // If the title starts with ' or ", we exclude this character.
    private String simplifyTitle(String title) {
        String result;
        if (title.length() > 32) {
            result = title.substring(12, 32);
        } else {
            result = title;
        }
        if (title.startsWith("'") || title.startsWith("\"")) {
            result = result.substring(1);
        }

        return result;
    }

    public List<Publication> readAllForInstitution(String institute) {
        return IterableUtils.toList(publicationRepository.findAllByInstitution(institute));
    }

    public Publication readById(String id) {
        return publicationRepository.findById(id).orElse(null);
    }

    public Publication readByDoi(String doi) {
        List<Publication> publications = IterableUtils.toList(publicationRepository.findAllByDoi(doi));
        if (publications.isEmpty()) {
            return null;
        } else if (publications.size() == 1) {
            return publications.get(0);
        } else {
            logger.warn(String.format("found %d publications for doi %s", publications.size(), doi));
            return publications.get(0);
        }
    }

    public void delete(Publication publication) {
        publicationRepository.delete(publication);
    }

    public void deleteAll() {
        publicationRepository.deleteAll();
    }

    public Publication findPublicationBySourceId(String sourceId) {
        Publication publication = publicationRepository.findBySourceId(sourceId);
        return publication;
    }

    public int updateColor(String id, String color) {
        return publicationRepository.updateColor(id, color, SystemTime.currentTimeMillis());
    }

    public int updateComment(String id, String comment) {
        return publicationRepository.updateComment(id, comment);
    }

    public int updatePublisherId(String id, String publisherId) {
        return publicationRepository.updatePublisherId(id, publisherId, SystemTime.currentTimeMillis());
    }

    public int updateMainPublisherId(String id, String publisherId) {
        return publicationRepository.updateMainPublisherId(id, publisherId, SystemTime.currentTimeMillis());
    }

    public int updateJournalId(String id, String journalId) {
        return publicationRepository.updateJournalId(id, journalId, SystemTime.currentTimeMillis());
    }

    public int resetUnknownJournalId() {
        return publicationRepository.resetUnknownJournalId();
    }

    public int resetUnknownPublisherId() {
        return publicationRepository.resetUnknownPublisherId();
    }

    public Publication findPublication(Publication searchPublication) {
        Publication result = null;
        // 1.Search for DOI
        String doi = searchPublication.getDoi();
        if (doi != null && !doi.isEmpty()) {
            result = readByDoi(doi);
        }
        if (result == null) {
            // 2.Search for title
            List<Publication> publications = readAllByTitleHash(searchPublication.getTitleHash());
            if (!publications.isEmpty()) {
                for (Publication pub : publications) {
                    if (hasValue(doi) && hasValue(pub.getDoi())) {
                        if (pub.getDoi().equals(doi)) {
                            // If the DOI matches, we merge the records
                            result = pub;
                            break;
                        } else {
                            // There are publications that appear under the same title in different journals.
                            // e.g. 10.1007/s00142-018-0255-1, 10.1007/s00167-018-5198-6
                            // If these have different DOIs, we do not group them together.
                            continue;
                        }
                    }
                    // The title is often not unique, so also compare the publication year and the exact title
                    if (pub.getYear() == searchPublication.getYear()
                            && pub.getTitle().equals(searchPublication.getTitle())) {
                        // also compare the authors, because even in a year there are often titles that are not unique, e.g. "Editorial"
                        // Authors often have different spellings, e.g., in their first names
                        // Therefore, this search does not always find matching publications, even if it is the same publication
                        if (CollectionSupport.isEqualCollection(searchPublication.getAuthors(), pub.getAuthors(), authorComparator)) {
                            result = pub;
                            break;
                        } else {
                            logger.warn(String.format("candidate for find publication for %s: %s, but authors are different", searchPublication.getId(), pub.getId()));
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<PublicationColor> readColorsPerYearAndInstitution() {
        return publicationRepository.readColorsPerYearAndInstitution();
    }

    public List<PublicationColor> readColorsPerYearAndPublisher() {
        return publicationRepository.readColorsPerYearAndPublisher();
    }

    public List<PublicationColor> readColorsPerYearAndInstitutionAndPublisher() {
        return publicationRepository.readColorsPerYearAndInstitutionAndPublisher();
    }

    public List<PublicationColor> readColorsPerYearAndPublicationType() {
        return publicationRepository.readColorsPerYearAndPublicationType();
    }

    public List<PublicationColor> readColorsPerYearAndInstitutionAndPublicationType() {
        return publicationRepository.readColorsPerYearAndInstitutionAndPublicationType();
    }

    public List<PublicationColor> readColorsPerYearAndInstitutionAndLicence() {
        return publicationRepository.readColorsPerYearAndInstitutionAndLicence();
    }

    public List<PublicationColor> readColorsPerYearAndLicence() {
        return publicationRepository.readColorsPerYearAndLicence();
    }

    public long countPublications() {
        return publicationRepository.count();
    }

    public List<PublicationColor> readColorsPerInstitution() {
        return publicationRepository.readColorsPerInstitution();
    }


    public List<Publisher> readPublishersFromPublications() {
        return publicationRepository.readPublishersFromPublications();
    }

    public List<Publisher> readTopPublishers(int maxPublishers) {
        List<Publisher> result = publicationRepository.readPublishersFromPublications();
        result.sort(Comparator.comparing(Publisher::getPublicationCount).reversed());
        int endIndex = maxPublishers;
        if (endIndex > result.size()) {
            endIndex = result.size();
        }
        return result.subList(0, endIndex);
    }

    public void searchForDoiInCrossref() {

        logger.info("Search for DOI in Crossref");
        AtomicInteger count = new AtomicInteger();
        visitAll(0, publication -> {
            count.getAndIncrement();
            // already handled ?
            if (publication.getDoiSource() != Publication.DOI_SOURCE_REPOSITORY) {
                return;
            }

            // Publisher from crossref
            String doi = publication.getDoi();
            if (!hasValue(doi)) {
                String author = null;
                List<Author> authors = publication.getAuthors();
                if (authors != null && !authors.isEmpty()) {
                    author = authors.get(0).lastName;
                }
                CrossrefDoiSearchResult searchResult = crossrefService.searchWork(author, publication.getTitle());
                if (searchResult.ok()) {
                    CrossrefWork bestWork = CrossrefSupport.bestFit(publication, searchResult.works);
                    if (bestWork != null) {
                        logger.debug(String.format("%d\tid=%s\ttitle=%s", count.get(), publication.getId(), publication.getTitle()));
                        logger.debug(String.format("found doi=%s\t%s", bestWork.doi, bestWork.title));
                        // cache the CrossrefWork
                        crossrefService.getCrossrefWork(bestWork.doi);
                        publication.setDoi(bestWork.doi);
                        publication.setDoiSource(Publication.DOI_SOURCE_CROSSREF);
                    } else {
                        if (searchResult.all()) {
                            publication.setDoiSource(Publication.DOI_NOT_FOUND_CROSSREF);
                        } else {
                            logger.debug(String.format("%d\tcrossref search for pubId = %s, title = %s has too many results: %d",
                                    count.get(), publication.getId(), publication.getTitle(), searchResult.totalResults));
                            publication.setDoiSource(Publication.DOI_TOO_MANY_RESULTS);
                        }
                    }
                    save(publication);
                }
            }

        });

    }


    public void checkDoiInPublications() {

        logger.info("Check DOIs in publications");
        AtomicInteger count = new AtomicInteger();
        visitAll(0, publication -> {
            count.getAndIncrement();

            String doi = publication.getDoi();
            // We analyze DOIs in all publications
            if (hasValue(doi)) {

                // did we get a Crossref result?
                CrossrefSource crossrefSource = crossrefService.readCrossrefSourcefromDB(doi);
                if (crossrefSource != null && crossrefSource.getStatus().ok()) {
                    // doi was found in Crossref, so no need for further check
                    return;
                }

                String newDoi = correctAndCheckDoi(doi);
                if (newDoi == null) {
                    if (doi.contains(" ")) {
                        logger.warn(String.format("Doi for publication %s contains a space\t'%s'", publication.getId(), doi));
                    }
                }
                if (newDoi != null && !Objects.equals(newDoi, doi)) {
                    logger.info(String.format("New doi for publication %6d\t%s\t'%s'\t('%s')", count.get(), publication.getId(), newDoi, doi));
                    if (containsReservedCharacter(newDoi)) {
                        logger.warn(String.format("Doi for publication %s contains reserved Chars\t'%s'", publication.getId(), newDoi));
                    }
                    try {
                        publication.setDoi(newDoi);
                        publication.setDoiSource(DOI_SOURCE_MANUAL);
                        publication.setClassificationStatus(0); // try the classification again
                        save(publication);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    try {
                        if (crossrefSource != null) {
                            crossrefService.deleteCrossrefSourcefromDB(doi);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

        });

        logger.info("Check DOIs in publications end");
    }

    private String correctAndCheckDoi(String doi) {
        if (doi == null || doi.isEmpty()) {
            return null;
        }

        // normalize and check
        doi = parseDoi(doi);
        if (doi == null) {
            return null;
        } else {
            // check the doi
            CrossrefWork crossrefWork = crossrefService.crossrefWork(doi);
            if (crossrefWork.isValid()) {
                // Alright, we have a correct DOI
                return crossrefWork.doi;
            }
        }

        if (doi.endsWith(".")) {
            // Let's try the doi without the .
            String testDoi = doi.substring(0, doi.length() - 1);
            CrossrefWork crossrefWork = crossrefService.crossrefWork(testDoi);
            if (crossrefWork.isValid()) {
                // Alright, we have a correct DOI
                return crossrefWork.doi;
            }
        }

        int posFirstSpace = doi.indexOf(' ');
        if (posFirstSpace == -1) {
            return null;
        }

        // Let's try a DOI without the part after the space.
        String testDoi = doi.substring(0, posFirstSpace);
        CrossrefWork crossrefWork = crossrefService.crossrefWork(testDoi);
        if (crossrefWork.isValid()) {
            // Alright, we have a correct DOI
            return crossrefWork.doi;
        }
        // Let's try a DOI without spaces
        testDoi = doi.replaceAll(" ", "");
        crossrefWork = crossrefService.crossrefWork(testDoi);
        if (crossrefWork.isValid()) {
            // Alright, we have a correct DOI
            return crossrefWork.doi;
        }

        return null;
    }

}
