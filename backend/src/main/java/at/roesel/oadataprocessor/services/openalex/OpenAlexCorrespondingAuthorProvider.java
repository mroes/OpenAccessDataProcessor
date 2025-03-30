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

package at.roesel.oadataprocessor.services.openalex;

import at.roesel.common.CollectionSupport;
import at.roesel.oadataprocessor.model.*;
import at.roesel.oadataprocessor.model.openalex.OpenAlexAuthorship;
import at.roesel.oadataprocessor.model.openalex.OpenAlexInstitution;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.support.AuthorComparator;
import at.roesel.oadataprocessor.support.PublicationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.model.SourceReference.CORRESPONDING;
import static at.roesel.oadataprocessor.services.openalex.OpenAlexSupport.authorFromOpenAlex;
import static at.roesel.oadataprocessor.support.PublicationSupport.propsFrom;

public class OpenAlexCorrespondingAuthorProvider {

    private final Logger logger = LoggerFactory.getLogger(OpenAlexCorrespondingAuthorProvider.class);
    private final Map<String, Institution> institutionMap;
    private final AuthorComparator authorComparator;

    public OpenAlexCorrespondingAuthorProvider(Map<String, Institution> institutionMap, AuthorComparator authorComparator) {
        this.institutionMap = institutionMap;
        this.authorComparator = authorComparator;
    }

    public boolean setCorrespondingAuthorAndAffiliation(Publication publication, OpenAlexWork work) {
        boolean changed = false;
        List<OpenAlexAuthorship> authorships = work.correspondingAuthorships();
        Set<String> affiliations = new HashSet<>();
        if (!authorships.isEmpty()) {
            for (OpenAlexAuthorship authorship : authorships) {
                // add affiliations of this author
                for (OpenAlexInstitution institution : authorship.institutions) {
                    if (hasValue(institution.ror)) {
                        affiliations.add(institution.ror);
                    } else {
                        logger.warn(String.format("institution %s from OpenAlex authorship has no rorId", institution.display_name));
                    }
                }
                Author searchAuthor = authorFromOpenAlex(authorship);
                Author firstAuthor = null;
                int matches = 0;
                for (Author author : publication.getAuthors()) {
                    if (authorComparator.compare(author, searchAuthor) == 0) {
                        matches++;
                        if (firstAuthor == null) {
                            firstAuthor = author;
                        }
                    }
                }
                if (firstAuthor != null) {
                    if (matches == 1) {
                        logger.debug(String.format("found corr author in %s, %s for OpenAlex %s ", publication.getId(), firstAuthor.toString(), searchAuthor.toString()));
                        if (!firstAuthor.isCorresponding) {
                            firstAuthor.setCorresponding(true);
                            changed = true;
                        }
                    } else {
                        logger.warn(String.format("found %d multiple authors in %s, for OpenAlex %s ", matches, publication.getId(), searchAuthor.toString()));
                    }
                } else {
                    logger.debug(String.format("no matching author in %s for OpenAlex %s ", publication.getId(), searchAuthor.toString()));
                }
            }
        }
        if (!affiliations.isEmpty()) {
            PublicationProps props = propsFrom(publication);
            // update affiliations
            if (!CollectionSupport.isEqualCollection(props.affiliations, affiliations, String::compareTo)) {
                props.affiliations = affiliations;
                publication.setProps(PublicationSupport.jsonFrom(props));
                changed = true;
            }
            if (updateSourceRefs(publication, affiliations)) {
                changed = true;
            }
        }

        return changed;
    }

    private boolean updateSourceRefs(Publication publication, Set<String> affiliations) {
        boolean changed = false;
        Set<SourceReference> sources = publication.getSources();
        if (sources.isEmpty()) {
            logger.error(String.format("found publication, id = %s with no source references",
                    publication.getId()));
        }

        for (String instId : affiliations) {
            // is the institution one of the considered institutions?
            if (!institutionMap.containsKey(instId)) {
                continue;
            }
            // is the institution already existing in the publication?
            Optional<SourceReference> sourceRefOpt = sources.stream().filter(src -> src.getInstitutionId().equals(instId)).findFirst();
            if (sourceRefOpt.isPresent()) {
                // flag as affiliation with corresponding author
                SourceReference sourceReference = sourceRefOpt.get();
                if (sourceReference.getCorresponding() != CORRESPONDING) {
                    sourceReference.setCorresponding(CORRESPONDING);
                    changed = true;
                }
                continue;
            }
            // institution is one of the considered institutions, but not existing in the publication
            // add institution
            logger.debug(String.format("pubId = %s, adding institution %s from OpenAlex to SourceRef", publication.getId(), instId));
            SourceReference newSource = new SourceReference();
            newSource.setPublicationId(publication.getId());
            // special sourceId for OpenAlex
            newSource.setSourceId(OpenAlexSourceRefId.PREFIX + UUID.randomUUID());
            newSource.setInstitutionId(instId);
            newSource.setCorresponding(CORRESPONDING);
            sources.add(newSource);
            changed = true;
        }

        return changed;
    }
}
