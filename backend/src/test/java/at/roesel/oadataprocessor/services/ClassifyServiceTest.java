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

import at.roesel.oadataprocessor.model.Coat;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class ClassifyServiceTest {

    @Autowired
    private ClassifyService classifyService;

    @Test
    void classifyTupleRestricted() {
        String doi = "10.1038/s41586-018-0277-x";
        Coat expected = new Coat("", 4, 3, 4, 3, 4);
        Coat coat = classifyService.classifyTuple(doi);
        assertEquals(expected, coat);
    }

    @Test
    void classifyTupleGold() {
        String doi = "10.1038/s41467-017-02159-y";
        Coat expected = new Coat("", 1, 1, 1, 1, 2);
        Coat coat = classifyService.classifyTuple(doi);
        assertEquals(expected, coat);
    }

    @Test
    void classifyTupleHybrid() {
        String doi = "10.1016/j.molp.2016.08.010";
        Coat expected = new Coat("", 1, 2, 1, 1, 3);
        Coat coat = classifyService.classifyTuple(doi);
        assertEquals(expected, coat);
    }

    @Test
    void classifyTuplePreprint() {
        String doi = "10.1103/PhysRevFluids.3.054401";
        Coat expected = new Coat("", 1, 3, 2, 4, 1);
        Coat coat = classifyService.classifyTuple(doi);
        assertEquals(expected, coat);
    }

    @Test
    void classifyTuplePostprint() {
        String doi = "10.1093/pcp/pcx118";
        Coat expected = new Coat("", 1, 4, 1, 4, 3);
        Coat coat = classifyService.classifyTuple(doi);
        assertEquals(expected, coat);
    }

    @Test
    void classifyTupleBronze() {
        String doi = "10.1073/pnas.1501343112";
        Coat expected = new Coat("", 2, 4, 1, 3, 1);
        Coat coat = classifyService.classifyTuple(doi);
        assertEquals(expected, coat);
    }

    @Test
    void classifyOther() {
        String doi = "10.18335/region.v1i1.9";
        Coat expected = new Coat("", 1, 1, 1, 1, 1);
        Coat coat = classifyService.classifyTuple(doi);
        assertEquals(expected, coat);
    }

    @Test
    void classifySingle() {
        String doi = "10.1002/2014JA020380";
        Coat expected = new Coat("", 1, 1, 1, 1, 3);
        Coat coat = classifyService.classifyTuple(doi);
        assertEquals(expected, coat);
    }

    @Test
    @Tag("manual")
    void classifyAllPublications() {
        classifyService.classifyAllPublications();
    }

}
