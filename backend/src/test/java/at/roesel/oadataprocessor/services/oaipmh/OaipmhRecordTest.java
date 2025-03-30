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

package at.roesel.oadataprocessor.services.oaipmh;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OaipmhRecordTest {

    @Test
    void issnInRelation() {
        OaipmhRecord record = new OaipmhRecord();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> relationMap = new HashMap<>();
        relationMap.put("values", Collections.singletonList("info:eu-repo/semantics/altIdentifier/issn/1664-462X"));
        data.put("relation", relationMap);
        record.setData(data);

        assertEquals("1664-462X", record.issn().get(0));
    }

    @Test
    void issnInIdentifier() {
        OaipmhRecord record = new OaipmhRecord();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> relationMap = new HashMap<>();
        relationMap.put("values", Collections.singletonList("1664-462X"));
        data.put("identifier", relationMap);
        record.setData(data);

        assertEquals("1664-462X", record.issn().get(0));
    }


}
