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

package at.roesel.oadataprocessor.model.oaipmh;

import at.roesel.oadataprocessor.model.oaipmh.jabx.dc.ElementType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class OaiPmhField {

    @JsonIgnore
    private JAXBElement<ElementType> jaxbElement;
    private String name;
    private List<String> values = new ArrayList<>();

    public OaiPmhField(String name) {
        this.name = name;
    }

    @JsonIgnore
    public JAXBElement<ElementType> getJaxbElement() {
        return jaxbElement;
    }

    public void setJaxbElement(JAXBElement<ElementType> jaxbElement) {
        this.jaxbElement = jaxbElement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean multipleValues() {
        return (values.size() > 0);
    }

    public void addValue(String value) {
        values.add(value);
    }

    @JsonIgnore
    public String getValue() {
        if (!values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    public String getValue(int index) {
        if (index < values.size()) {
            return values.get(index);
        }
        return null;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
