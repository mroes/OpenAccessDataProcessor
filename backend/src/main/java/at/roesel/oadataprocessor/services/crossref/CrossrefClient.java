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

package at.roesel.oadataprocessor.services.crossref;

import at.roesel.oadataprocessor.model.crossref.*;
import at.roesel.oadataprocessor.services.common.RestClient;
import at.roesel.common.SleepSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CrossrefClient extends RestClient {

    private final Logger logger = LoggerFactory.getLogger(CrossrefClient.class);

    private final static String apiUrl = "https://api.crossref.org";
    // Maximum number of repeated REST calls
    private final int maxRepeatCount = 3;

    private final String mailTo;
    private final String userAgent;

    private int requestCount;

    public CrossrefClient(String mailTo) {
        super();
        this.mailTo = mailTo;
        userAgent = String.format("%s (mailto:%s)", clientName, mailTo);
        requestCount = 0;
    }

    // https://api.crossref.org/works/10.1155/2014/413629
    public CrossrefWork crossrefWork(String doi) {
        String error;
        boolean repeat;
        int repeatCount = 0;
        do {
            repeat = false;
            try {
                requestCount++;
                repeatCount++;
                // The URL is encoded in the RestTemplate and therefore must not be encoded beforehand.
                ResponseEntity<CrossrefWorkResponse> response = restTemplate.exchange(
                        apiUrl + "/works/" + doi, HttpMethod.GET, headersEntity(), CrossrefWorkResponse.class);
                CrossrefWorkResponse crossrefWorkResponse = response.getBody();
                return crossrefWorkResponse.message;
            } catch (HttpClientErrorException e) {
                // Only log the exception if it is not a 404 (not found)
                if (!e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                    logger.error("request doi {} from Crossref: {}", doi, e.getMessage(), e);
                }
                error = e.getMessage();
            } catch (HttpServerErrorException e) {
                error = e.getMessage();
                // 504 Gateway Time-out
                if (e.getStatusCode().equals(HttpStatus.GATEWAY_TIMEOUT) && repeatCount < maxRepeatCount) {
                    logger.warn(e.getMessage());
                    Random random = new Random();
                    SleepSupport.sleep(200 * (random.nextInt(10) + 1));
                    repeat = true;
                } else {
                    logger.error("request doi {} from Crossref: {}", doi, e.getMessage(), e);
                }
            } catch (RestClientException e) {
                logger.error("request doi {} from Crossref: {}", doi, e.getMessage(), e);
                error = e.getMessage();
            }
        } while (repeat);

        // In case of an error, we return an empty dataset
        CrossrefWork errorResult = new CrossrefWork();
        errorResult.setError(error);
        return errorResult;
    }

    /*
     @return null on error, otherwise an array with the results (can be empty)
     */
    public CrossrefDoiSearchResult searchWork(String author, String title) {

        if (author == null && title == null) {
            throw new RuntimeException("Crossref searchWork: no arguments for search given");
        }

        int maxRows = 200;
        int maxResults = 5000;  // maximum number of requested works

        CrossrefDoiSearchResult result = new CrossrefDoiSearchResult();

        List<CrossrefWork> works = new ArrayList<>();
        String cursor = "*";
        String url = apiUrl + "/works";

        try {
            if (title != null) {
                title = title.replace("&", " ");
            }

            boolean repeat;
            do {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                        .queryParam("rows", maxRows)
                        .queryParam("cursor", cursor);
                if (author != null) {
                    builder = builder.queryParam("query.author", author);
                }
                if (title != null) {
                    builder = builder.queryParam("query.title", title);
                }
                // Felder im Resultat einschränken
                builder = builder.queryParam("select", "DOI,author,title,published");

                repeat = false;
                ResponseEntity<CrossrefMultipleWorkResponse> response = restTemplate.exchange(
                        builder.build().toUri(), HttpMethod.GET, headersEntity(), CrossrefMultipleWorkResponse.class);
                CrossrefMultipleWorkResponse crossrefWorkResponse = response.getBody();
                if (crossrefWorkResponse != null && crossrefWorkResponse.message != null) {
                    if (crossrefWorkResponse.message.items != null) {
                        result.totalResults = crossrefWorkResponse.message.totalResults;
                        int rows = crossrefWorkResponse.message.items.size();
                        if (rows == maxRows) {
                            repeat = true;
                            cursor = crossrefWorkResponse.message.nextCursor;
                        }
                        works.addAll(crossrefWorkResponse.message.items);
                        if (works.size() > maxResults) {
                            repeat = false;
                        }
                    }
                }
            } while (repeat);
        } catch (RestClientException e) {
            logger.error("{}\t{}", title, e.getMessage(), e);
            result.exception = e;
        }

        result.works = works;
        return result;
    }

    public CrossrefPrefix crossrefPrefix(String prefix) {

        try {
            ResponseEntity<CrossrefPrefixResponse> response = restTemplate.exchange(
                    apiUrl + "/prefixes/" + prefix, HttpMethod.GET, headersEntity(), CrossrefPrefixResponse.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                CrossrefPrefixResponse crossrefResponse = response.getBody();
                if (crossrefResponse != null) {
                    return crossrefResponse.message;
                }
            }
        } catch (HttpClientErrorException e) {
            if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error(e.getMessage(), e);
            }
        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }


    private HttpEntity<String> headersEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", userAgent);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        return entity;
    }

    public int getRequestCount() {
        return requestCount;
    }
}
