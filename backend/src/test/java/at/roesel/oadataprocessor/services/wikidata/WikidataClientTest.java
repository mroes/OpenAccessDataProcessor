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

import at.roesel.oadataprocessor.model.wikidata.WikidataJsonEntities;
import at.roesel.oadataprocessor.model.wikidata.WikidataJsonEntity;
import at.roesel.oadataprocessor.model.wikidata.WikidataValue;
import at.roesel.oadataprocessor.persistance.conversion.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static at.roesel.common.TestResourceHelper.fileForTestResource;
import static at.roesel.common.TestResourceHelper.readProperty;
import static at.roesel.oadataprocessor.model.wikidata.WikidataJsonEntity.parentOrganizationPropId;
import static at.roesel.oadataprocessor.support.FileSupport.readStringFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WikidataClientTest {

    private static String contactEmail;

    @BeforeAll
    static void setUpBeforeClass() {
        contactEmail = readProperty("contact.email");
    }

    @Test
    @Tag("manual")
    void fetchPublishers() {
        WikidataClient client = new WikidataClient(contactEmail);
        String response = client.fetchPublishers(LocalDate.of(2024, 1, 1));
        assertNotNull(response);

    }

    @Test
    @Tag("manual")
    void fetchWikiEntity() {
        WikidataClient client = new WikidataClient(contactEmail);
        String result = client.fetchWikiEntity("Q106825143");
        System.out.println(result);
    }

    @Test
    void readWikiEntity() {
        File jsonFile = fileForTestResource("Q106825143.json");

        String contents = readStringFromFile(jsonFile.getAbsolutePath());
        if (!contents.isEmpty()) {
            ObjectMapper mapper = ObjectMapperFactory.create();
            try {
                WikidataJsonEntities entities = mapper.readValue(contents, WikidataJsonEntities.class);
                WikidataJsonEntity entity = entities.entity(null);
                List<WikidataValue> values = entity.valuesForPropertyId(parentOrganizationPropId);
                assertEquals(2, values.size());
                assertEquals("Q3067653", values.get(0).value);
                assertEquals("Q2881141", values.get(1).value);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Test
    @Tag("manual")
    void executeWikidataQuery() {
        WikidataClient client = new WikidataClient(contactEmail);
        String query = "SELECT ?item WHERE {" +
                "  ?item wdt:P236 \"2398-9629\"." +
                "}";
        String result = client.executeWikidataQuery(query);
        assertNotNull(result);
    }

}
