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

import at.roesel.oadataprocessor.services.PublicationAccessService;
import at.roesel.oadataprocessor.services.PublisherIdentificationService;
import at.roesel.oadataprocessor.services.PublisherService;
import at.roesel.oadataprocessor.services.WorkService;
import at.roesel.oadataprocessor.services.common.SpringBeanMethodInvoker;
import at.roesel.oadataprocessor.services.publicationsource.PublicationSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Autowired
    private WorkService workService;
    @Autowired
    private PublicationSourceService publicationSourceService;
    @Autowired
    private PublicationAccessService publicationAccessService;
    @Autowired
    private PublisherIdentificationService publisherIdentificationService;
    @Autowired
    private PublisherService publisherService;
    @Autowired
    SpringBeanMethodInvoker springBeanMethodInvoker;

    public AdminController() {
    }

    @GetMapping(path = "cmd/{cmd}")
    @ResponseStatus(HttpStatus.OK)
    public void executeCommand(@PathVariable String cmd, Principal principal) {
        logger.debug("executeCommand {} called", cmd);

        if (cmd.equals("state")) {
            // allow for everyone
        } else {
            if (!hasRole("admin", principal)) {
                throw new AccessDeniedException("You do not have the required role");
            }
        }

        executor.submit(() -> {
            switch (cmd) {
                case "state":
                    workService.sendStateToClient();
                    break;
                case "collect":
                    workService.collectPublications();
                    break;
                case "ingest":
                    workService.ingestPublications();
                    break;
                case "classify":
                    workService.classifyPublications();
                    break;
                case "fetch":
                    publicationSourceService.fetchFromActiveInstitutions();
                    break;
                case "update":
                    publicationSourceService.updatePublications(null);
                    break;
                case "identifyJournals":
                    publisherIdentificationService.identifyJournal();
                    break;
                case "updatePublishers":
                    publisherService.updatePublishersfromWikidata(null);
                    break;
                case "identifyPublishers":
                    publisherIdentificationService.identifyPublisher();
                    break;
                case "createElasticIndex":
                    publicationAccessService.createElasticIndex();
                    break;
                case "updateElasticData":
                    publicationAccessService.storePublicationsInElastic();
                    break;
                case "resetUnknownPublisherFlag":
                    workService.resetUnknownJournalsAndPublishersInPublications();
                    break;
                case "clearCache":
                    workService.clearCaches();
                    break;
                default:
                    springBeanMethodInvoker.execute(cmd);
            }
        });
    }

    private boolean hasRole(String role, Principal principal) {
        // Get the user's authorities
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
            return auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }

    @GetMapping(path = "processing/state")
    @ResponseStatus(HttpStatus.OK)
    public String getProcessingState() {
        return workService.getState().toString();
    }

}