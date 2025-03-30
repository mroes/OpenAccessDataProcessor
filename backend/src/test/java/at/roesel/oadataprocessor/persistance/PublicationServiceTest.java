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

import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.ui.PublicationColor;
import at.roesel.oadataprocessor.services.PublicationService;
import at.roesel.oadataprocessor.services.PublisherService;
import at.roesel.oadataprocessor.services.publisher.MainPublisherSupplier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
class PublicationServiceTest {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublisherService publisherService;

    @Test
    @Tag("manual")
    void searchForDoiInCrossref() {
        publicationService.searchForDoiInCrossref();
    }

    @Test
    @Tag("manual")
    void readStats() {
        publicationService.readColorsPerYearAndInstitution();
    }

    @Test
    @Tag("manual")
    void readPublishersFromPublications() {
        publicationService.readPublishersFromPublications();
    }

    @Test
    @Tag("manual")
    void readTopPublishers() {
        List<Publisher> result = publicationService.readTopPublishers(10);
        System.out.println(result);
    }


    @Test
    @Tag("manual")
    void readColorsPerInstitution() {
        publicationService.readColorsPerInstitution();
    }

    @Test
    @Tag("manual")
    void checkDoiInPublications() {
        publicationService.checkDoiInPublications();
    }

    @Test
    @Tag("manual")
    void fillMainPublisher() {
        List<PublicationColor> values = publicationService.readColorsPerYearAndInstitutionAndPublisher();
        Iterable<Publisher> publishers = publisherService.readPublishers();
        MainPublisherSupplier supplier = new MainPublisherSupplier(publishers, 0);
        supplier.supplyMainPublisher(values);
    }

}
