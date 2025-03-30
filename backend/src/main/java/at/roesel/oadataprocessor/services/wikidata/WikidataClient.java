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

package at.roesel.oadataprocessor.services.wikidata;

import at.roesel.oadataprocessor.services.common.RestClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.ZoneOffset;

/*
    https://www.wikidata.org/wiki/Wikidata:Data_access?
    https://meta.wikimedia.org/wiki/User-Agent_policy
    http://baskauf.blogspot.com/2019/05/getting-data-out-of-wikidata-using.html
 */
public class WikidataClient extends RestClient {

    private final Logger logger = LoggerFactory.getLogger(WikidataClient.class);

    private final static String apiUrl = "https://query.wikidata.org/sparql";
    private final static MediaType sparqlMediaType = MediaType.valueOf("application/sparql-query");
    private final static String entityUrl = "https://www.wikidata.org/wiki/Special:EntityData";
    private final static String cr = "\r\n";

    private String userAgent;

    public WikidataClient(String contactMail) {
        super();

        if (contactMail != null) {
            userAgent = String.format("%s (mailto:%s)", clientName, contactMail);
        } else {
            userAgent = String.format("%s", clientName);
        }

    }

    public String fetchPublishers(LocalDate since) {

        if (since == null) {
            since = LocalDate.of(1900, 1, 1);
        }
        String formattedDate = since.atStartOfDay(ZoneOffset.UTC).toString();
        String query = new StringBuilder()
                .append("SELECT ?publisher ?date ?redirect").append(cr)
                .append("WHERE").append(cr)
                .append("{").append(cr)
                //  publisher of focus list
                .append("?publisher wdt:P5008 wd:Q117222928.").append(cr)
                .append("OPTIONAL { ?redirect owl:sameAs ?publisher }.").append(cr)
                .append("?publisher schema:dateModified ?date .").append(cr)
                .append("FILTER (?date > \"").append(formattedDate).append("\"^^xsd:dateTime)").append(cr)
                .append("}")
                .append("ORDER BY ?date")
                .toString();

        return executeWikidataQuery(query);
    }

    public String executeWikidataQuery(String sparqlQuery) {

        RequestParameters requestParameters = new RequestParameters();
        requestParameters.format = "json";
        requestParameters.query = sparqlQuery;

        try {
            // https://linkedwiki.com/query/SPARQL_endpoints_in_Wikidata
            // https://github.com/BorderCloud/SPARQL-JAVA
            String requestBody = requestParameters.query;
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, requestEntity(requestBody), String.class);
            if (responseEntity.getStatusCode().isError()) {
                throw new RuntimeException("Wikidata POST failed " +  responseEntity.getStatusCode());
            }
            return responseEntity.getBody();

        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    private HttpEntity<String> requestEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", userAgent);
        // Mediatype is important; otherwise, PUT is not accepted.
        headers.setContentType(sparqlMediaType);
        headers.add("Accept", "application/sparql-results+json");
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        return entity;
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private static class RequestParameters {
        public String format;
        public String query;
    }


    public String fetchWikiEntity(String wikiId) {
        if (wikiId.isEmpty()) {
            return "";
        }
        if (!wikiId.startsWith("Q")) {
            logger.error("fetchWikiEntity() query parameter is invalid: {}", wikiId);
            return "";
        }
        try {
            String params = "/" + wikiId + ".json";

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    entityUrl + params, HttpMethod.GET, requestEntity(params), String.class);
            return responseEntity.getBody();

        } catch (HttpClientErrorException e) {
            if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error(e.getMessage(), e);
                throw e;
            }
            return "error not found: " + e.getMessage();
        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

}
