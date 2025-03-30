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

package at.roesel.oadataprocessor.services.publicationsource.uibk;

import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.json.JsonAuthor;
import at.roesel.oadataprocessor.model.json.JsonJournal;
import at.roesel.oadataprocessor.model.json.JsonPublication;
import at.roesel.oadataprocessor.model.json.JsonPublisher;
import at.roesel.oadataprocessor.openapi.model.Author;
import at.roesel.oadataprocessor.openapi.model.Publication;
import at.roesel.oadataprocessor.persistance.conversion.ObjectConverter;
import at.roesel.oadataprocessor.services.common.FetchResult;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import at.roesel.oadataprocessor.services.publicationsource.PublicationSourceService;
import at.roesel.oadataprocessor.support.DoiSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.services.publicationsource.SourceTypes.json;
import static at.roesel.oadataprocessor.support.IdentifierSupport.coarPrefix;

public class ApiPublicationImportHandler implements ResultResponseHandler<Publication> {

//    private final Logger logger = LoggerFactory.getLogger(ApiPublicationImportHandler.class);

    private final PublicationSourceService publicationSourceService;
    protected final String institution;

    private final FetchResult fetchResult;

    protected final ObjectConverter<JsonPublication> recordConverter = new ObjectConverter<>(JsonPublication.class);

    public ApiPublicationImportHandler(PublicationSourceService publicationSourceService, String institution) {
        this.institution = institution;
        this.publicationSourceService = publicationSourceService;
        fetchResult = new FetchResult();
    }

    @Override
    public boolean handleResponse(List<Publication> records) {
        for (Publication record : records) {
            handleResponse(record);
        }
        return false; // don't stop handling
    }

    @Override
    public boolean handleResponse(Publication record) {
        fetchResult.incImportedRecords();

        String nativeId = record.getId();
        if (!hasValue(nativeId)) {
            return false;
        }

        JsonPublication jsonPublication = createFrom(record);

        PublicationSource source = new PublicationSource();
        source.setId(UUID.randomUUID().toString());
        source.setInstitution(institution);
        source.setDataType(json);
        source.setNativeId(nativeId);
        source.setRecord(recordConverter.convertToDatabaseColumn(jsonPublication));

        source.setDoi(jsonPublication.getDoi());
        source.setTitle(jsonPublication.getTitle());
        source.setYear(jsonPublication.getYear());
        source.setPubtype(jsonPublication.getType());


        publicationSourceService.createOrUpdateSource(source, fetchResult, false);
        return false;
    }

    private JsonPublication createFrom(Publication publication) {
        JsonPublication jsonPublication = new JsonPublication();

        if (publication.getDoi() != null) {
            jsonPublication.setDoi(DoiSupport.parseDoi(publication.getDoi()));
        }
        jsonPublication.setTitle(publication.getTitle());
        jsonPublication.setYear(publication.getYear());

        if (publication.getType() != null) {
            String coarId = publication.getType().getCoarid();
            if (hasValue(coarId)) {
                jsonPublication.setType(coarPrefix + coarId);
            } else {
                jsonPublication.setType(publication.getType().getName());
            }

        }

        if (publication.getPublisher() != null) {
            JsonPublisher jsonPublisher = new JsonPublisher();
            jsonPublisher.setName(publication.getPublisher().getName());
            jsonPublication.setPublisher(jsonPublisher);
        }

        if (publication.getJournal() != null) {
            JsonJournal jsonJournal = new JsonJournal();
            jsonJournal.setTitle(publication.getJournal().getTitle());
            if (publication.getJournal().getIssn() != null) {
                for (String issn : publication.getJournal().getIssn()) {
                    jsonJournal.addIssn(issn);
                }
            }
            if (publication.getJournal().getEissn() != null) {
                jsonJournal.addIssn(publication.getJournal().getEissn());
            }
            jsonPublication.setJournal(jsonJournal);
        }

        if (publication.getAuthor() != null) {
            List<JsonAuthor> jsonAuthors = new ArrayList<>();
            for (Author author : publication.getAuthor()) {
                JsonAuthor jsonAuthor = new JsonAuthor();
                jsonAuthor.setFamily(author.getFamily());
                jsonAuthor.setGiven(author.getGiven());
                jsonAuthors.add(jsonAuthor);
            }
            if (!jsonAuthors.isEmpty()) {
                jsonPublication.setAuthors(jsonAuthors);
            }
        }

        return jsonPublication;
    }


    @Override
    public FetchResult summary() {
        return fetchResult;
    }

}
