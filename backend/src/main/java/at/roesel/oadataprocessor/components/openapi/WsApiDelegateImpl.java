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

package at.roesel.oadataprocessor.components.openapi;

import at.roesel.oadataprocessor.components.controller.PublicationSearchFilter;
import at.roesel.oadataprocessor.model.PublicationFlat;
import at.roesel.oadataprocessor.openapi.api.WsApiDelegate;
import at.roesel.oadataprocessor.openapi.model.Error;
import at.roesel.oadataprocessor.openapi.model.Publication;
import at.roesel.oadataprocessor.openapi.model.PublicationResponse;
import at.roesel.oadataprocessor.services.PublicationAccessService;
import at.roesel.oadataprocessor.persistance.elastic.ElasticCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WsApiDelegateImpl implements WsApiDelegate {

    @Autowired
    private PublicationAccessService publicationAccessService;

    private final Logger logger = LoggerFactory.getLogger(WsApiDelegateImpl.class);

    public WsApiDelegateImpl() {
    }


    @Override
    public ResponseEntity<Publication> getPublication(String publicationId) {
        logger.debug("getPublication {}", publicationId);
        try {
            PublicationFlat result = publicationAccessService.searchPublicationFromElasticById(publicationId);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            PublicationConverter publicationConverter = new PublicationConverter(true);
            return new ResponseEntity<>(publicationConverter.from(result), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<PublicationResponse> handleException(Exception e) {
        logger.error("", e);
        PublicationResponse response = new PublicationResponse();
        Error error = new Error();
        error.setErrorDescription(e.getMessage());
        response.setError(error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<PublicationResponse> listPublications(Integer limit, String cursor, String doi, String institution,
                                                                Integer year, String oacolor, List<String> include) {
        PublicationResponse response = new PublicationResponse();
        logger.debug("listPublications()");
        try {
            PublicationConverter publicationConverter = new PublicationConverter(include);
            PublicationSearchFilter filter = new PublicationSearchFilter();
            filter.doi = doi;
            filter.institution = institution;
            if (year != null) {
                filter.year = year;
            }
            filter.color = oacolor;


            ElasticCursor elasticCursor;
            if (cursor != null && !cursor.isEmpty()) {
                elasticCursor = ElasticCursor.fromString(cursor);
            } else {
                elasticCursor = new ElasticCursor();
            }
            if (limit == null) {
                limit = 0;
            }
            List<PublicationFlat> publication = publicationAccessService.readPublicationsFromElastic(filter, elasticCursor, limit);
            response.setCursor(elasticCursor.toString());
            response.setItems(publication.stream().map(publicationConverter::from).collect(Collectors.toList()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("", e);
            Error error = new Error();
            error.setErrorDescription(e.getMessage());
            response.setError(error);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
