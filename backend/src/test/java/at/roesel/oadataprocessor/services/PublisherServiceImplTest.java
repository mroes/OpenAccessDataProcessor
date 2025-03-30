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

package at.roesel.oadataprocessor.services;

import at.roesel.oadataprocessor.services.publisher.PublisherIdentifyResult;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PublisherServiceImplTest {

    @Autowired
    private PublisherService publisherService;

    @Test
    @Tag("manual")
    void identifyPublisher() {
        PublisherIdentifyResult result = publisherService.identifyPublisher("Oxford University PressOxford");
        assertEquals("Oxford University Press", result.getPublisher().getName());
    }

    @Test
    @Tag("manual")
    void fuzzyCompare() {
        int ratio = FuzzySearch.ratio("Sage publications", "Sage publishing");
        System.out.println(ratio);
        ratio = FuzzySearch.ratio("SAGE", "sage");
        System.out.println(ratio);
        ratio = FuzzySearch.ratio("Society for Industrial & Applied Mathematics", "Society for Industrial and Applied Mathematics");
        System.out.println(ratio);
        ratio = FuzzySearch.ratio("Pleiades Publishing Ltd", "Pleiades Publishing");
        System.out.println(ratio);
        ratio = FuzzySearch.ratio("Verlag Vittorio Klostermann", "Vittorio Klostermann GMBH");
        System.out.println(ratio);
        ratio = FuzzySearch.ratio("DE GRUYTER", "de Gruyter");
        System.out.println(ratio);

    }

    @Test
    @Tag("manual")
    void updatePublishersfromWikidata() {
        publisherService.updatePublishersfromWikidata(LocalDate.of(2022,1,1));
    }

    @Test
    @Tag("manual")
    void updateJournalsfromWikidata() {
        publisherService.updateJournalsfromWikidata(false);
    }

    @Test
    @Tag("manual")
    void removeRedirectedPublishers() {
        publisherService.removeRedirectedPublishers();
    }

}
