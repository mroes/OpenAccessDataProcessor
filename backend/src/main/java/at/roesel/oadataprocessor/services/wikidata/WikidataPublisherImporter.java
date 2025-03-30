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

import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.wikidata.WikidataJsonPublisher;
import at.roesel.oadataprocessor.persistance.conversion.ObjectMapperFactory;
import at.roesel.oadataprocessor.services.PublisherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.support.WikidataSupport.extractWikiId;

/*
 * Result from Wikidata
  {
  "head" : {
    "vars" : [ "publisher", "date", "redirect" ]
  },
  "results" : {
    "bindings" : [ {
      "publisher" : {
        "type" : "uri",
        "value" : "http://www.wikidata.org/entity/Q681"
      },
      "date" : {
        "datatype" : "http://www.w3.org/2001/XMLSchema#dateTime",
        "type" : "literal",
        "value" : "2024-11-08T09:40:26Z"
      },
      "redirect" : {
        "type" : "uri",
        "value" : "http://www.wikidata.org/entity/Q65058584"
      }
    },
    ...
     */

    /*
the same publisher could be received multiple times if there are redirects, e.g.
18:05:36.421 [main] DEBUG a.r.o.s.wikidata.WikidataServiceImpl - Publisher item Q1522, redirected from Q12828855
18:05:38.371 [main] DEBUG a.r.o.s.wikidata.WikidataServiceImpl - Publisher item Q1522, redirected from Q12930602
18:05:39.247 [main] DEBUG a.r.o.s.wikidata.WikidataServiceImpl - Publisher item Q1522, redirected from Q19748951
18:05:40.049 [main] DEBUG a.r.o.s.wikidata.WikidataServiceImpl - Publisher item Q1522, redirected from Q65436856
 */

public class WikidataPublisherImporter {

    private final Logger logger = LoggerFactory.getLogger(WikidataPublisherImporter.class);
    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    private final AppSettings appSettings;

    private final PublisherService publisherService;
    private final PublisherUpdater publisherUpdater = new PublisherUpdater();
    private final Function<String, Publisher> fetchPublisher;

    private Map<Publisher, Publisher> redirectedPublishers = new HashMap<>();

    public WikidataPublisherImporter(AppSettings appSettings, PublisherService publisherService, Function<String, Publisher> fetchPublisher) {
        this.appSettings = appSettings;
        this.publisherService = publisherService;
        this.fetchPublisher = fetchPublisher;
    }

    public Map<Publisher, Publisher> getRedirectedPublishers() {
        return redirectedPublishers;
    }

    public void updatePublishersfromWikidata(LocalDate modifiedAfterDate) {
        try {

            Set<String> handledPublishers = new HashSet<>();
            WikidataClient client = new WikidataClient(appSettings.getContactEMail());
            String response = client.fetchPublishers(modifiedAfterDate);
            JsonNode rootNode = objectMapper.readTree(response);

            JsonNode bindingsNode = rootNode.path("results").path("bindings");
            bindingsNode.forEach(binding -> {
                WikidataJsonPublisher item = new WikidataJsonPublisher();
                binding.fields().forEachRemaining(field -> {
                    switch (field.getKey()) {
                        case "publisher":
                            item.publisher = extractWikiId(valueFromField(field.getValue()));
                            break;
                        case "redirect":
                            item.redirectFrom = extractWikiId(valueFromField(field.getValue()));
                            break;
                    }
                });
                if (logger.isDebugEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("Publisher item %s", item.publisher));
                    if (hasValue(item.redirectFrom)) {
                        sb.append(String.format(", redirect from: %s", item.redirectFrom));
                    }
                    logger.debug(sb.toString());
                }
                // only update if publisher was not already handled
                if (!handledPublishers.contains(item.publisher)) {
                    handledPublishers.add(item.publisher);
                    updatePublisherFromWikidata(item);
                }
            });

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String valueFromField(JsonNode jsonNode) {
        if (jsonNode != null && !jsonNode.isEmpty()) {
            String value = jsonNode.get("value").toString();
            if (value != null && !value.isEmpty()) {
                return value.replaceAll("^\"|\"$", "");
            }
        }
        return null;
    }

    public void updatePublisherFromWikidata(WikidataJsonPublisher wikidataPublisher) {
        String redirected = wikidataPublisher.redirectFrom;
        Publisher publisher = fetchPublisher.apply(wikidataPublisher.publisher);
        Publisher existingPublisher = publisherService.findByWikiId(publisher.getWikiId());
        if (existingPublisher == null) {
            // only save a new publisher that is not redirected
            if (!hasValue(redirected)) {
                existingPublisher = publisherService.save(publisher);
            }
        } else {
            boolean changed = publisherUpdater.updatePublisher(existingPublisher, publisher);
            if (changed) {
                logger.info("Publisher {} {} has changed", existingPublisher.getWikiId(), existingPublisher.getName());
                existingPublisher.setUpdatedWikidata(publisher.getUpdatedWikidata());
                existingPublisher = publisherService.save(existingPublisher);
            }
        }

        // store redirected publisher for later processing
        if (hasValue(redirected)) {
            try {
                Publisher redirectedPublisher = publisherService.findByWikiId(redirected);
                if (redirectedPublisher != null) {
                    logger.debug(String.format("found redirected publisher %s for %s", redirectedPublisher.getId(), redirected));
                    redirectedPublishers.put(redirectedPublisher, existingPublisher);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
