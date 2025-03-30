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

import at.roesel.common.StringSupport;
import at.roesel.oadataprocessor.model.json.JsonAuthor;
import at.roesel.oadataprocessor.services.publicationsource.BokuNameParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BokuNameParserTest {

    @Test
    void authorsFromString() {
        List<JsonAuthor> authors = BokuNameParser.authorsFromString("Schwen A, Hernandez-Ramirez G, Buchan G, Carrick S");
        assertEquals(4, authors.size());

        authors = BokuNameParser.authorsFromString("Hauer, C., Rudolf-Miklau, F., Suda, J., Brandl, H., Blovsky, S., Hübl, J., Holub, M., Habersack, H.");
        assertEquals(8, authors.size());
    }

    @Test
    void authorFromString() {
        JsonAuthor author = BokuNameParser.authorFromString("Family, G");
        assertEquals("Family", author.getFamily());
        assertEquals("G", author.getGiven());

        author = BokuNameParser.authorFromString("Given Family");
        assertEquals("Family", author.getFamily());
        assertEquals("Given", author.getGiven());

        author = BokuNameParser.authorFromString("Family G");
        assertEquals("Family", author.getFamily());
        assertEquals("G", author.getGiven());

        author = BokuNameParser.authorFromString("Family H.G.");
        assertEquals("Family", author.getFamily());
        assertEquals("H.G.", author.getGiven());

    }

    @Test
    void testAuthorsFromString() {
        String rawAuthors = StringSupport.stripSpecialCharacters("Z&#780;alud, Z., Trnka, M., Hlavinka, P., Dubrovsky&#769;, M., Svobodova&#769;, E.,  Semera&#769;dova&#769;, D., Bartos&#780;ova&#769;, L., Balek, J., Eitzinger J., Moz&#780;ny, M. ");
        List<JsonAuthor> authors = BokuNameParser.authorsFromString(rawAuthors);
        assertEquals(10, authors.size());
    }
}
