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

package at.roesel.oadataprocessor.services.unpaywall;

import at.roesel.oadataprocessor.model.unpaywall.UnpaywallResource;
import at.roesel.oadataprocessor.services.common.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

public class UnpaywallClient extends RestClient {

    private final Logger logger = LoggerFactory.getLogger(UnpaywallClient.class);

    private final static String apiUrl = "https://api.unpaywall.org/v2";

    private final String mailTo;

    private int requestCount;

    public UnpaywallClient(String mailTo) {
        super();
        this.mailTo = mailTo;
        requestCount = 0;
    }

    public UnpaywallResource resource(String doi) {

        String error;
        // We use Spring parameters because this allows special characters in the DOI to work as well.
        String url = apiUrl + "/{doi}?email={mailTo}";
        try {
            requestCount++;
            // The URL is encoded in the RestTemplate and therefore must not be encoded beforehand.
            ResponseEntity<UnpaywallResource> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, UnpaywallResource.class,
                    doi, mailTo);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // Only log the exception if it is not a 404 (not found).
            if (!e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                logger.error("request doi {} from Unpaywall: {}", doi, e.getMessage(), e);
            }
            error = e.getMessage();
        }
        catch (RestClientException e) {
            logger.error("request doi {} from Unpaywall: {}", doi, e.getMessage(), e);
            error = e.getMessage();
        }

        // In case of an error, we return an empty data set.
        UnpaywallResource errorResult = new UnpaywallResource();
        errorResult.setError(error);
        return errorResult;
    }

    public int getRequestCount() {
        return requestCount;
    }
}
