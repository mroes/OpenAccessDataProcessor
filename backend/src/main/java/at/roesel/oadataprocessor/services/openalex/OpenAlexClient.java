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

package at.roesel.oadataprocessor.services.openalex;

import at.roesel.oadataprocessor.model.openalex.OpenAlexPublisher;
import at.roesel.oadataprocessor.model.openalex.OpenAlexPublisherResponse;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWorkResponse;
import at.roesel.oadataprocessor.persistance.conversion.OpenAlexWorkConverter;
import at.roesel.oadataprocessor.services.common.RestClient;
import at.roesel.oadataprocessor.services.common.CollectAllResultsResponseHandler;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

// https://api.openalex.org/works?filter=from_publication_date:2022-01-01,to_publication_date:2022-01-26,authorships.institutions.country_code:AT

public class OpenAlexClient extends RestClient {

    private final Logger logger = LoggerFactory.getLogger(OpenAlexClient.class);

    private final static String apiUrl = "https://api.openalex.org/";

    private final String mailTo;

    private final OpenAlexWorkConverter openAlexWorkConverter = new OpenAlexWorkConverter();

    public OpenAlexClient(String contactMail) {
        super();

        if (contactMail != null) {
            mailTo = "mailto=" + contactMail;
        } else {
            mailTo = null;
        }
    }

    public OpenAlexWork work(String doi) {
        String error;
        // The URL is encoded in the `RestTemplate` and therefore must not be encoded beforehand.
        String cmd = "works/doi:" + doi;
        if (mailTo != null) {
            cmd += "?" + mailTo;
        }
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiUrl + cmd, HttpMethod.GET, null, String.class);
            if (responseEntity.getBody() != null) {
                // Explicit conversion from String to Object, as character encoding issues occur during
                // implicit conversion via `restTemplate.exchange`., z.B. bei doi 10.18632/oncotarget.1696
                OpenAlexWork work = openAlexWorkConverter.convertToEntityAttribute(responseEntity.getBody());
                return work;
            } else {
                error = "no data received";
            }
        } catch (HttpClientErrorException e) {
            // Only log the exception if it is not a 404
            if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error(e.getMessage(), e);
            }
            error = e.getMessage();
        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            error = e.getMessage();
        }

        OpenAlexWork errorResult = new OpenAlexWork();
        errorResult.id = UUID.randomUUID().toString();
        errorResult.error = error;

        return errorResult;
    }

    /*
    To page through results, specify the page you want using the ?page query parameter.
    By default there are 25 results per page; you can use the ?per-page parameter to change that to any number between 1 and 200.

    Currently you can only use paging to read the first 10,000 results of any list.
    To read more, you'll need to use cursor pagination, which we haven't implemented.
     */
    public List<OpenAlexWork> works(String parameters) {
        CollectAllResultsResponseHandler<OpenAlexWork> responseHandler = new CollectAllResultsResponseHandler<>();
        fetchWorks(parameters, responseHandler);
        return responseHandler.getResults();
    }

    public void fetchWorks(String parameters, ResultResponseHandler<OpenAlexWork> resultResponseHandler) {

        int pageSize = 200; // Number of results per query

        // from_publication_date:2022-01-01,to_publication_date:2022-01-26,authorships.institutions.country_code:AT
        String cmd = "works?filter=authorships.institutions.country_code:AT" ;
        cmd += "&per-page=" + pageSize;
        if (parameters != null && !parameters.isEmpty()) {
            cmd += "&" + parameters;
        }
        if (mailTo != null) {
            cmd += "&" + mailTo;
        }
        cmd += "&cursor=";
        try {
            // Cursor paging is a more advanced method when you need to retrieve results over 10,000 records. Request a cursor by appending cursor=* to any endpoint.
            int offset = 0;
            String cursor = "*";
            while (true) {
                String url = apiUrl + cmd + cursor;
                logger.debug("url:" + url);
                ResponseEntity<OpenAlexWorkResponse> responseEntity = restTemplate.exchange(
                        url, HttpMethod.GET, null, OpenAlexWorkResponse.class);
                OpenAlexWorkResponse response = responseEntity.getBody();
                if (response == null || response.results == null || response.results.isEmpty()) {
                    logger.debug("response or response results is null");
                    break;
                }
                if (offset == 0) {
                    logger.debug("Number of records: {}", response.meta.get("count"));
                }
                boolean stop = resultResponseHandler.handleResponse(response.results);
                if (stop) {
                    break;
                }
                cursor = response.meta.get("next_cursor");
                if (cursor == null || cursor.isEmpty()) {
                    break;
                }
                offset += pageSize;
            }

        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void fetchPublishers(String parameters, ResultResponseHandler<OpenAlexPublisher> resultResponseHandler) {

        int pageSize = 200; // Anzahl der Resultate pro Abfrage

        String cmd = "publishers" ;
        cmd += "?per-page=" + pageSize;
        if (parameters != null && !parameters.isEmpty()) {
            cmd += "&" + parameters;
        }
        if (mailTo != null) {
            cmd += "&" + mailTo;
        }
        cmd += "&cursor=";
        try {
            // Cursor paging is a more advanced method when you need to retrieve results over 10,000 records. Request a cursor by appending cursor=* to any endpoint.
            int offset = 0;
            String cursor = "*";
            while (true) {
                String url = apiUrl + cmd + cursor;
                logger.debug("url:" + url);
                ResponseEntity<OpenAlexPublisherResponse> responseEntity = restTemplate.exchange(
                        url, HttpMethod.GET, null, OpenAlexPublisherResponse.class);
                OpenAlexPublisherResponse response = responseEntity.getBody();
                if (response == null || response.results == null || response.results.isEmpty()) {
                    logger.debug("response or response results is null");
                    break;
                }
                if (offset == 0) {
                    logger.debug("Number of records: {}", response.meta.getCount());
                }
                boolean stop = resultResponseHandler.handleResponse(response.results);
                if (stop) {
                    break;
                }
                cursor = response.meta.getNextCursor();
                if (cursor == null || cursor.isEmpty()) {
                    break;
                }
                offset += pageSize;
            }

        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
        }

    }

}
