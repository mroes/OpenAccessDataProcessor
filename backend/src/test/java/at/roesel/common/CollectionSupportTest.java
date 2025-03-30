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

package at.roesel.common;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionSupportTest {

    @Test
    void isEqualCollection() {
        List<String> coll1 = Arrays.asList("a", "b", "c");
        List<String> coll2 = Arrays.asList("c", "a", "b");
        boolean result = CollectionSupport.isEqualCollection(coll1, coll2, String::compareTo);
        assertTrue(result);

        List<String> coll3 = Arrays.asList("c", "a", "f");
        result = CollectionSupport.isEqualCollection(coll1, coll3, String::compareTo);
        assertFalse(result);
    }

    @Test
    void find() {
        List<String> coll = Arrays.asList("a", "b", "c");
        String result = CollectionSupport.find(coll, "b", String::compareTo);
        assertEquals("b", result);
    }

    @Test
    void findNot() {
        List<String> coll = Arrays.asList("a", "b", "c");
        String result = CollectionSupport.find(coll, "d", String::compareTo);
        assertNull(result);
    }

    @Test
    void collectionToString() {
        List<String> coll = Arrays.asList("a", "b", "c");
        String result = CollectionSupport.collectionToString(coll, ",");
        assertEquals("a,b,c", result);
    }
}
