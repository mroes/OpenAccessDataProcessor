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

package at.roesel.oadataprocessor.services.publicationsource.aau;

import at.roesel.oadataprocessor.services.common.RestClient;
import at.roesel.oadataprocessor.services.common.ClientParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/*
 * Client for retrieving publications from University of Klagenfurt
 */
public class AauRestClient extends RestClient {
    private final Logger logger = LoggerFactory.getLogger(AauRestClient.class);

    private final String userAgent;

    private final String apiUrl;
    private final String apiKey;

    public AauRestClient(String mailTo, String apiUrl, String apiKey) {
        super();

        if (!apiUrl.endsWith("/")) {
            apiUrl += "/";
        }

        this.apiUrl = apiUrl;
        this.apiKey = apiKey;

        userAgent = String.format("%s (mailto:%s)", clientName, mailTo);
    }

    public AauRestClient(String mailTo, ClientParameter parameter) {
        this(mailTo, parameter.getUrl(), parameter.getApiKey());
    }

    public InputStream getInputStream(LocalDate modifiedAfter) {

        InputStream responseInputStream = null;

        // https://cris.aau.at/at2oa2/getChanges?since=20-01-2024
        String cmd = "getPublications";
        if (modifiedAfter != null) {
            cmd = String.format("getChanges?since=%s", formatDate(modifiedAfter));
        }
        String url = apiUrl + cmd;

        try {

            int offset = 0;
            logger.debug(String.format("offset = %6d\t%s", offset, url));
            ResponseEntity<Resource> responseEntity = restTemplate.exchange(
                    url, HttpMethod.GET, headersEntity(), Resource.class);
                if (responseEntity.getBody() == null) {
                    return null;
                }
                try {
                    responseInputStream = responseEntity.getBody().getInputStream();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        return responseInputStream;
    }

    private static String formatDate(LocalDate date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        return date.atStartOfDay().atOffset(ZoneOffset.UTC).format(dtf);
    }

    private HttpEntity<String> headersEntity() {
        HttpHeaders headers = new HttpHeaders();
//        headers.add("api-key", apiKey);
        headers.add("User-Agent", userAgent);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        return entity;
    }


}
