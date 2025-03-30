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
import at.roesel.oadataprocessor.model.oaipmh.jabx.RecordType;
import at.roesel.oadataprocessor.services.common.FetchResult;
import at.roesel.oadataprocessor.services.oaipmh.AbstractOaiPmhResultResponseHandler;
import at.roesel.oadataprocessor.services.oaipmh.PublicationCreatorOaiPmh;
import at.roesel.oadataprocessor.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static at.roesel.oadataprocessor.config.Identifiers.*;

public class OaiPmhPublicationSourceHandler extends AbstractOaiPmhResultResponseHandler {

    private final Logger logger = LoggerFactory.getLogger(OaiPmhPublicationSourceHandler.class);

    private final PublicationSourceService publicationSourceService;
    private final String institutionId;
    private final PublicationCreatorOaiPmh publicationCreator;

    private final FetchResult fetchResult;

    private boolean tryRun; // if true, don't write the imported publications into the database

    public OaiPmhPublicationSourceHandler(PublicationSourceService publicationSourceService, String institutionId) {
        this.institutionId = institutionId;
        this.publicationSourceService = publicationSourceService;

        this.fetchResult = new FetchResult();

        DataExtractor dataExtractor;
        switch (institutionId) {
            case idTuWien : dataExtractor = new DataExtractorTuWien(); break;
            case idIsta: dataExtractor = new DataExtractorIst(); break;
            case idIiasa: dataExtractor = new DataExtractorIiasa(); break;
            case idIhs: dataExtractor = new DataExtractorIhs(); break;
            case idFhb: dataExtractor = new DataExtractorFhb(); break;
            case idFhCampusWien: dataExtractor = new DataExtractorFhCampusWien(); break;
            case idFhCampus2: dataExtractor = new DataExtractorFhCampus2(); break;
            case idMoz: dataExtractor = new DataExtractorMoz(); break;
            default:
                dataExtractor = null;
        }
        publicationCreator = new PublicationCreatorOaiPmh(dataExtractor);
    }

    public boolean isTryRun() {
        return tryRun;
    }

    public void setTryRun(boolean tryRun) {
        this.tryRun = tryRun;
    }

    @Override
    protected boolean handleRecordType(RecordType recordType) {

        fetchResult.incImportedRecords();

        PublicationSource source = publicationCreator.sourceFrom(recordType);
        if (source != null) {
            source.setInstitution(institutionId);
            publicationSourceService.createOrUpdateSource(source, fetchResult, isTryRun());
        }
        return false;
    }

    @Override
    public void onFinished() {
        logger.info(fetchResult.asText());
    }

    @Override
    public FetchResult summary() {
        return fetchResult;
    }

}
