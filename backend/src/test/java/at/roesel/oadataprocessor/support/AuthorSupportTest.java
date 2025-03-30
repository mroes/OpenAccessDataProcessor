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

import at.roesel.oadataprocessor.model.Author;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorSupportTest {

    @Test
    void authorsFromStringList() {
    }

    @Test
    void authorFromString() {
        Author author1 = AuthorSupport.authorFromString("Ford, Harrison C.");
        assertEquals("Ford", author1.lastName);
        assertEquals("Harrison C.", author1.firstName);
    }

    @Test
    void initialFromName() {
        String result = AuthorSupport.initialFromName("Harrison Collin");
        assertEquals("HC", result);

        result = AuthorSupport.initialFromName("Harrison C.");
        assertEquals("HC", result);

        result = AuthorSupport.initialFromName("H.C.");
        assertEquals("HC", result);

        result = AuthorSupport.initialFromName("H.C");
        assertEquals("HC", result);

        result = AuthorSupport.initialFromName("HC");
        assertEquals("HC", result);

        result = AuthorSupport.initialFromName("H");
        assertEquals("H", result);

        result = AuthorSupport.initialFromName("Harrison");
        assertEquals("H", result);

        result = AuthorSupport.initialFromName("HC");
        assertEquals("HC", result);
    }

    @Test
    void containsInitial() {
        boolean result = AuthorSupport.containsInitial("Harrison Collin");
        assertFalse(result);

        result = AuthorSupport.containsInitial("H Collin");
        assertTrue(result);

        result = AuthorSupport.containsInitial("H C");
        assertTrue(result);
    }
}
