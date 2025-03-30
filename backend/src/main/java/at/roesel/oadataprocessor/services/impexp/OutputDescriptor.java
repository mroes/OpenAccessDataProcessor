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

package at.roesel.oadataprocessor.services.impexp;

import org.apache.commons.beanutils.NestedNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OutputDescriptor<T> {
    private final List<String> headers;
    private List<Field> fields;

    private final Logger logger = LoggerFactory.getLogger(OutputDescriptor.class);

    public OutputDescriptor(Class<T> clazz, List<String> headers) {
        this.headers = headers;
        fields = buildFields(clazz);
    }

    private List<Field> buildFields(Class<T> clazz) {
        List<Field> fields = new ArrayList<>();
        for (String fieldName : headers) {
            try {
                Field field = clazz.getField(mapField(fieldName));
                fields.add(field);
            } catch (NoSuchFieldException e) {
                logger.error(String.format("No such field %s in %s", fieldName, clazz.getName()), e);
            }
        }
        return fields;
    }

    protected String mapField(String fieldName) {
        return fieldName;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<String> cells(T object) {
        List<String> rows = new ArrayList<>();
        for (Field field : fields) {
            String value;
            try {
                Object fieldValue = field.get(object);
                if (fieldValue != null) {
                    value = fieldValue.toString();
                } else {
                    value = "";
                }
            } catch (NestedNullException e) {
                value = "";
            } catch (Exception e) {
                value = "!error! " + e.getMessage();
            }
            rows.add(value);
        }
        return rows;
    }

}
