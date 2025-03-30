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

package at.roesel.oadataprocessor.services.crossref;

import at.roesel.oadataprocessor.model.crossref.CrossrefPrefix;
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CrossrefServiceTest {

    @Autowired
    private CrossrefService crossrefService;

    @Test
    @Tag("manual")
    public void crossref_test() {
        String doi = "10.7767/9783205213048";

        CrossrefWork crossrefWork = crossrefService.crossrefWork(doi);
        System.out.println("doi = " + crossrefWork.doi);
    }

    @Test
    @Tag("manual")
    public void getCrossrefWorkTest() {
        String doi = "10.1038/s41467-017-02159-y2";
        CrossrefWork crossrefWork = crossrefService.getCrossrefWork(doi);
        System.out.println("doi = " + crossrefWork.doi);
    }

    @Test
    @Tag("manual")
    public void prefix_test() {
        CrossrefPrefix crossrefPrefix = crossrefService.crossrefPrefix("10.1002");
        System.out.println("publisher = " + crossrefPrefix.name);
    }

    @Test
    @Tag("manual")
    public void search_test() {
        String author = null;
        String title = "Improving the Quality of East and {West} European Public Services";
        CrossrefDoiSearchResult searchResult = crossrefService.searchWork(author, title);
        assertNotNull(searchResult.works);
    }

}
