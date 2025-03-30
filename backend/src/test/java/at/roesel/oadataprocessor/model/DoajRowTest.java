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

import at.roesel.oadataprocessor.model.doaj.DoajFields;
import at.roesel.oadataprocessor.model.doaj.DoajRow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoajRowTest {

    @Test
    void getApcAmount() {
        DoajRow row = new DoajRow();

        row.setField(DoajFields.apcAmount, null);
        Amount result = row.getApcAmount();
        assertEquals(0, result.value);
        assertEquals("", result.currency);

        row.setField(DoajFields.apcAmount, "100");
        result = row.getApcAmount();
        assertEquals(10000, result.value);
        assertEquals("", result.currency);

        row.setField(DoajFields.apcAmount, "100 USD");
        result = row.getApcAmount();
        assertEquals(10000, result.value);
        assertEquals("USD", result.currency);
    }

    @Test
    void getMultipleApcAmount() {
        DoajRow row = new DoajRow();

        row.setField(DoajFields.apcAmount, "2000 EUR; 1700 GBP; 2200 USD");
        Amount result = row.getApcAmount();
        assertEquals(200000, result.value);
        assertEquals("EUR", result.currency);
    }
}