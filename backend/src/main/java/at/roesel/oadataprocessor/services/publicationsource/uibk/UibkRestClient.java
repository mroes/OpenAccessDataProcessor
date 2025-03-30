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

package at.roesel.oadataprocessor.services.publicationsource.uibk;

import at.roesel.common.SleepSupport;
import at.roesel.oadataprocessor.openapi.model.Error;
import at.roesel.oadataprocessor.openapi.model.Publication;
import at.roesel.oadataprocessor.persistance.conversion.ObjectMapperFactory;
import at.roesel.oadataprocessor.services.common.ClientParameter;
import at.roesel.oadataprocessor.services.common.RestClient;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/*
 * Client for the special webservice for collecting publications from the University Innsbruck
 *
 * Hier der Server-Endpunkt zur Authentifzierung:
{server}/oauth/token/client_credentials?grant_type=client_credentials&client_id={client_id}&client_secret={client_secret}

Die Endpunkte für die Publikationen:
- für einen einzelnen Datensatz:
{server}/ws/publication/{publication_id}
- für eine Liste von Datensätzen:
{server}/ws/publications

Das Abholen eines einzelnen Datensatzes ist nur über die ID möglich, da
im UIBK-FIS die DOIs nicht eindeutig sind. z.B.:
{server}/ws/publication/1&access_token={access_token}

Beim Abholen einer Liste von Datensätzen können folgende Parameter
angegeben werden:
- since: letztes Änderungsdatum im Format "dd.mm.yyyy"
- page: abzurufende Seite
- limit: Anzahl der Datensätze pro Seite z.B.:
{server}/ws/publications?since=01.01.1999&page=2&limit=1&access_token={access_token}

Über die Angaben "previous_page" bzw. "next_page" in "cursor" kann über die Daten iteriert werden.

 */
public class UibkRestClient extends RestClient {
    private final Logger logger = LoggerFactory.getLogger(UibkRestClient.class);

    private final String userAgent;

    private final String apiUrl;
    private final String clientId;
    private final String clientSecret;
    private int pageSize; // number of results per query

    public UibkRestClient(String mailTo, String apiUrl, String clientId, String clientSecret) {
        super();

        if (!apiUrl.endsWith("/")) {
            apiUrl += "/";
        }

        this.apiUrl = apiUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        pageSize = 500;

        userAgent = String.format("%s (mailto:%s)", clientName, mailTo);

    }

    public UibkRestClient(String mailTo, String clientId, ClientParameter parameter) {
        this(mailTo, parameter.getUrl(), clientId, parameter.getApiKey());
    }

    public String requestAccessToken() {
        StringBuilder url = new StringBuilder(apiUrl + "oauth/token/client_credentials?grant_type=client_credentials");
        url.append("&client_id=" + clientId);
        url.append("&client_secret=" + clientSecret);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url.toString(), HttpMethod.GET, null, String.class);

        if (responseEntity.getBody() == null) {
            throw new RestClientException("Could not get access token");
        }
        Map<String, String> responseMap = KeyValueParser.parseString(responseEntity.getBody());
        String accessToken = responseMap.get("access_token");
        if (accessToken == null) {
            throw new RestClientException("Could not get access token");
        }
        return accessToken;
    }

    public void fetchPublications(ResultResponseHandler<Publication> resultResponseHandler, LocalDate modifiedAfter) {

        ObjectMapper objectMapper = ObjectMapperFactory.create();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        StringBuilder url = new StringBuilder(apiUrl + "ws/publications?");
        url.append("limit=").append(pageSize);
        if (modifiedAfter != null) {
            url.append("&since=").append(formatDate(modifiedAfter));
        }

        String accessToken = requestAccessToken();

        String parameters = "&access_token=%s";
        url.append(parameters);
        String cursor = null;
        try {
            int page = 1;
            int retry = 0;
            while (true) {
                String currentUrl = String.format(url.toString(), accessToken);
                if (cursor != null) {
                    currentUrl += String.format("&cursor=%s", cursor);
                }
                logger.debug(String.format("page = %6d\t%s", page, currentUrl));

                try {
                    ResponseEntity<String> responseEntity;
                        responseEntity = restTemplate.exchange(
                                currentUrl, HttpMethod.GET, headersEntity(), String.class);
                    if (responseEntity.getBody() == null) {
                        break;
                    }
                    PublicationResponseUibk publicationResponse;
                    try {
                        String responseBody = responseEntity.getBody();
                        publicationResponse = objectMapper.readValue(responseBody, PublicationResponseUibk.class);
                        Error error = errorFrom(publicationResponse);
                        if (error != null && !error.getError().equals("200")) {
                            if (error.getError().equals("401")) {
                                // Expired bearer token
                                // we need a new access token
                                accessToken = requestAccessToken();
                                continue;
                            } else {
                                logger.error("{} {}", error.getError(), error.getErrorDescription());
                                break;
                            }
                        }
                        boolean stop = resultResponseHandler.handleResponse(publicationResponse.getItems());
                        if (stop) {
                            break;
                        }
                        cursor = publicationResponse.getCursor();
                        if (cursor == null || cursor.isEmpty()) {
                            break;
                        }
                    } catch (JsonProcessingException e) {
                          throw new RuntimeException(e);
                    }

                    page++;
                    retry = 0;  // reset retry counter
                } catch (ResourceAccessException e) {
                    logger.error(e.getMessage(), e);
                    if (retry > 3) {
                        // too many tries, throw Exception
                        throw e;
                    }
                    retry++; // try again with the same offset
                    SleepSupport.sleep((long) retry * 1000 * 10);  // wait 10 x retry seconds
                }
            }
        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            resultResponseHandler.onError(e);
            throw e;
        }

        resultResponseHandler.onFinished();
    }

    private Error errorFrom(PublicationResponseUibk publicationResponse) {
        Error error = null;
        JsonNode errorObj = publicationResponse.getError();
        if (errorObj != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                error = mapper.treeToValue(errorObj, Error.class);
                if (error.getError() == null) {
                    return null;
                }
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return error;
    }


    private static String formatDate(LocalDate date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        return date.atStartOfDay().atOffset(ZoneOffset.UTC).format(dtf);
    }

    private HttpEntity<String> headersEntity() {
        return null;
    }

}
