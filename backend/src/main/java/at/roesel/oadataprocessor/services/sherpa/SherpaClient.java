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

package at.roesel.oadataprocessor.services.sherpa;

import at.roesel.oadataprocessor.model.sherpa.RomeoPublisher;
import at.roesel.oadataprocessor.model.sherpa.SherpaObjectResponse;
import at.roesel.oadataprocessor.model.sherpa.SherpaPublisherResponse;
import at.roesel.oadataprocessor.services.common.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.function.Consumer;

// https://v2.sherpa.ac.uk/api/
public class SherpaClient extends RestClient {

    private final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private final static String apiUrl = "https://v2.sherpa.ac.uk/cgi/retrieve_by_id";
    private final static String argsTemplate = "?item-type=publication&api-key=%s&format=Json&identifier=";
    private final String args;
    private final String apiKey;

    public SherpaClient(String apiKey) {
        super();
        this.apiKey = apiKey;
        args = String.format(argsTemplate, apiKey);
    }

    public SherpaObjectResponse objectByID(String issn) {
        SherpaObjectResponse result;
        try {
            ResponseEntity<SherpaObjectResponse> response = restTemplate.exchange(
                    apiUrl + args + issn, HttpMethod.GET, null, SherpaObjectResponse.class);
            result = response.getBody();
        } catch (RestClientException e) {
            logger.error(String.format("Sharpa request error for %s: %s", issn, e.getMessage()), e);
            result = new SherpaObjectResponse();
            result.setError(e.getMessage());
        }

        return result;
    }

    private final static String apiPublisher = "https://v2.sherpa.ac.uk/cgi/retrieve?item-type=publisher&api-key=%s&format=Json&limit=%d&offset=%d";

    public void fetchPublishers(Consumer<RomeoPublisher> visitor) {

        int limit = 1000;
        int offset = 0;

        while (true) {
            String url = String.format(apiPublisher, apiKey, limit, offset);
            ResponseEntity<SherpaPublisherResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, SherpaPublisherResponse.class);
            List<RomeoPublisher> publishers = response.getBody().items;
            for (RomeoPublisher publisher : publishers) {
                visitor.accept(publisher);
            }
            if (publishers.size() < limit) {
                break;
            }
            offset += limit;
        }
    }

}
