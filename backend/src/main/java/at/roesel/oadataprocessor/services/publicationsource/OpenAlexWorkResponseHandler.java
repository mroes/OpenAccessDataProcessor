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

import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.persistance.conversion.ObjectConverter;
import at.roesel.oadataprocessor.services.common.FetchResult;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;

import java.util.List;
import java.util.UUID;

import static at.roesel.oadataprocessor.config.Identifiers.idOpenAlex;

class OpenAlexWorkResponseHandler implements ResultResponseHandler<OpenAlexWork> {

    private final PublicationSourceService publicationSourceService;

    private final ObjectConverter<OpenAlexWork> recordConverter = new ObjectConverter<>(OpenAlexWork.class);

    private long importedRecords;

    public OpenAlexWorkResponseHandler(PublicationSourceService publicationSourceService) {
        this.publicationSourceService = publicationSourceService;
    }

    @Override
    public boolean handleResponse(List<OpenAlexWork> records) {
        for (OpenAlexWork work : records) {
            importedRecords++;
            PublicationSource source = new PublicationSource();
            source.setId(UUID.randomUUID().toString());
            source.setNativeId(String.valueOf(work.id));
            source.setTitle(work.title);
            source.setDoi(work.doi);
            source.setInstitution(idOpenAlex);
            source.setRecord(recordConverter.convertToDatabaseColumn(work));
            publicationSourceService.saveSource(source);
        }
        return false;
    }

    @Override
    public FetchResult summary() {
        return new FetchResult(importedRecords, 0, 0, 0, 0);
    }

}
