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

class DataExtractorTest {

    @Test
    void doiWuJournals() {
        OaiWrapperStub wrapper = new OaiWrapperStub(null);
        wrapper.addValue("identifier", "https://openjournals.wu.ac.at/ojs/index.php/region/article/view/9");
        wrapper.addValue("identifier", "10.18335/region.v1i1.9");
        assertEquals("10.18335/region.v1i1.9", new DataExtractorDefault().doi(wrapper));
    }

    @Test
    void doiIst() {
        OaiWrapperStub wrapper = new OaiWrapperStub(null);
        wrapper.addValue("source", "Vol 12079. Springer Nature; 2020:79-97. doi:<a href=\"https://doi.org/10.1007/978-3-030-45237-7_5\">10.1007/978-3-030-45237-7_5</a>");
        assertEquals("10.1007/978-3-030-45237-7_5", new DataExtractorDefault().doi(wrapper));
    }

    @Test
    void identifierOeaw() {
        OaiWrapperStub wrapper = new OaiWrapperStub(null);
        wrapper.addValue("identifier", "http://hw.oeaw.ac.at/ml/musik_A/Anzoletti_Familie.xml");
        wrapper.addValue("identifier", "GOid: 0xc1aa5576_0x0001f710");
        assertEquals("0xc1aa5576_0x0001f710", new DataExtractorOeaw().identifier(wrapper));
    }

}