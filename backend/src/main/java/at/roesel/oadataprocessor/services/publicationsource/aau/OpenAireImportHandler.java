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

package at.roesel.oadataprocessor.services.publicationsource.aau;

import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.services.common.FetchResult;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import at.roesel.oadataprocessor.services.publicationsource.PublicationSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static at.roesel.oadataprocessor.services.publicationsource.SourceTypes.json;

public class OpenAireImportHandler implements ResultResponseHandler<PublicationSource> {

    private final Logger logger = LoggerFactory.getLogger(OpenAireImportHandler.class);

    private final PublicationSourceService publicationSourceService;
    protected final String institution;

    private final FetchResult fetchResult;

    public OpenAireImportHandler(PublicationSourceService publicationSourceService, String institution) {
        this.institution = institution;
        this.publicationSourceService = publicationSourceService;
        fetchResult = new FetchResult();
    }

    @Override
    public boolean handleResponse(PublicationSource source) {
        fetchResult.incImportedRecords();

        source.setId(UUID.randomUUID().toString());
        source.setInstitution(institution);
        source.setDataType(json);

        publicationSourceService.createOrUpdateSource(source, fetchResult, false);
        return false;
    }

    @Override
    public FetchResult summary() {
        return fetchResult;
    }

}
