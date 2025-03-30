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

package at.roesel.oadataprocessor.services.doaj;

import at.roesel.oadataprocessor.model.doaj.DoajJsonResponse;
import at.roesel.oadataprocessor.model.doaj.DoajPayload;
import at.roesel.oadataprocessor.services.common.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoajClient extends RestClient {

    private final Logger logger = LoggerFactory.getLogger(DoajClient.class);

    private final static String apiUrl = "https://doaj.org/api";

    private final String mailTo;
    private final String userAgent;

    private int requestCount;

    public DoajClient(String mailTo) {
        super();
        this.mailTo = mailTo;
        userAgent = String.format("%s (mailto:%s)", clientName, mailTo);
        requestCount = 0;
    }

    /*
     * @return: null on error, otherwise an array with the results (can be empty)
     */
    public List<DoajPayload> searchJournal(String issn) {

        List<DoajPayload> journals = new ArrayList<>();
        String url = apiUrl + "/search/journals/issn:" + issn;

        try {
            ResponseEntity<DoajJsonResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, headersEntity(), DoajJsonResponse.class);
            DoajJsonResponse doajJsonResponse = response.getBody();
            if (doajJsonResponse != null && doajJsonResponse.getResults() != null) {
                journals.addAll(doajJsonResponse.getResults());
            }
        } catch (Exception e) {
            logger.error("error in DoajClient.searchJournal for issn={}: {}", issn, e.getMessage());
            throw(e);
        }

        return journals;
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
