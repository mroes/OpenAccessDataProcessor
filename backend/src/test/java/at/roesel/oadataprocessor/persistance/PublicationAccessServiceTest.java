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

package at.roesel.oadataprocessor.persistance;

import at.roesel.oadataprocessor.components.controller.PublicationSearchFilter;
import at.roesel.oadataprocessor.model.PublicationFlat;
import at.roesel.oadataprocessor.services.PublicationAccessService;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
class PublicationAccessServiceTest {

    @Autowired
    private PublicationAccessService publicationAccessService;

    @Test
    @Tag("manual")
    @Disabled
    void storePublicationsInElastic() {
        publicationAccessService.storePublicationsInElastic();
    }

    @Test
    @Tag("manual")
    @Disabled
    void createElasticIndex() {
        publicationAccessService.createElasticIndex();
    }

    @Test
    @Tag("manual")
    void storeSinglePublicationInElastic() {
        IndexResponse response = publicationAccessService.storeSinglePublicationInElastic("09872287-739f-45ba-aeaa-641ee8d55642");
        assertNotNull(response);
    }

    @Test
    @Tag("manual")
    void readPublicationsFromElastic() {
        PublicationSearchFilter filter = new PublicationSearchFilter();
        filter.publisherId = "3e087a3c-88dc-4937-afaa-9acbab943901";
        filter.author="Altendorfer-Kaiser Susanne";
        List<PublicationFlat> publications = publicationAccessService.readPublicationsFromElastic(filter);
        assertNotNull(publications);

    }
}
