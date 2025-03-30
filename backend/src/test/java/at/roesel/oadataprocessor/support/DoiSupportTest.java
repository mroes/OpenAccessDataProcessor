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

package at.roesel.oadataprocessor.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoiSupportTest {

    @Test
    void fileNameForDoi() {
        String doi = "10.1155/2014/413629";
        assertEquals("10.1155_2014_413629", DoiSupport.fileNameForDoi(doi));
    }

    @Test
    void parseDoi() {
        String doi = "10.1093/oso/9780198767664.003.0046";

        String identifier1 = "https://doi.org/" + doi;
        assertEquals(doi, DoiSupport.parseDoi(identifier1));

        String identifier2 = "https://doi.org/DOI: " + doi;
        assertEquals(doi, DoiSupport.parseDoi(identifier2));

        String identifier3 = "https://doi.org/";
        assertNull(DoiSupport.parseDoi(identifier3), "no doi in identifer");

        String identifier4 = doi;
        assertEquals(doi, DoiSupport.parseDoi(identifier4));

        String identifier5 = " " + doi;
        assertEquals(doi, DoiSupport.parseDoi(identifier5));

    }

    @Test
    void parseNonDoi() {
        String identifier1 = "info:eu-repo/semantics/altIdentifier/arxiv/1910.12628";
        assertNull(DoiSupport.parseDoi(identifier1));
    }


    @Test
    void parseDoi2() {
        String doi = "10.1348/000711010X519320";

        String identifier1 = "https://doi.org/https://psycnet.apa.org/doi/10.1348/000711010X519320";
        assertEquals(doi, DoiSupport.parseDoi(identifier1));
    }

    @Test
    void parseDoi3() {
        String doi = "10.1024//1422-4917.29.1.37";

        String identifier1 = "https://doi.org/" + doi;
        assertEquals(doi, DoiSupport.parseDoi(identifier1));
    }

    @Test
    void parseDoi4() {
        String doi = "10.5586%2Fasbp.2007.018";
        String doiresult = "10.5586/asbp.2007.018";

        String identifier1 = "https://doi.org/" + doi;
        assertEquals(doiresult, DoiSupport.parseDoi(identifier1));
    }

    @Test
    void parseDoi5() {
        String doiresult = "10.1080/13691066.2022.2139206";

        String identifier1 = "https://www.tandfonline.com/doi/full/10.1080/13691066.2022.2139206";
        assertEquals(doiresult, DoiSupport.parseDoi(identifier1));
    }

    @Test
    void parseDoi6() {
        String doiresult = "10.1145/1989493.1989507";

        String identifier1 = "10.1145/1989493.1989507\n" +
                "Host";
        assertEquals(doiresult, DoiSupport.parseDoi(identifier1));
    }

    @Test
    void parseDoiInText() {
        String doiresult = "10.1145/1989493.1989507";

        String identifier1 = "blablabla 10.1145/1989493.1989507";
        assertEquals(doiresult, DoiSupport.parseDoi(identifier1));
    }


    @Test
    void parseDoiInUrl() {
        String doiresult = "10.1007/978-3-030-45237-7_5";

        String identifier1 = "doi:<a href=\"https://doi.org/10.1007/978-3-030-45237-7_5\">10.1007/978-3-030-45237-7_5</a>";
        assertEquals(doiresult, DoiSupport.parseDoi(identifier1));
    }

    @Test
    void checkDoi() {

        String doi = "10.1024";
        assertFalse(DoiSupport.checkDoi(doi));

        doi = "10.102/";
        assertFalse(DoiSupport.checkDoi(doi));

        doi = "10.1024";
        assertFalse(DoiSupport.checkDoi(doi));

        doi = "10.1024/";
        assertFalse(DoiSupport.checkDoi(doi));

        doi = "0.1024//1422-4917.29.1.37";
        assertFalse(DoiSupport.checkDoi(doi));

        doi = "10.1024/1";
        assertTrue(DoiSupport.checkDoi(doi));

        doi = "10.1024//1422-4917.29.1.37";
        assertTrue(DoiSupport.checkDoi(doi));


    }

    @Test
    void prefix() {
        String doi = "10.1093/oso/9780198767664.003.0046";
        assertEquals("10.1093", DoiSupport.prefix(doi));
    }

    @Test
    void suffix() {
        String doi = "10.1093/oso/9780198767664.003.0046";
        assertEquals("oso/9780198767664.003.0046", DoiSupport.suffix(doi));
    }
}
