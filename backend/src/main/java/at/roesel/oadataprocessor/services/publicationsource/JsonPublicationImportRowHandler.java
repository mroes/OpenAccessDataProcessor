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
import at.roesel.oadataprocessor.model.json.JsonPublication;
import at.roesel.oadataprocessor.persistance.conversion.ObjectConverter;
import at.roesel.oadataprocessor.services.common.FetchResult;
import at.roesel.oadataprocessor.services.common.FieldAccessor;
import at.roesel.oadataprocessor.services.common.RowImportHandler;

import java.util.UUID;

import static at.roesel.oadataprocessor.services.publicationsource.SourceTypes.json;

/*
 * Import of publications via JSON
 * */
public abstract class JsonPublicationImportRowHandler implements RowImportHandler {

//    private final Logger logger = LoggerFactory.getLogger(JsonPublicationImportRowHandler.class);

    private final ObjectConverter<JsonPublication> recordConverter = new ObjectConverter<>(JsonPublication.class);

    private final PublicationSourceService publicationSourceService;
    protected final String institution;
    private final FetchResult fetchResult;
    private boolean tryRun; // if true, don't write the imported publications into the database

    public JsonPublicationImportRowHandler(String institution, PublicationSourceService publicationSourceService) {
        this.institution = institution;
        this.publicationSourceService = publicationSourceService;
        fetchResult = new FetchResult();
    }

    public boolean isTryRun() {
        return tryRun;
    }

    public void setTryRun(boolean tryRun) {
        this.tryRun = tryRun;
    }

    @Override
    public final void handle(FieldAccessor fieldAccessor) {
        fetchResult.incImportedRecords();

        PublicationSource source = new PublicationSource();
        source.setId(UUID.randomUUID().toString());
        source.setInstitution(institution);
        source.setDataType(json);

        JsonPublication publication = new JsonPublication();
        boolean result = mapRecordToSource(fieldAccessor, source, publication);
        if (result) {
            source.setRecord(recordConverter.convertToDatabaseColumn(publication));
            publicationSourceService.createOrUpdateSource(source, fetchResult, isTryRun());
            // if tryRun store the publication in fetchResult
            if (isTryRun()) {
                fetchResult.addPublicationsSources(source);
            }
        } else {
            fetchResult.incIgnoredRecords();
        }
    }

    /*
     * @return true, if the record shall be imported
     */
    protected abstract boolean mapRecordToSource(FieldAccessor fieldAccessor, PublicationSource source, JsonPublication publication);

    public FetchResult getFetchResult() {
        return fetchResult;
    }
}
