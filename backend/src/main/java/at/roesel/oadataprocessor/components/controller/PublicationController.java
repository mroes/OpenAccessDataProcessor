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

package at.roesel.oadataprocessor.components.controller;

import at.roesel.oadataprocessor.model.PublicationFlat;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.ui.*;
import at.roesel.oadataprocessor.services.PublicationAccessService;
import at.roesel.oadataprocessor.services.PublicationService;
import at.roesel.oadataprocessor.services.DataService;
import at.roesel.oadataprocessor.services.UiSupportService;
import at.roesel.oadataprocessor.support.InputValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static at.roesel.common.Encoding.decodeBase64;

@RestController
@RequestMapping("/api/publication")
public class PublicationController {

    @Autowired
    private DataService dataService;

    @Autowired
    private UiSupportService uiSupportService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationAccessService publicationAccessService;

    private final Logger logger = LoggerFactory.getLogger(PublicationController.class);

    public PublicationController() {
    }

    @GetMapping(path = "/commondata")
    public CommonData commonData() {
        return dataService.commonData();
    }

    @GetMapping(path = "/publisherdata")
    public PublisherData publisherData() {
        return dataService.publisherData();
    }

    @GetMapping(path = "/stats")
    public PublicationPublisherStats publicationStats() {
        logger.debug("publicationStats()");
        return dataService.publicationStats();
   }

    @GetMapping(path = "/stats/publicationtype")
    public PublicationStatsPair publicationStatsByPublicationType() {
        logger.debug("publicationStatsByPublicationType()");
        return dataService.publicationStatsByPublicationType();
    }

    @GetMapping(path = "/stats/licence")
    public PublicationStatsPair publicationStatsByLicence() {
        logger.debug("publicationStatsByLicence()");
        return dataService.publicationStatsByLicence();
    }

    @GetMapping(path = "/publishers")
    public List<Publisher> publishersFromPublication()  {
        logger.debug("readPublishersFromPublications()");
        List<Publisher> result =  publicationService.readPublishersFromPublications();
        result.sort(Comparator.comparing(Publisher::getName));
        return result;
    }

    @GetMapping(path = "/classify/{doi}")
    // DOI is encoded in Base64 because of possible special characters, especially `/`
    public ClassificationResult classifyPublication(HttpServletRequest request, @PathVariable("doi") String doi) throws IOException {
        doi = decodeBase64(doi);

        if (InputValidator.containsForbiddenCode(doi)) {
            throw new RuntimeException("Die Eingabe ist aus Sicherheitsgründen nicht zulässig");
        }

        logger.debug("classifyPublication() for doi: {}", doi);
        String ip = request.getRemoteAddr();
        return uiSupportService.classifyPublication(doi, ip);
    }

    @GetMapping(path = "/{id}")
    public PublicationFlat publication(@PathVariable("id") String id) throws IOException {
        logger.debug("publication()");
        return publicationAccessService.readPublicationFromElastic(id);
    }

    @PostMapping(path = "/list")
    public Iterable<PublicationHeader> publications(@RequestBody PublicationSearchFilter filter) throws IOException {
        logger.debug("publications()");

        return publicationAccessService.readPublicationsFromElastic(filter).stream().map(PublicationHeader::from).collect(Collectors.toList());
    }

}
