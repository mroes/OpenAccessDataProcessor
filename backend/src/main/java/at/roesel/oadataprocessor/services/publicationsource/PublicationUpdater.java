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

package at.roesel.oadataprocessor.services.publicationsource;

import at.roesel.common.CollectionSupport;
import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.PublicationType;
import at.roesel.oadataprocessor.model.SourceReference;
import at.roesel.oadataprocessor.support.AuthorComparator;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.model.Publication.*;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;

public class PublicationUpdater {

    private final Logger logger = LoggerFactory.getLogger(PublicationUpdater.class);

    private final AuthorComparator authorComparator = new AuthorComparator();

    private final Map<Integer, PublicationType> publicationTypeMap;

    public PublicationUpdater(Map<Integer, PublicationType> publicationTypeMap) {
        this.publicationTypeMap = publicationTypeMap;
    }

    // Updating the data in the existing publication
    public UpdateResult update(Publication existingPublication, Publication newPublication) {

        UpdateResult result = new UpdateResult(existingPublication);

        if (existingPublication.updateSources(newPublication.getSources())) {
            result.setChanged(true);
        }

        boolean singleSource = existingPublication.getSources() == null || existingPublication.getSources().size() < 2;

        SourceReference sourceReference = sourceReference(newPublication);
        String sourceId = sourceReference != null ? sourceReference.getSourceId() : "?";

        // year
        if (newPublication.getYear() != 0) {
            if (existingPublication.getYear() == 0) {
                result.setChanged(true);
                existingPublication.setYear(newPublication.getYear());
            } else {
                if (newPublication.getYear() != existingPublication.getYear()) {
                    if (singleSource) {
                        result.setChanged(true);
                        existingPublication.setYear(newPublication.getYear());
                    } else {
                        logger.warn(String.format("Existing publication id=%s has year=%d, new publication sourceId=%s has year=%d",
                                existingPublication.getId(), existingPublication.getYear(), sourceId, newPublication.getYear()));
                    }
                }
            }
        }

        // doi
        if (hasValue(newPublication.getDoi())) {
            if (!hasValue(existingPublication.getDoi())) {
                result.setChanged(true);
                result.addedDoi = true;
                existingPublication.setDoi(newPublication.getDoi());
                existingPublication.setDoiSource(DOI_SOURCE_REPOSITORY);
                existingPublication.setClassificationStatus(CLASSIFICATION_TODO);
            } else if (existingPublication.getDoiSource() != DOI_SOURCE_MANUAL){
                // Update only if the DOI was not entered manually
                if (newPublication.getDoi().compareToIgnoreCase(existingPublication.getDoi()) != 0) {
                    if (singleSource) {
                        result.setChanged(true);
                        result.addedDoi = true;
                        existingPublication.setDoi(newPublication.getDoi());
                        existingPublication.setClassificationStatus(CLASSIFICATION_TODO);
                    } else {
                        logger.warn(String.format("Existing publication id=%s has doi=%s, new publication sourceId=%s has doi=%s",
                                existingPublication.getId(), existingPublication.getDoi(), sourceId, newPublication.getDoi()));
                    }
                }
            } else {
                logger.warn(String.format("Existing publication id=%s has manual doi=%s, new publication sourceId=%s has doi=%s",
                        existingPublication.getId(), existingPublication.getDoi(), sourceId, newPublication.getDoi()));

            }
        }

        // normalized publication type
        // we need the publication type later for PublicationFilter.test
        PublicationType existingType = publicationTypeMap.get(existingPublication.getPubtypeId());
        existingPublication.setPublicationType(existingType);

        if (newPublication.getPubtypeId() != 0) {
            if (existingPublication.getPubtypeId() == 0) {
                result.setChanged(true);
                existingPublication.setPubtypeId(newPublication.getPubtypeId());
                existingPublication.setPublicationType(newPublication.getPublicationType());
            } else {
                if (newPublication.getPubtypeId() != existingPublication.getPubtypeId()) {
                    if (singleSource) {
                        result.setChanged(true);
                        existingPublication.setPubtypeId(newPublication.getPubtypeId());
                        existingPublication.setPublicationType(newPublication.getPublicationType());
                    } else {
                        logger.warn(String.format("Existing publication id=%s has pubtypeId=%d, new publication sourceId=%s has pubtypeId=%d",
                                existingPublication.getId(), existingPublication.getPubtypeId(), sourceId, newPublication.getPubtypeId()));
                        // Only apply the changed status if it is enabled
                        if (existingType != null && !existingType.isEnabled()) {
                            PublicationType newType = publicationTypeMap.get(newPublication.getPubtypeId());
                            if (newType.isEnabled()) {
                                existingPublication.setPubtypeId(newPublication.getPubtypeId());
                                existingPublication.setPublicationType(newType);
                                result.setChanged(true);
                            }
                        }
                    }
                }
            }
        }

        // publisher
        if (hasValue(newPublication.getPublisher())) {
            if (!hasValue(existingPublication.getPublisher())) {
                result.setChanged(true);
                existingPublication.setPublisher(newPublication.getPublisher());
            } else {
                if (newPublication.getPublisher().compareToIgnoreCase(existingPublication.getPublisher()) != 0) {
                    if (singleSource) {
                        result.setChanged(true);
                        existingPublication.setPublisher(newPublication.getPublisher());
                    } else {
                        logger.warn(String.format("Existing publication id=%s has publisher=%s, new publication sourceId=%s has publisher=%s",
                                existingPublication.getId(), existingPublication.getPublisher(), sourceId, newPublication.getPublisher()));
                    }
                }
            }
        }

        // title
        if (hasValue(newPublication.getTitle())) {
            if (!hasValue(existingPublication.getTitle())) {
                result.setChanged(true);
                existingPublication.setTitleAndHash(newPublication.getTitle());
            } else {
                if (newPublication.getTitle().compareToIgnoreCase(existingPublication.getTitle()) != 0) {
                    if (singleSource) {
                        result.setChanged(true);
                        existingPublication.setTitleAndHash(newPublication.getTitle());
                    } else {
                        int ratio = FuzzySearch.ratio(newPublication.getTitle(), existingPublication.getTitle());
                        if (ratio < 80) {
                            logger.warn(String.format("Existing publication id=%s has title=%s, new publication sourceId=%s has title=%s",
                                    existingPublication.getId(), existingPublication.getTitle(), sourceId, newPublication.getTitle()));
                        }
                    }
                }
            }
        }

        // issn
        if (hasValue(newPublication.getIssn())) {
            if (!hasValue(existingPublication.getIssn())) {
                result.setChanged(true);
                existingPublication.setIssn(newPublication.getIssn());
            } else {
                if (!isEqualCollection(newPublication.getIssn(), existingPublication.getIssn())) {
                    if (existingPublication.addIssn(newPublication.getIssn())) {
                        result.setChanged(true);
                    }
                    if (existingPublication.getIssn().size() > 2) {
                        logger.warn(String.format("Existing publication id=%s has more than 2 issn: %s",
                                existingPublication.getId(), existingPublication.getIssn()));
                    }
                }
            }
        }

        // authors
        boolean sameAuthors = CollectionSupport.isEqualCollection(newPublication.getAuthors(), existingPublication.getAuthors(), authorComparator);
        if (!sameAuthors) {
            if (singleSource) {
                result.setChanged(true);
                existingPublication.setAuthors(newPublication.getAuthors());
            }
        }

        return result;
    }

    private SourceReference sourceReference(Publication publication) {
        if (publication.getSources() != null) {
            return publication.getSources().stream().findFirst().orElse(null);
        }

        return null;
    }

    public static class UpdateResult {

        public Publication publication;
        private boolean changed = false;
        public boolean addedDoi = false;

        public UpdateResult(Publication publication) {
            this.publication = publication;
        }

        public Publication getPublication() {
            return publication;
        }

        public void setChanged(boolean changed) {
            this.changed = changed;
        }

        public boolean isChanged() {
            return changed;
        }

        public boolean isAddedDoi() {
            return addedDoi;
        }
    }
}
