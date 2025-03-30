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

import at.roesel.oadataprocessor.model.oaipmh.OaiPmhField;
import at.roesel.oadataprocessor.model.oaipmh.jabx.dc.ElementType;
import at.roesel.oadataprocessor.model.oaipmh.jabx.dc.OaiDcType;
import at.roesel.oadataprocessor.services.oaipmh.OaipmhRecord;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.*;

public class OaiDcWrapper implements MetaDataWrapper {

    private final OaiDcType type;
    private final Map<String, OaiPmhField> fields = new HashMap<>();

    public OaiDcWrapper(OaiDcType type) {
        this.type = type;
        if (type != null) {
            buildFieldMap(type.getTitleOrCreatorOrSubject());
        }
    }

    public OaiDcWrapper(OaipmhRecord record) {
        this.type = null;
        Object data = record.getData();
        if (data instanceof Map) {
            for (Object value : ((Map<String, Object>)data).values()) {
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>)value;
                    OaiPmhField oaiPmhField = new OaiPmhField((String)map.get("name"));
                    oaiPmhField.setValues((List<String>)map.get("values"));
                    fields.put(oaiPmhField.getName(), oaiPmhField);
                }
            }
        }
    }

    public void buildFieldMap(List<JAXBElement<ElementType>> elements) {
        for (JAXBElement<ElementType> jaxbElement : elements) {
            QName qName = jaxbElement.getName();
            String name = qName.getLocalPart();
            OaiPmhField field = getOrAddField(name);
            field.setJaxbElement(jaxbElement);
            field.addValue(jaxbElement.getValue().getValue());
        }
    }

    protected OaiPmhField getOrAddField(String name) {
        OaiPmhField field = fields.get(name);
        if (field == null) {
            field = new OaiPmhField(name);
            fields.put(name, field);
        }
        return field;
    }

    public OaiPmhField getField(String name) {
        return fields.get(name);
    }

    @Override
    public String getFieldValue(String name) {
        OaiPmhField field = getField(name);
        if (field != null) {
            return field.getValue();
        }
        return null;
    }

    @Override
    public List<String> getFieldValues(String name) {
        OaiPmhField field = getField(name);
        if (field != null) {
            return field.getValues();
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getFieldNames() {
        return new ArrayList<>(fields.keySet());
    }

    @Override
    public OaipmhRecord getData() {
        OaipmhRecord data = new OaipmhRecord();
        data.setData(fields);
        return data;
    }
}
