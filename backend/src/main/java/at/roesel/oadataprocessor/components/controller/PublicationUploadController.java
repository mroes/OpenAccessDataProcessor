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

package at.roesel.oadataprocessor.components.controller;

import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.Institution;
import at.roesel.oadataprocessor.services.InstitutionService;
import at.roesel.oadataprocessor.services.WorkService;
import at.roesel.oadataprocessor.services.common.FetchResult;
import at.roesel.oadataprocessor.services.publicationsource.PublicationSourceService;
import at.roesel.oadataprocessor.services.publicationsource.SourcePreviewCache;
import at.roesel.oadataprocessor.services.publicationsource.SourcePublicationsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/publication")
public class PublicationUploadController {

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private PublicationSourceService publicationSourceService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private WorkService workService;

    @Autowired
    private SourcePreviewCache sourcePreviewCache;

    private final Logger logger = LoggerFactory.getLogger(PublicationUploadController.class);
    private final ThreadPoolTaskExecutor taskExecutor = taskExecutor();

    private ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("ImportTask-");
        executor.initialize();
        return executor;
    }


    public PublicationUploadController() {
    }

    private final String rorUrlPrefix = "https://ror.org/";

    @PostMapping(path = "/upload")
    @PreAuthorize("hasRole('ROLE_upload') || hasRole('ROLE_admin')")
    public DeferredResult<ResponseEntity<?>> uploadPublications(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("institutionId") String institutionId) {

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(appSettings.getDownloadTimeout());

        String institutionCode = null; // = extractIdFromUrl(institutionId);
        if (file.getOriginalFilename() != null) {
            institutionCode = extractRorId(file.getOriginalFilename());
            Institution institution = institutionService.findById(rorUrlPrefix + institutionCode);
            if (institution == null) {
                logger.warn(String.format("Uploading publications attempt for unknown institution, file '%s'", file.getOriginalFilename()));
                output.setErrorResult(new ResponseEntity<>("Invalid institution id or unknown institution", HttpStatus.BAD_REQUEST));
                return output;
            }
            institutionId = institution.getId();
        }

        logger.info(String.format("Uploading publications for %s, file '%s'", institutionCode, file.getOriginalFilename()));

        try {
            String ext = ".xlsx";
            if (file.getOriginalFilename() != null) {
                int postLastDot = file.getOriginalFilename().lastIndexOf('.');
                if (postLastDot > 0) {
                    ext = file.getOriginalFilename().substring(postLastDot);
                }
            }

            String fileName = institutionCode + "_" + UUID.randomUUID() + ext;
            Path target = Paths.get(appSettings.getUploadPath(), fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Process file asynchronously
            String finalInstitutionId = institutionId;
            taskExecutor.submit(() -> {
                try {
//                    publicationSourceService.fetchFromCSV(institutionId, target.toFile().getAbsolutePath());
                    FetchResult fetchResult = publicationSourceService.fetchFromExcel(finalInstitutionId, target.toFile().getAbsolutePath(), 0, true);
                    // cache the publications for preview and import
                    SourcePublicationsStore store = new SourcePublicationsStore(fetchResult.getPublicationSources());
                    String key = UUID.randomUUID().toString();
                    sourcePreviewCache.put(key, store);
                    fetchResult.limitPreviewPublications(100);
                    output.setResult(new ResponseEntity<>(new UploadResponse(key, fetchResult), HttpStatus.OK));
                } catch (Exception e) {
                    logger.error("Error processing file asynchronously", e);
                    output.setErrorResult(new ResponseEntity<>("Processing error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                }
            });

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            output.setResult(new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED));
        }

        return output;
    }

    @PostMapping(path = "/import")
    @PreAuthorize("hasRole('ROLE_upload') || hasRole('ROLE_admin') ")
    public ResponseEntity<?> importPublications(@RequestBody ImportParams params) {

        logger.info(String.format("Importing publications for %s", params.institutionId));

        SourcePublicationsStore store = sourcePreviewCache.get(params.key);
        if (store == null || store.getPublications() == null || store.getPublications().isEmpty()) {
            return new ResponseEntity<>("no_data", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        taskExecutor.submit(() -> {
            FetchResult fetchResult = publicationSourceService.importPublications(params.institutionId, store.getPublications());
            logger.info(fetchResult.asText());

            if ("ingest".equals(params.cmd)) {
                try {
                    workService.ingestPublications();
                } catch (Exception e) {
                    logger.error("Error ingesting publications asynchronously", e);
                }
            }
        });

        return ResponseEntity.ok("ok");
    }

    public static class UploadResponse {

        public UploadResponse(String key, FetchResult fetchResult) {
            this.key = key;
            this.fetchResult = fetchResult;
        }

        public String key;
        public FetchResult fetchResult;
    }

    public static class ImportParams {
        public String cmd;
        public String institutionId;
        public String token;
        public String key;  // cache key for uploaded data
    }

    @GetMapping(path = "/ingest/state")
    public String getPublicationIngestState() {
        return workService.getState().toString();
    }

    private static String extractRorId(String name) {
        int idxUnderScore = name.indexOf('_');
        if (idxUnderScore < 0) {
            return name;
        }
        return name.substring(0, idxUnderScore).toLowerCase();
    }
}
