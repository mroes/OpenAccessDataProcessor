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

import at.roesel.oadataprocessor.model.Institution;
import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.PublicationProps;
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.model.unpaywall.UnpaywallResource;
import at.roesel.oadataprocessor.services.crossref.CrossrefService;
import at.roesel.oadataprocessor.services.openalex.OpenAlexCorrespondingAuthorProvider;
import at.roesel.oadataprocessor.services.openalex.OpenAlexService;
import at.roesel.oadataprocessor.services.unpaywall.UnpaywallService;
import at.roesel.oadataprocessor.support.AuthorComparator;
import at.roesel.oadataprocessor.support.PublicationSupport;

import java.util.HashSet;
import java.util.Map;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.support.PublicationSupport.jsonFrom;

public class PublicationDataEnhancer {

    private final CrossrefService crossrefService;

    private final UnpaywallService unpaywallService;

    private final OpenAlexService openAlexService;

    private final OpenAlexCorrespondingAuthorProvider correspondingAuthorProvider;

    public PublicationDataEnhancer(CrossrefService crossrefService, UnpaywallService unpaywallService, OpenAlexService openAlexService,
                                   Map<String, Institution> institutionMap) {
        this.crossrefService = crossrefService;
        this.unpaywallService = unpaywallService;
        this.openAlexService = openAlexService;

        correspondingAuthorProvider = new OpenAlexCorrespondingAuthorProvider(institutionMap, new AuthorComparator());
    }

    /*
     * @return true if data was added
     */
    public boolean enrichDataInPublication(Publication publication) {

        String doi = publication.getDoi();
        if (!hasValue(doi)) {
            return false;
        }

        boolean added = false;

        // fetch data from Crossref
        CrossrefWork crossrefWork = crossrefService.getCrossrefWork(doi);

        // fetch data from Unpaywall
        UnpaywallResource unpaywallResource = unpaywallService.getResource(doi);

        if (unpaywallResource != null) {
            // add some missing data to the publication from Unpaywall
            if (publication.getAuthors() == null) {
                publication.setAuthors(unpaywallResource.resolvedAuthors());
                added = true;
            }
        }

        if (crossrefWork != null) {
            // add some missing data to the publication from Crossref
            if (!hasValue(publication.getTitle())) {
                publication.setTitleAndHash(crossrefWork.firstTitle());
                added = true;
            }
            int year = crossrefWork.publishedYear();
            if (year != 0) {
                publication.setYear(year);
                added = true;
            }
            if (!hasValue(publication.getPublisher())) {
                publication.setPublisher(crossrefWork.publisher);
                added = true;
            }

            if (publication.getAuthors() == null) {
                publication.setAuthors(crossrefWork.resolvedAuthors());
                added = true;
            }

            PublicationProps props = PublicationSupport.propsFrom(publication);
            if (hasValue(crossrefWork.publisher)) {
                props.publisherCr = crossrefWork.publisher;
                added = true;
            }
            if (!crossrefWork.getIssns().isEmpty()) {
                props.issnCr = new HashSet<>(crossrefWork.getIssns());
                added = true;
            }
            if (unpaywallResource != null) {
                if (hasValue(unpaywallResource.journal_issn_l)) {
                    props.issnL = unpaywallResource.journal_issn_l;
                    added = true;
                }
            }

            if (setCorrespondingAuthorAndInstitution(publication)) {
                added = true;
            }

            publication.setProps(jsonFrom(props));
        }

        return added;
    }

    /*
     * @return true if data was added
     */
    public boolean setCorrespondingAuthorAndInstitution(Publication publication) {
        String doi = publication.getDoi();
        // query OpenAlex for data about corresponding authors
        if (hasValue(doi)) {
            OpenAlexWork work = openAlexService.getWork(doi);
            if (work != null) {
                boolean changed = correspondingAuthorProvider.setCorrespondingAuthorAndAffiliation(publication, work);
                if (changed) {
                    return true;
                }
            }
        }
        return false;
    }

}