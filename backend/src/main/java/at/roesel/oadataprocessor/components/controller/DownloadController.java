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
import at.roesel.oadataprocessor.services.impexp.export.DownloadCache;
import at.roesel.oadataprocessor.services.impexp.export.DownloadService;
import at.roesel.oadataprocessor.services.websocket.WebSocketMessage;
import at.roesel.oadataprocessor.services.websocket.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Controller
public class DownloadController {

    private final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    private final MediaType xlsxMediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String downloadReadyCommand = "download_ready";

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private DownloadCache downloadCache;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private WebSocketService webSocketService;

    private final ThreadPoolTaskExecutor taskExecutor = taskExecutor();

    private final Map<String, Future<DownloadService.BuildResult>> runningTasks = new ConcurrentHashMap<>();


    public DownloadController() {
    }

    private ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("DownloadTask-");
        executor.initialize();
        return executor;
    }


    @MessageMapping("/command")
    public void onMessage(WebSocketMessage<PublicationSearchFilter> message, Principal principal) throws Exception {
        logger.debug("WebSocket command received: {}", message.cmd);

        // we use the hash of the filter as key for the cache
        String downloadKey = message.data.getCacheKey();

        // Check if the result is already in the cache
        DownloadService.BuildResult cachedResult = downloadCache.get(downloadKey);
        if (cachedResult != null) {
            logger.debug("Using cached data for download key: {}", downloadKey);
            sendMessageDownloadReady(principal.getName(), downloadKey);
        } else {
            prepareDataForDownload(message.data, downloadKey, (result) -> {
                sendMessageDownloadReady(principal.getName(), downloadKey);
            });
        }
    }

    // Notify the client that the result is available for download
    private void sendMessageDownloadReady(String userName, String downloadKey) {
        WebSocketMessage<String> msg = new WebSocketMessage<>(downloadReadyCommand);
        msg.data = downloadKey;
        webSocketService.sendMessageToUser(userName, msg);
    }

    private void prepareDataForDownload(PublicationSearchFilter filter, String downloadKey, Consumer<DownloadService.BuildResult> consumer) {
        // only start a new task for data preparation if there is no existing task for it
        synchronized (runningTasks) {
            if (!runningTasks.containsKey(downloadKey)) {
                Future<DownloadService.BuildResult> future = taskExecutor.submit(() -> {
                    try {
                        logger.debug("Preparing data for download key: {}", downloadKey);
                        DownloadService.BuildResult result = downloadService.createDownloadRecords(filter);
                        downloadCache.put(downloadKey, result);
                        return result;
                    } finally {
                        runningTasks.remove(downloadKey);
                    }
                });
                runningTasks.put(downloadKey, future);
            }
        }

        Future<DownloadService.BuildResult> future = runningTasks.get(downloadKey);
        taskExecutor.execute(() -> {
            try {
                logger.debug("Waiting for the download data for key: {}", downloadKey);
                DownloadService.BuildResult result = future.get();
                logger.debug("Finished download preparation for key: {}", downloadKey);
                consumer.accept(result);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error while processing download request", e);
            }
        });
    }

    @RequestMapping(method = {RequestMethod.POST},
            path = "api/download/{reportname}",
            produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public DeferredResult<ResponseEntity<MultiValueMap<String, Object>>> handleRequest(@PathVariable("reportname") String reportName,
                                                                                       @RequestBody DownloadRequest request
    ) {
        logger.debug("Download request for the download key: {}", request.key);

        DeferredResult<ResponseEntity<MultiValueMap<String, Object>>> result = new DeferredResult<>(appSettings.getDownloadTimeout());
        boolean useKey = true;
        DownloadService.BuildResult buildResult = null;
        if (request.key != null) {
            buildResult = downloadCache.get(request.key);
        } else if (request.filter != null) {
            useKey = false;
            buildResult = downloadCache.get(request.filter.getCacheKey());
        }
        String extension = ".xlsx";
        String reportFileName = "publications" + extension;
        if (useKey && buildResult == null) {
            DownloadService.BuildResult buildResultError = new DownloadService.BuildResult();
            buildResultError.messages.add("Invalid download key");
            ResponseEntity<MultiValueMap<String, Object>> responseEntity = createResponse(reportFileName, extension, null, buildResultError);
            result.setResult(responseEntity);
        } else {
            if (buildResult != null) {
                // data is available, build the result
                ResponseEntity<MultiValueMap<String, Object>> responseEntity = createResponse(reportFileName, extension, buildResult.resource, buildResult);
                result.setResult(responseEntity);
            } else {
                // handleRequest() is used without WebSocket messages, we have to prepare the data
                prepareDataForDownload(request.filter, request.filter.getCacheKey(), (res) -> {
                    ResponseEntity<MultiValueMap<String, Object>> responseEntity = createResponse(reportFileName, extension, res.resource, res);
                    result.setResult(responseEntity);
                });
            }
        }

        logger.debug("Finished download request for the download key: {}", request.key);
        return result;
    }

    public ResponseEntity<MultiValueMap<String, Object>> createResponse(String reportFileName, String ext,
                                                                        ByteArrayResource resource,
                                                                        DownloadService.BuildResult buildResult) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Frame-Options", "SAMEORIGIN");

        MediaType mediaType;
        switch (ext) {
            case ".pdf": {
                mediaType = MediaType.APPLICATION_PDF;
                break;
            }
            case ".xml": {
                mediaType = MediaType.TEXT_XML;
                break;
            }
            case ".csv":
            case ".txt": {
                mediaType = MediaType.TEXT_PLAIN;
                break;
            }
            case ".docx": {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            }
            case ".xlsx": {
                mediaType = xlsxMediaType;
                break;
            }
            default: {
                mediaType = MediaType.APPLICATION_XML;
            }
        }

        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(mediaType);
        ContentDisposition contentDisposition = ContentDisposition.builder("form-data").name("file").filename(reportFileName).build();
        partHeaders.setContentDisposition(contentDisposition);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(resource, partHeaders);
        formData.add("file", fileEntity);
        if (buildResult != null && buildResult.messages != null) {
            formData.add("msg", buildResult.messages);
        }

        return new ResponseEntity<>(formData, headers, HttpStatus.OK);

    }

    public static class DownloadRequest {
        public String key;
        public PublicationSearchFilter filter;
    }

}
