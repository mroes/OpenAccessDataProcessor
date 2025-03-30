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

import static at.roesel.oadataprocessor.support.IdentifierSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class IdentifierSupportTest {

    @Test
    void extractIdFromUrlTest() {
        assertEquals("05j0w0e76", extractIdFromUrl("https://ror.org/05j0w0e76"));
    }

    @Test
    void coarIdFromUrlTest() {
        assertNull(coarIdFromUrl("info:eu-repo/semantics/bookPart"));
        assertEquals(coarPrefix + "c_3248", coarIdFromUrl("http://purl.org/coar/resource_type/c_3248"));
    }

    @Test
    void coarIdFromUrlTest2() {
        assertEquals(coarPrefix + "c_f744", coarIdFromUrl("https://vocabularies.coar-repositories.org/resource_types/c_f744/"));
    }
}
