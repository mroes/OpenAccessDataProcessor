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

package at.roesel.oadataprocessor.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoatTest {

    private final static Coat gold = new Coat("gold", 1, 4, 1, 1, 3);
    private final static Coat green = new Coat("green", 2, 4, 4, 4, 1);
    private final static Coat closed = new Coat("closed", 4, 4, 4, 4, 4);

    @Test
    void isLowerGold() {
        assertTrue(gold.isLower(gold));
        assertFalse(green.isLower(gold));
        assertFalse(closed.isLower(gold));
    }

    @Test
    void isLowerGreen() {
        assertFalse(gold.isLower(green));
        assertTrue(green.isLower(green));
        assertFalse(closed.isLower(green));
    }

    @Test
    void isLowerClosed() {
        assertTrue(gold.isLower(closed));
        assertTrue(green.isLower(closed));
        assertTrue(closed.isLower(closed));
    }

    @Test
    void coat2str() {
        Coat coat = gold;
        assertEquals("1,4,1,1,3", coat.buildString());
    }

    @Test
    void fromString() {
        assertEquals(gold, Coat.fromString("1,4,1,1,3"));
    }
}