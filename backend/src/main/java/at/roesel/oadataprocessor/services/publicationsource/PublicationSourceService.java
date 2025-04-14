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

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.model.*;
import at.roesel.oadataprocessor.model.json.JsonAuthor;
import at.roesel.oadataprocessor.model.json.JsonPublication;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.persistance.PublicationSourceRepository;
import at.roesel.oadataprocessor.persistance.SourceLogRepository;
import at.roesel.oadataprocessor.persistance.conversion.ObjectConverter;
import at.roesel.oadataprocessor.services.InstitutionService;
import at.roesel.oadataprocessor.services.PublicationDataEnhancer;
import at.roesel.oadataprocessor.services.PublicationService;
import at.roesel.oadataprocessor.services.PublicationTypeService;
import at.roesel.oadataprocessor.services.common.ClientParameter;
import at.roesel.oadataprocessor.services.common.FetchResult;
import at.roesel.oadataprocessor.services.common.PublicationTypeMapper;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import at.roesel.oadataprocessor.services.impexp.LastImportService;
import at.roesel.oadataprocessor.services.oaipmh.OaiPmhImporter;
import at.roesel.oadataprocessor.services.oaipmh.OaiPmhService;
import at.roesel.oadataprocessor.services.oaipmh.OaipmhRecord;
import at.roesel.oadataprocessor.services.openalex.OpenAlexService;
import at.roesel.oadataprocessor.services.publicationsource.aau.OpenAireImportHandler;
import at.roesel.oadataprocessor.services.publicationsource.aau.OpenAireService;
import at.roesel.oadataprocessor.services.publicationsource.uibk.ApiPublicationImportHandler;
import at.roesel.oadataprocessor.services.publicationsource.uibk.UibkImportService;
import at.roesel.oadataprocessor.services.pure.PureService;
import at.roesel.oadataprocessor.services.pure.model.PureResearchOutput;
import at.roesel.oadataprocessor.services.xls.LargeExcelFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static at.roesel.oadataprocessor.config.Identifiers.idBokuWien;
import static at.roesel.oadataprocessor.config.Identifiers.idTuWien;
import static at.roesel.oadataprocessor.model.Publication.CLASSIFICATION_TODO;
import static at.roesel.oadataprocessor.support.DoiSupport.containsReservedCharacter;
import static at.roesel.oadataprocessor.support.DoiSupport.parseDoi;
import static at.roesel.oadataprocessor.support.FileSupport.isCsv;
import static at.roesel.oadataprocessor.support.FileSupport.isExcel;
import static java.lang.String.format;

@Component
public class PublicationSourceService {

    private final static String repositoryPure = "Pure";
    private final static String repositoryOaiPmh = "OaiPmh";
    private final static String repositoryOpenAire = "OpenAire";
    private final static String repositoryUibk = "Uibk";

    private final Logger logger = LoggerFactory.getLogger(PublicationSourceService.class);

    @Autowired
    private PublicationSourceRepository publicationSourceRepository;

    @Autowired
    private SourceLogRepository sourceLogRepository;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private LastImportService lastImportService;

    @Autowired
    private PureService pureService;

    @Autowired
    private OaiPmhService oaiPmhService;

    @Autowired
    private OpenAireService openAireService;

    @Autowired
    private UibkImportService uibkImportService;

    @Autowired
    private OpenAlexService openAlexService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationTypeService publicationTypeService;

    public void saveSource(PublicationSource source) {
        try {
            publicationSourceRepository.save(source);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void createOrUpdateSource(PublicationSource source, FetchResult fetchResult, boolean tryRun) {
        try {
            // Do we already have this publication from the institution?
            PublicationSource existingSource = null;
            try {
                // search for publication bei institution and nativeId
                existingSource = findByInstitutionAndNativeId(source.getInstitution(), source.getNativeId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (existingSource == null) {
                fetchResult.incCreatedRecords();
                if (!tryRun) {
                    saveSource(source);
                }
            } else {
                if (existingSource.isDataEqual(source)) {
                    fetchResult.incUnchangedRecords();
                } else {
                    fetchResult.incModifiedRecords();
                    // take fields id and created from existing source and write the new data
                    source.setId(existingSource.getId());
                    source.setCreated(existingSource.getCreated());
                    if (!tryRun) {
                        saveSource(source);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    PublicationSource findByInstitutionAndNativeId(String institution, String nativeId) {
        return publicationSourceRepository.findByInstitutionAndNativeId(institution, nativeId);
    }

    public List<PublicationSource> findAllByInstitutionAndNativeId(String institution, String nativeId) {
        return publicationSourceRepository.findAllByInstitutionAndNativeId(institution, nativeId);
    }

    public List<PublicationSource> findSourceByPublicationId(String publicationId) {
        return publicationSourceRepository.findSourceByPublicationId(publicationId);
    }


    private SourceLog saveSourceLog(SourceLog entry) {
        if (entry.getComment().length() > 254) {
            entry.setComment(entry.getComment().substring(0, 254));
            logger.error(String.format("InstitutionId = %s, comment too long %s", entry.getInstitutionId(), entry.getComment()));
        }
        return sourceLogRepository.save(entry);
    }

    public void fetchFromActiveInstitutions() {
        List<Institution> institutions = institutionService.findAllActive();
        fetchFromInstitutions(institutions);
    }

    /*
     * Fetch the publications from all institutions that provide web services.
     * Only publication that are new or were changed since the last run are queried
     */
    public void fetchFromInstitutions(List<Institution> institutions) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Institution institution : institutions) {
            RepositoryParams repositoryParams = institution.getRepositoryparams();
            String apiUrl = institution.getRepositoryUrl();
            if (apiUrl == null || apiUrl.isEmpty()) {
                continue;
            }
            LocalDate lastImportDate = lastImportService.loadLastImportDate(institution.getId());
            logger.info(String.format("fetching publications from %s (%s), starting with %s ",
                    institution.getName(), institution.getId(), dateFormat.format(lastImportDate)));
            SourceLog sourceLog = new SourceLog(institution.getId());
            sourceLog.setStartTime(SystemTime.currentTimeMillis());
            try {
                ClientParameter parameter = ClientParameter.of(institution.getRepositoryUrl(), institution.getRepositoryKey());
                parameter.setSetName(repositoryParams.getOaipmhSetName());
                LocalDate modifiedAfter = lastImportDate.minusDays(1);
                String comment;
                switch (institution.getRepository()) {
                    case repositoryOaiPmh: {
                        parameter.setStartDate(modifiedAfter);
                        parameter.setQueryFormat(repositoryParams.getOaipmhMetadataFormat());
                        OaiPmhPublicationSourceHandler responseHandler = new OaiPmhPublicationSourceHandler(this, institution.getId());
                        oaiPmhService.fetchPublications(parameter, responseHandler);
                        comment = responseHandler.summary().asText();
                        break;
                    }
                    case repositoryPure: {
                        ResultResponseHandler<PureResearchOutput> responseHandler = new PurePublicationSourceHandler(this, institution.getId());
                        pureService.fetchPublications(parameter, responseHandler, modifiedAfter);
                        comment = responseHandler.summary().asText();
                        break;
                    }
                    case repositoryOpenAire: {
                        OpenAireImportHandler responseHandler = new OpenAireImportHandler(this, institution.getId());
                        openAireService.fetchPublications(parameter, responseHandler, modifiedAfter);
                        comment = responseHandler.summary().asText();
                        break;
                    }
                    case repositoryUibk: {
                        ApiPublicationImportHandler responseHandler = new ApiPublicationImportHandler(this, institution.getId());
                        uibkImportService.fetchPublications(parameter, repositoryParams.getClientId(), responseHandler, modifiedAfter);
                        comment = responseHandler.summary().asText();
                        break;
                    }
                    default: {
                        comment = "unhandled repository type " + institution.getRepository();
                    }
                }
                sourceLog.setEndTime(SystemTime.currentTimeMillis());
                sourceLog.setComment(comment);
                saveSourceLog(sourceLog);
                // remember the time of the last publication query
                saveLastImportDate(institution);
            } catch (Exception e) {
                // example for an exception: 401: Request not authorized. Provided API key has expired
                sourceLog.setEndTime(SystemTime.currentTimeMillis());
                sourceLog.setComment(e.getMessage());
                sourceLog.setStatus(-1);
                saveSourceLog(sourceLog);
            }
        }
    }

    private void saveLastImportDate(Institution institution) {
        lastImportService.saveLastPublicationImportDate(institution.getId(), LocalDate.now());
    }

    public void fetchFromPure(String institutionId, String apiUrl, String apiKey, String proxyConfig, LocalDate from) {
        ResultResponseHandler<PureResearchOutput> handler = new PurePublicationSourceHandler(this, institutionId);
        pureService.fetchPublications(ClientParameter.of(apiUrl, apiKey, proxyConfig), handler, from);
    }

    public void fetchOaiPmh(String institutionId, String apiUrl, LocalDate from) {
        ClientParameter parameter = ClientParameter.of(apiUrl, null);
        parameter.setStartDate(from);
//        parameter.setQueryFormat(OaiPmhImporter.mdf_oai_dc);
        parameter.setQueryFormat(OaiPmhImporter.mdf_openaire);
        OaiPmhPublicationSourceHandler responseHandler = new OaiPmhPublicationSourceHandler(this, institutionId);
        oaiPmhService.fetchPublications(parameter, responseHandler);
    }

    // for testing
    public void fetchTuWien(String apiUrl) {
        LocalDate from = LocalDate.of(2023, 11, 1); // verursacht Fehler
        ClientParameter parameter = ClientParameter.of(apiUrl, null);
        // for TU Wien we need a set
        parameter.setSetName("publication_output_by_TU_Wien_members");

        parameter.setStartDate(from);
        parameter.setQueryFormat(OaiPmhImporter.mdf_openaire);
        OaiPmhPublicationSourceHandler responseHandler = new OaiPmhPublicationSourceHandler(this, idTuWien);
        responseHandler.setTryRun(true);
        oaiPmhService.fetchPublications(parameter, responseHandler);
    }

    public void fetchFromExcel(String institutionId, String filePath, Integer indexSheet) {
        fetchFromExcel(institutionId, filePath, indexSheet, false);
    }

    public FetchResult fetchFromExcel(String institutionId, String filePath, Integer indexSheet, boolean preview) {

        JsonPublicationImportRowHandler rowHandler;

        if (institutionId.equals(idBokuWien)) {
            rowHandler = new DefaultPublicationRowHandler(institutionId, this) {
                @Override
                protected List<JsonAuthor> parseAuthors(String authorText) {
                    return BokuNameParser.authorsFromString(authorText);
                }
            };
        } else {
            rowHandler = new DefaultPublicationRowHandler(institutionId, this);
        }

        if (preview) {
            rowHandler.setTryRun(true);
        }

        File file = new File(filePath);
        try (InputStream is = Files.newInputStream(file.toPath())) {
            if (isExcel(filePath)) {
                LargeExcelFile excelFile = new LargeExcelFile(rowHandler, true);
                excelFile.setExceptionOnMissingColumn(false);
                if (indexSheet == null) {
                    excelFile.readFromStream(is);
                } else {
                    excelFile.readFromStream(is, indexSheet);
                }
            } else if (isCsv(filePath)) {
                DefaultPublicationCsvImporter importer = new DefaultPublicationCsvImporter();
                importer.readFromStream(is, rowHandler);
            } else {
                throw new RuntimeException("Unsupported file type: " + filePath);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        FetchResult fetchResult = rowHandler.getFetchResult();
        return fetchResult;
    }


    public void fetchOpenAlexPublications() {
        String filter = "";
        ResultResponseHandler<OpenAlexWork> handler = new OpenAlexWorkResponseHandler(this);
        openAlexService.fetchWorks(filter, handler);
    }

    public PublicationSourceRecord convertSourceRecord(PublicationSource source) {
        PublicationSourceRecord result;
        switch (source.getDataType()) {
            case SourceTypes.pure: {
                ObjectConverter<PureResearchOutput> converter = new ObjectConverter<>(PureResearchOutput.class);
                result = converter.convertToEntityAttribute(source.getRecord());
                break;
            }
            case SourceTypes.oai_dc: {
                ObjectConverter<OaipmhRecord> converter = new ObjectConverter<>(OaipmhRecord.class);
                result = converter.convertToEntityAttribute(source.getRecord());
                break;
            }
            case SourceTypes.json: {
                ObjectConverter<JsonPublication> converter = new ObjectConverter<>(JsonPublication.class);
                result = converter.convertToEntityAttribute(source.getRecord());
                break;
            }
            default:
                result = null;
        }
        return result;
    }

    /*
     * read imported publications from table source
     * if a publication can be identified in table publication, the publication is updated, otherwise it is created
     *
     * @param institution
     *      if null, all publications are read, otherwise only the publications for the given institution
     */
    public void updatePublications(String institution) {

        PublicationTypeMapper publicationTypeMapper = publicationTypeService.buildMapper(null);

        PublicationCreator publicationCreator = new PublicationCreator(publicationTypeMapper);
        PublicationUpdater publicationUpdater = new PublicationUpdater(publicationTypeService.buildTable());
        PublicationFilter publicationFilter = new PublicationFilterDefault();

        PublicationDataEnhancer publicationDataEnhancer = publicationService.createPublicationDataEnhancer(institutionService.institutionMap());

        PageRequest pageRequest = PageRequest.of(0, 500);
        int count = 0;
        int ignored = 0;
        int created = 0;
        int updated = 0;
        while (true) {
            logger.debug("updatePublications(): reading sources from database");
            long startTime = SystemTime.currentTimeMillis();
            Page<PublicationSource> sources;
            if (institution != null) {
                sources = publicationSourceRepository.findAllByInstitutionAndStatus(pageRequest, institution, PublicationSourceStatus.NEW_OR_CHANGED);
            } else {
                sources = publicationSourceRepository.findAllByStatus(pageRequest, PublicationSourceStatus.NEW_OR_CHANGED);
            }
            logger.debug(String.format("loading time: %d ms", SystemTime.currentTimeMillis() - startTime));
            if (sources.getContent().isEmpty()) {
                break;
            }
            // status is updated in the loop below, so we must not call next(), we have to fetch the pages always from the beginning
            // pageRequest = pageRequest.next();

            for (PublicationSource source : sources.getContent()) {
                count++;
                logger.debug(String.format("Handle source %6d\t%s\t%s", count, source.getId(), source.getTitle()));
                Publication publication = publicationCreator.from(source);
                if (publication == null) {
                    logger.warn("could not create publication from source, id = {}, format = {}", source.getId(), source.getDataType());
                    continue;
                }
                PublicationSourceStatus newSourceStatus = PublicationSourceStatus.IMPORTED;
                boolean activePublication = publicationFilter.test(publication);
                if (!activePublication) {
                    ignored++;
                    newSourceStatus = PublicationSourceStatus.IGNORED;
                }
                try {
                    // check if the publication is already existing from a previous import or from another institution
                    long findStartTime = SystemTime.currentTimeMillis();
                    // if the publication was already imported from this source, we should find it with the sourceId
                    Publication existingPublication = publicationService.findPublicationBySourceId(source.getId());
                    if (existingPublication == null) {
                        // search publication with other criteria (doi, title)
                        existingPublication = publicationService.findPublication(publication);
                    }

                    logger.debug(String.format("find publication: %d ms", SystemTime.currentTimeMillis() - findStartTime));
                    if (existingPublication == null) {
                        if (activePublication) {
                            created++;
                            long addSourceStartTime = SystemTime.currentTimeMillis();
                            publicationDataEnhancer.enrichDataInPublication(publication);
                            logger.debug(String.format("enrich data in publication: %d ms", SystemTime.currentTimeMillis() - addSourceStartTime));
                            long saveStartTime = SystemTime.currentTimeMillis();
                            publicationService.save(publication);
                            logger.debug(String.format("save publication: %d ms", SystemTime.currentTimeMillis() - saveStartTime));
                        }
                    } else {
                        if (!activePublication) {
                            logger.warn(String.format("publication id = %s is inactive after update, pub type = %d", existingPublication.getId(), publication.getPubtypeId()));
                        }
                        if (existingPublication.getSources().size() > 1) {
                            logger.debug(String.format("publication id = %s has multiple sources %d", existingPublication.getId(), publication.getSources().size()));
                        }
                        updated++;
                        logger.debug("Updating publication id = {} from source id {}", existingPublication.getId(), source.getId());
                        PublicationUpdater.UpdateResult updateResult = publicationUpdater.update(existingPublication, publication);
                        Publication updatedPublication = updateResult.getPublication();
                        boolean activeUpdatedPublication = publicationFilter.test(updatedPublication);
                        boolean changedStatus;
                        if (activeUpdatedPublication) {
                            changedStatus = updatedPublication.getStatus() != Publication.STATUS_ACTIVE;
                            updatedPublication.setStatus(Publication.STATUS_ACTIVE);
                            newSourceStatus = PublicationSourceStatus.IMPORTED;
                        } else {
                            changedStatus = updatedPublication.getStatus() != Publication.STATUS_INACTIVE;
                            updatedPublication.setStatus(Publication.STATUS_INACTIVE);
                            newSourceStatus = PublicationSourceStatus.IGNORED;
                        }
                        if (updateResult.isChanged() || changedStatus) {
                            // Check if a DOI has been added
                            // if yes then we have to add additional data from the web
                            if (updateResult.isAddedDoi()) {
                                publicationDataEnhancer.enrichDataInPublication(updatedPublication);
                            }
                            // we need a new coat classification after the update
                            updatedPublication.setClassificationStatus(CLASSIFICATION_TODO);
                            // eventually try to find a journal again
                            updatedPublication.resetJournalIdStatus();
                            // eventually try to find a publisher again
                            updatedPublication.resetPublisherIdStatus();
                            publicationService.save(updatedPublication);
                        }
                    }
                    source.setStatus(newSourceStatus);
                    saveSource(source);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        logger.info(format("updating %d publications: created: %d, updated: %d, ignored: %d ", count, created, updated, ignored));

    }

    /*
     utility method to check dois in sources
     */
    public void checkDoiInSource() {

        logger.info("checkDoiInSource");

        PageRequest pageRequest = PageRequest.of(0, 500);
        int count = 0;
        int ignored = 0;
        while (true) {
            long startTime = SystemTime.currentTimeMillis();
            Page<PublicationSource> sources = publicationSourceRepository.findAllByDoiLike(pageRequest, "% %");
            logger.debug(String.format("time for findAllByDoiLike: %d ms", SystemTime.currentTimeMillis() - startTime));
            if (sources.getContent().isEmpty()) {
                break;
            }
            for (PublicationSource source : sources.getContent()) {
                count++;

                String newDoi = parseDoi(source.getDoi());
                if (newDoi != null && newDoi.contains(" ")) {
                    logger.warn(String.format("Doi for source contains a space %s\t'%s'", source.getId(), newDoi));
                }
                if (!Objects.equals(newDoi, source.getDoi())) {
                    logger.info(String.format("New doi for source %6d\t%s\t'%s'\t('%s')", count, source.getId(), newDoi != null ? newDoi : "", source.getDoi()));
                    if (containsReservedCharacter(newDoi)) {
                        logger.warn(String.format("Doi for source %s\t'%s'", source.getId(), newDoi));
                    }
                    try {
                        source.setDoi(newDoi);
                        source.setStatus(PublicationSourceStatus.NEW_OR_CHANGED);
                        saveSource(source);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            pageRequest = pageRequest.next();
        }

    }

    public FetchResult importPublications(String institutionId, List<PublicationSource> publications) {

        logger.info(String.format("Importing publications for institution %s", institutionId));

        SourceLog sourceLog = new SourceLog(institutionId);
        sourceLog.setStartTime(SystemTime.currentTimeMillis());

        FetchResult fetchResult = new FetchResult();
        for (PublicationSource source : publications) {
            createOrUpdateSource(source, fetchResult, false);
        }

        sourceLog.setEndTime(SystemTime.currentTimeMillis());
        sourceLog.setComment(fetchResult.asText());
        saveSourceLog(sourceLog);

        Institution institution = institutionService.findById(institutionId);
        // remember time of import
        saveLastImportDate(institution);

        logger.info(String.format("Importing publications for institution %s end", institutionId));

        return fetchResult;
    }

}
