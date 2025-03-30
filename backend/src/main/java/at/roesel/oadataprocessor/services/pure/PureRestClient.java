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

package at.roesel.oadataprocessor.services.pure;

import at.roesel.common.SleepSupport;
import at.roesel.oadataprocessor.persistance.conversion.ObjectMapperFactory;
import at.roesel.oadataprocessor.services.common.RestClient;
import at.roesel.oadataprocessor.services.common.ClientParameter;
import at.roesel.oadataprocessor.services.common.CollectAllResultsResponseHandler;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import at.roesel.oadataprocessor.services.pure.model.PureResearchOutput;
import at.roesel.oadataprocessor.services.pure.model.PureResearchOutputResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
    docs for Pure Webservice, e.g. https://ucris.univie.ac.at/ws/
    we need an API-Key with access for the needed endpoints
    developed for Purse WS version 5.20
 */
public class PureRestClient extends RestClient {

    private final Logger logger = LoggerFactory.getLogger(PureRestClient.class);
    private final static ObjectMapper objectMapper = ObjectMapperFactory.create();

    private final static String locale = "de_DE";

    private final String apiUrl;
    private final String apiKey;
    private int pageSize; // number of results per query

    public PureRestClient(String apiUrl, String apiKey, String proxyConfig) {
        super(proxyConfig);

        if (!apiUrl.endsWith("/")) {
            apiUrl += "/";
        }

        this.apiUrl = apiUrl;
        this.apiKey = apiKey;

        pageSize = 200;

    }

    public PureRestClient(ClientParameter parameter) {
        this(parameter.getUrl(), parameter.getApiKey(), parameter.getProxyConfig());
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public PureResearchOutput getPureResearchOutput(String doi) {
        throw new NotImplementedException();
    }

    public List<PureResearchOutput> getResearchOutputs(LocalDate modifiedAfter) {
        CollectAllResultsResponseHandler<PureResearchOutput> responseHandler = new CollectAllResultsResponseHandler<>();
        fetchResearchOutputs(responseHandler, modifiedAfter);
        return responseHandler.getResults();
    }

    public void fetchResearchOutputs(ResultResponseHandler<PureResearchOutput> resultResponseHandler, LocalDate modifiedAfter) {

        String cmd = "research-outputs";
        String url = apiUrl + cmd;

        RequestParameters requestParameters = new RequestParameters();
        requestParameters.addLocale(locale);
        requestParameters.size = pageSize;
        requestParameters.modifiedAfter = modifiedAfter;
        try {

            int offset = 0;
            int retry = 0;
            while (true) {
                requestParameters.offset = offset;
                String requestBody;
                try {
                    requestBody = objectMapper.writeValueAsString(requestParameters);
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                    break;
                }
                logger.debug(String.format("offset = %6d\t%s", offset, url));
                ResponseEntity<PureResearchOutputResponse> responseEntity = null;
                try {
                    responseEntity = restTemplate.exchange(
                            url, HttpMethod.POST, requestEntity(requestBody), PureResearchOutputResponse.class);
                    PureResearchOutputResponse response = responseEntity.getBody();
                    if (response == null || response.items == null || response.items.isEmpty()) {
                        break;
                    }
                    if (offset == 0) {
                        logger.debug("total record count = {}", response.count);
                    }
                    boolean stop = resultResponseHandler.handleResponse(response.items);
                    if (stop) {
                        break;
                    }
                    offset += pageSize;
                    retry = 0;  // reset retry counter
                } catch (ResourceAccessException e) {
                    // org.springframework.web.client.ResourceAccessException: I/O error on POST request for "https://pure.tugraz.at/ws/api/524/research-outputs": pure.tugraz.at:443 failed to respond
                    logger.error(e.getMessage());
                    if (retry > 3) {
                        // too many tries, throw Exception
                        throw e;
                    }
                    retry++; // try again with the same offset
                    SleepSupport.sleep((long) retry * 1000 * 10);  // wait 10 x retry seconds
                }
            }
        } catch (RestClientException e) {
            logger.error(e.getMessage());
            resultResponseHandler.onError(e);
            throw e;
        }

        resultResponseHandler.onFinished();
    }

    private HttpEntity<String> requestEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("api-key", apiKey);
        headers.add("User-Agent", clientName);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return entity;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private static class RequestParameters {
        public List<String> locales = new ArrayList<>();
        public LocalDate modifiedAfter;
        public int size;
        public int offset;

        public void addLocale(String locale) {
            locales.add(locale);

        }
    }

}
