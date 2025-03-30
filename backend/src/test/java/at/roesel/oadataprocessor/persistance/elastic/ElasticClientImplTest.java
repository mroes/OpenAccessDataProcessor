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

package at.roesel.oadataprocessor.persistance.elastic;

import at.roesel.oadataprocessor.components.controller.PublicationSearchFilter;
import at.roesel.oadataprocessor.config.ElasticsearchSettings;
import at.roesel.oadataprocessor.model.PublicationFlat;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static at.roesel.oadataprocessor.persistance.elastic.QuerySupport.buildQueryForFilter;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ElasticClientImplTest {

    @Autowired
    private ElasticsearchSettings settings;

    @Test
    @Tag("manual")
    void readById() {
        ElasticClient elasticClient = new ElasticClientImpl(settings);
        PublicationFlat publication = elasticClient.readById(PublicationFlat.class, "pubtest", "2a916ee5-b119-443a-8178-14f95a689175");
        assertNotNull(publication);
    }

    @Test
    @Tag("manual")
    void search() {
        ElasticClient elasticClient = new ElasticClientImpl(settings);

        List<PublicationFlat> publications2 = elasticClient.search(PublicationFlat.class, "pubtest", "sources.institutionId", "https://ror.org/03gnh5541");
        assertNotNull(publications2);
    }

    @Test
    @Tag("manual")
    void search2() {
        ElasticClient elasticClient = new ElasticClientImpl(settings);
        PublicationSearchFilter filter = new PublicationSearchFilter();

        List<PublicationFlat> publications = elasticClient.search(PublicationFlat.class, "pubtest", buildQueryForFilter(filter));
        assertNotNull(publications);
        System.out.println(publications.size());

    }

    @Test
    @Tag("manual")
    void pagedSearch() {
        ElasticClient elasticClient = new ElasticClientImpl(settings);
        PublicationSearchFilter filter = new PublicationSearchFilter();
        filter.institution = "https://ror.org/057ff4y42";
        filter.year = 2020;

        ElasticCursor cursor = new ElasticCursor();
        List<PublicationFlat> publications = elasticClient.pagedSearch(PublicationFlat.class, "pubtest", buildQueryForFilter(filter), 5, cursor);
        assertNotNull(publications);
        System.out.println(publications.size());
        String strCursor = cursor.toString();
        cursor = ElasticCursor.fromString(strCursor);
        List<PublicationFlat> publications2 = elasticClient.pagedSearch(PublicationFlat.class, "pubtest", buildQueryForFilter(filter), 5, cursor);
        assertNotNull(publications2);
    }

    @Test
    @Tag("manual")
    void create() {
        ElasticClient elasticClient = new ElasticClientImpl(settings);
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index("pubnew")
                .mappings(new TypeMapping.Builder()
                        .properties("doi", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("doiSource", builder -> builder.integer(builder1 -> builder1.index(true)))
                        .properties("pubmedId", builder -> builder.integer(builder1 -> builder1.index(true)))
                        .properties("title", builder -> builder.text(builder1 -> builder1.index(true)))
                        .properties("year", builder -> builder.integer(builder1 -> builder1.index(true)))
                        .properties("date", builder -> builder.integer(builder1 -> builder1.index(true)))
                        .properties("pubtype", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("coat", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("color", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("colorUPW", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .properties("publisher", builder -> builder.text(builder1 -> builder1.index(true)))
                        .properties("publisherId", builder -> builder.keyword(builder1 -> builder1.index(true)))
                        .build())
                .build();
        CreateIndexResponse response = elasticClient.createIndexRequest(createIndexRequest);
        System.out.println(response);

    }

}
