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

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.Journal;
import at.roesel.oadataprocessor.model.JournalVar;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.PublisherVar;
import at.roesel.oadataprocessor.model.wikidata.WikidataJsonEntities;
import at.roesel.oadataprocessor.model.wikidata.WikidataJsonEntity;
import at.roesel.oadataprocessor.model.wikidata.WikidataMonolingualtext;
import at.roesel.oadataprocessor.model.wikidata.WikidataValue;
import at.roesel.oadataprocessor.persistance.conversion.ObjectMapperFactory;
import at.roesel.oadataprocessor.support.WikidataSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static at.roesel.common.CharacterSupport.has4ByteCharacters;
import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.model.wikidata.WikidataJsonEntity.*;
import static at.roesel.oadataprocessor.support.WikidataSupport.*;

@Service
public class WikidataServiceImpl implements WikidataService {

    private final static Logger logger = LoggerFactory.getLogger(WikidataServiceImpl.class);
    private final static ObjectMapper objectMapper = ObjectMapperFactory.create();

    private final AppSettings appSettings;

    public WikidataServiceImpl(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    @Override
    public Journal searchJournal(List<String> issns) {
        WikidataClient client = new WikidataClient(appSettings.getContactEMail());

        for (String issn : issns) {
            String query = "SELECT ?item WHERE {" +
                    "  ?item wdt:P236 \"" + issn + "\"." +
                    "}";
            String response = client.executeWikidataQuery(query);

            try {
                WikidataResponse wikidataResponse = objectMapper.readValue(response, WikidataResponse.class);
                List<String> values = wikidataResponse.getValues("uri");
                if (!values.isEmpty()) {
                    if (values.size() > 1) {
                        logger.warn(String.format("Found multiple entries %s for issn = '%s'in Wikidata", response, issn));
                    }
                    String entityUrl = values.get(0);
                    logger.debug("Found journal {} with issn {} in Wikidata", entityUrl, issn);
                    String wikidataId = extractWikiId(entityUrl);
                    return fetchJournal(wikidataId);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public Journal fetchJournal(String wikidataId) {
        WikidataJsonEntity entity = fetchWikidataEntity(wikidataId);
        if (entity == null) {
            return null;
        }
        // create Journal from entity
        Journal journal = new Journal();
        // the entity could have another wikidataId because it was redirected
        journal.setWikiId(entity.id);
        WikidataMonolingualtext titleText = entity.monolingualtext(titleId);
        journal.setTitle(titleText.text);
        String name = entity.preferredLabel();
        if (name.isEmpty()) {
            name = journal.getTitle();
        }
        journal.setName(name);
        journal.setWikiInstanceOf(stringFromValues(entity.valuesForPropertyId(instanceOfId)));
        journal.setIssn(stringFromValues(entity.valuesForPropertyId(issnId)));
        List<WikidataValue> issnLValues = entity.valuesForPropertyId(issnLId);
        if (issnLValues.size() > 1) {
            // if there is more than one issnL value than take only the first
            issnLValues = issnLValues.subList(0, 0);
            logger.warn("Journal {} has multiple issnL values {} ", journal.getWikiId(), stringFromValues(issnLValues));
        }
        journal.setIssnl(stringFromValues(issnLValues));
        journal.setCrJournalId(firstValueOrEmpty(entity.valuesForPropertyId(crJournalId)));
        journal.setUpdatedWikidata(SystemTime.currentTimeMillis());

        // publisher(s)
        List<WikidataValue> publisherValues = entity.valuesForPropertyId(publisherId);
        if (publisherValues.size() == 1 && !publisherValues.get(0).hasTime()) {
            journal.setWikiPublisherId(wikiIdOrEmpty(publisherValues.get(0).value));
        } else {
            if (publisherValues.size() == 1) {
                journal.setWikiPublisherId(wikiIdOrEmpty(publisherValues.get(0).value));
            } else {
                journal.setWikiPublisherId("");
            }
            for (WikidataValue parent : publisherValues) {
                JournalVar newPublisher = new JournalVar();
                String wikiPublisher = wikiIdOrEmpty(parent.value);
                if (wikiPublisher.isEmpty()) {
                    // if there is no valid wikiPublisher there is no need for this entry
                    continue;
                }
                newPublisher.setWikiPublisherId(wikiPublisher);
                if (parent.startTime != null) {
                    newPublisher.setStartDate(localDateFrom(parent.startTime));
                }
                if (parent.endTime != null) {
                    newPublisher.setEndDate(localDateFrom(parent.endTime));
                }
                journal.addVar(newPublisher);
            }
        }

        return journal;
    }

    @Override
    public WikidataJsonEntity fetchWikidataEntity(String wikidataId) {
        try {
            WikidataClient client = new WikidataClient(appSettings.getContactEMail());
            String entityResponse = client.fetchWikiEntity(wikidataId);
            if (!entityResponse.isEmpty()) {
                // check for error (plain text)
                if (entityResponse.startsWith("error")) {
                    int pos = entityResponse.indexOf("\"");
                    String errorMessage = pos > -1 ? entityResponse.substring(0, pos) : entityResponse;
                    logger.error("fetchWikidataEntity {}, {}", wikidataId, errorMessage);
                    return null;
                }
                WikidataJsonEntities entities = objectMapper.readValue(entityResponse, WikidataJsonEntities.class);
                WikidataJsonEntity entity = entities.entity(null);
                return entity;
            }
        } catch (JsonProcessingException e) {
            logger.error("fetchWikidataEntity {} error, {}", wikidataId, e.getMessage());
        }
        return null;
    }

    @Override
    public Publisher fetchPublisher(String wikidataId) {
        WikidataJsonEntity entity = fetchWikidataEntity(wikidataId);
        if (entity == null) {
            return null;
        }
        return createPublisherFromWikidataEntity(wikidataId, entity);
    }


    public static Publisher createPublisherFromWikidataEntity(String wikidataId, WikidataJsonEntity entity) {
        // create Publisher from entity
        String label = entity.preferredLabel();
        if (label.isEmpty()) {
            // Fallback to wikidataId as name
            label = wikidataId;
        }
        Publisher publisher = new Publisher();
        publisher.setFlag(Publisher.RELEVANT);   // default: activate the publisher
        // the entity could have another wikidataId because it was redirected
        publisher.setWikiId(entity.id);
        publisher.setName(label);
        publisher.setWikiInstanceOf(stringFromValues(entity.valuesForPropertyId(instanceOfId)));
        publisher.setIsni(stringFromValues(entity.valuesForPropertyId(isniId), WikidataSupport::normalizeIsni));
        publisher.setRinggoldId(stringFromValues(entity.valuesForPropertyId(ringgoldId)));
        publisher.setRor(stringFromValues(entity.valuesForPropertyId(rorId)));
        publisher.setRomeoId(stringFromValues(entity.valuesForPropertyId(romeoiId)));
        publisher.setUpdatedWikidata(SystemTime.currentTimeMillis());

        String labelEnglish = entity.label("en");
        if (hasValue(labelEnglish) && !labelEnglish.equals(label)) {
            publisher.addAlias(labelEnglish);
        }

        // Alternate names
        List<String> aliases = entity.aliases("de");
        aliases.addAll(entity.aliases("en"));
        for (String alias : aliases) {
            if (!has4ByteCharacters(alias)) {
                publisher.addAlias(alias);
            } else {
                logger.warn("publisher {} has 4byte character in alias", publisher.getWikiId());
            }
        }

        List<WikidataValue> parentValues = entity.valuesForPropertyId(parentOrganizationPropId);
        if (parentValues.size() == 1 && !parentValues.get(0).hasTime()) {
            publisher.setWikiParentId(wikiIdOrEmpty(parentValues.get(0).value));
        } else {
            if (parentValues.size() == 1) {
                publisher.setWikiParentId(wikiIdOrEmpty(parentValues.get(0).value));
            } else {
                publisher.setWikiParentId("");
            }
            for (WikidataValue parent : parentValues) {
                PublisherVar newParent = new PublisherVar();
                String wikiParentPublisher = wikiIdOrEmpty(parent.value);
                if (wikiParentPublisher.isEmpty()) {
                    // if there is no valid wikiParentPublisher there is no need for this entry
                    continue;
                }
                newParent.setWikiParentId(wikiParentPublisher);
                if (parent.startTime != null) {
                    newParent.setStartDate(localDateFrom(parent.startTime));
                }
                if (parent.endTime != null) {
                    newParent.setEndDate(localDateFrom(parent.endTime));
                }
                publisher.addPublisherVar(newParent);
            }
        }

        return publisher;
    }

}

