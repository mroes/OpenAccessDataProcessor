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

package at.roesel.oadataprocessor.components.openapi;

import at.roesel.oadataprocessor.model.PublicationFlat;
import at.roesel.oadataprocessor.model.SourceReferenceFlat;
import at.roesel.oadataprocessor.openapi.model.*;

import java.util.ArrayList;
import java.util.List;

import static at.roesel.common.StringSupport.hasValue;

public class PublicationConverter {

    private final static String baseCoarUrl = "http://purl.org/coar/resource_type/";
    private final static String includeAuthor = "author";

    private boolean includeAuthors = false;

    public PublicationConverter(List<String> include) {
        if (include != null && !include.isEmpty()) {
            for (String s : include) {
                if (s.equals(includeAuthor)) {
                    includeAuthors = true;
                    break;
                }
            }
        }
    }

    public PublicationConverter() {
        this(null);
    }

    public PublicationConverter(boolean includeAuthors) {
        this.includeAuthors = includeAuthors;
    }

    public Publication from(PublicationFlat publication) {
        Publication jsonPub = new Publication();
        jsonPub.setId((publication.getId()));
        jsonPub.setDoi(publication.getDoi());
        jsonPub.setTitle(publication.getTitle());
        jsonPub.setYear(publication.getYear());

        PublicationType type = new PublicationType();
        type.setName(publication.getType().getName());
        String coarId = publication.getType().getCoarId();
        type.setCoarid(addBaseCoarUrl(coarId));
        jsonPub.setType(type);

        jsonPub.setOaColor(publication.getColor());
        jsonPub.setUpwColor(publication.getColorUpw());

        jsonPub.setPublisher(toJsonPublisher(publication.getPublisher()));
        jsonPub.setMainPublisher(toJsonPublisher(publication.getMainPublisher()));

        if (publication.getJournal() != null && hasValue(publication.getJournal().getId())) {
            Journal journal = new Journal();
            journal.setTitle(publication.getJournal().getTitle());
            journal.setWikidataId(publication.getJournal().getWikidataId());
            journal.setIssn(new ArrayList<>(publication.getJournal().getIssn()));
            jsonPub.setJournal(journal);
        }

        Licence licence = new Licence();
        licence.setLicence(publication.getLicence().getLicence());
        licence.setUrl(publication.getLicence().getUrl());
        licence.setSource(publication.getLicence().getSource());
        jsonPub.setLicence(licence);

        Costs costs = new Costs();
        costs.setAmount(publication.getCosts().getAmount());
        costs.setCurrency(publication.getCosts().getCurrency());
        costs.setSource(publication.getCosts().getSource());
        jsonPub.setCosts(costs);

        jsonPub.setVersion(publication.getVersion());
        jsonPub.setPlace(publication.getOaPlace());
        jsonPub.setRepositoryLink(publication.getOaVersionLink());

        List<SourceRef> refs = new ArrayList<>();
        for (SourceReferenceFlat ref : publication.getSources()) {
            SourceRef sourceRef = new SourceRef();
            sourceRef.setInstitutionId(ref.institutionId);
            sourceRef.setNativeId(ref.nativeId);
            sourceRef.setCorresponding(ref.corr);
            refs.add(sourceRef);
        }
        jsonPub.setSourceRefs(refs);

        if (includeAuthors()) {
            List<Author> authors = new ArrayList<>();
            jsonPub.setAuthor(authors);
            for (PublicationFlat.Author author : publication.getAuthors()) {
                Author jAuthor = new Author();
                jAuthor.setFamily(author.getLastName());
                jAuthor.setGiven(author.getFirstName());
                jAuthor.setCorresponding(author.isCorr());
                authors.add(jAuthor);
            }
        }
        return jsonPub;
    }

    private Publisher toJsonPublisher(PublicationFlat.Publisher publisher) {
        Publisher jsonPublisher = null;
        if (publisher != null && hasValue(publisher.getId())) {
            jsonPublisher = new Publisher();
            jsonPublisher.setName(publisher.getName());
            jsonPublisher.setWikidataId(publisher.getWikidataId());
        }
        return jsonPublisher;
    }

    private boolean includeAuthors() {
        return includeAuthors;
    }

    public static String addBaseCoarUrl(String coarId) {
        // internally the coarId is stored without the url part, but we deliver the whole coar url
        if (coarId != null && !coarId.isEmpty()) {
            // add the url
            coarId = baseCoarUrl + coarId;
        }
        return coarId;
    }

}
