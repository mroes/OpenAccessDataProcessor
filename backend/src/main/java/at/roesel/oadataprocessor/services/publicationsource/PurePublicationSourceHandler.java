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
import at.roesel.oadataprocessor.persistance.conversion.PureResearchOutputConverter;
import at.roesel.oadataprocessor.services.common.FetchResult;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import at.roesel.oadataprocessor.services.pure.model.PureResearchOutput;
import at.roesel.oadataprocessor.services.pure.model.PureWorkflow;
import at.roesel.oadataprocessor.support.DoiSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static at.roesel.oadataprocessor.config.Identifiers.idFhOö;
import static at.roesel.oadataprocessor.config.Identifiers.idTuGraz;
import static at.roesel.oadataprocessor.services.publicationsource.SourceTypes.pure;
import static java.lang.String.format;

class PurePublicationSourceHandler implements ResultResponseHandler<PureResearchOutput> {

    private final Logger logger = LoggerFactory.getLogger(PurePublicationSourceHandler.class);

    private final PublicationSourceService publicationSourceService;

    private final String institutionId;

    private final FetchResult fetchResult;

    public PurePublicationSourceHandler(PublicationSourceService publicationSourceService, String institutionId) {
        this.publicationSourceService = publicationSourceService;
        this.institutionId = institutionId;
        this.fetchResult = new FetchResult();
    }

    private final PureResearchOutputConverter recordConverter = new PureResearchOutputConverter();

    @Override
    public boolean handleResponse(List<PureResearchOutput> records) {
        for (PureResearchOutput researchOutput : records) {
            if (isExcluded(researchOutput)) {
                continue;
            }

            fetchResult.incImportedRecords();
            PublicationSource source = new PublicationSource();
            source.setId(UUID.randomUUID().toString());
            source.setNativeId(String.valueOf(researchOutput.pureId));
            source.setTitle(researchOutput.resolvedTitle());
            source.setYear(researchOutput.resolvedPublicationYear());
            source.setDoi(DoiSupport.parseDoi(researchOutput.resolvedDoi()));
            String pubType = researchOutput.publicationType();
            source.setPubtype(pubType);
            source.setInstitution(institutionId);
            source.setRecord(recordConverter.convertToDatabaseColumn(researchOutput));
            source.setDataType(pure);
            if (!researchOutput.isAffiliated()) {
                source.setAffiliated(PublicationSource.AFFILIATED_NO);
            }
            // If no DOI was found, examine the entire record for a DOI
            if (source.getDoi() == null) {
                int idx = source.getRecord().indexOf("doi");
                if (idx > -1) {
                    int length = source.getRecord().length();
                    if (idx + 3 < length) {
                        Character nextChar = source.getRecord().charAt(idx + 3);
                        List<Character> chars = Arrays.asList(' ', ':', '.');
                        if (chars.contains(nextChar)) {
                            try {
                                String text = source.getRecord().substring(idx, Math.min(idx + 100, length - 1));
                                logger.info(String.format("possible doi for source id = %s (inst=%s, id=%s) in record at pos %d : %s",
                                        source.getId(), source.getInstitution(), source.getNativeId(), idx, text));
                            } catch (Exception e) {
                                logger.error(format("doi search error: idx: %d, length: %d", idx, length), e);
                            }
                        }
                    }
                }
            }
            String doi = source.getDoi();
            if (doi != null && doi.length() > 250) {
                logger.error(format("id %s: doi %s too long", source.getInstitution() + "/" + source.getNativeId(), doi));
                source.setDoi(doi.substring(0, 250));
            }
            publicationSourceService.createOrUpdateSource(source, fetchResult, false);
        }
        return false;
    }

    private boolean isExcluded(PureResearchOutput researchOutput) {
        /*
        // for University Vienna only affiliated records are imported
        if (institutionId.equals(idUniWien)) {
            if (!researchOutput.isAffiliated()) {
                return true;
            }
        }
         */
        // for FH Oberösterreich only records with workflow.workflowStep = "approved" are imported
        if (institutionId.equals(idFhOö)) {
            PureWorkflow workflow = researchOutput.workflow;
            if (workflow != null) {
                String step = workflow.workflowStep;
                if (!step.equals("approved")) {
                    return true;
                }
            }
        }
        // for TU Graz only records with an existing workflow.workflowStep which ist not "entryInProgress" are imported
        else if (institutionId.equals(idTuGraz)) {
            PureWorkflow workflow = researchOutput.workflow;
            if (workflow == null || workflow.workflowStep == null || "entryInProgress".equals(workflow.workflowStep)) {
                return true;
            }
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

    @Override
    public void onError(Throwable exception) {
        // do nothing
    }

}
