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

import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.services.openalex.OpenAlexService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OpenAlexServiceImplTest {

    @Autowired
    private OpenAlexService openAlexService;

    @Test
    @Tag("manual")
    public void testWorks() {

        String filter = "from_publication_date:2020-01-01,authorships.institutions.country_code:AT";
        List<OpenAlexWork> works = openAlexService.works(filter);
        System.out.println("Anzahl = " + works.size());
    }

    @Test
    @Tag("manual")
    public void testWork() {
        String doi = "10.1088/2516-1075/ac4eeb";
        OpenAlexWork work = openAlexService.getWork(doi);
        System.out.println(work.display_name);
    }

    @Test
    @Tag("manual")
    public void testGetWork() {
        OpenAlexWork result = openAlexService.getWork("10.1007/s10354-021-00872-4");
        assertNotNull(result);
    }

}
