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

package at.roesel.oadataprocessor.persistance.conversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/*
 Conversion for object <-> json string
 */
public class ObjectConverter<T> implements AttributeConverter<T, String> {

    private final Logger logger = LoggerFactory.getLogger(ObjectConverter.class);

    private final Class<T> objectClass;
    protected ObjectMapper objectMapper;

    public ObjectConverter(Class<T> objectClass) {
        this(objectClass, null);
    }

    public ObjectConverter(Class<T> objectClass, ObjectMapper objectMapper) {
        this.objectClass = objectClass;
        if (objectMapper == null) {
            this.objectMapper = ObjectMapperFactory.create();
        } else {
            this.objectMapper = objectMapper;
        }
    }

    public String convertToDatabaseColumn(T data) {

        if (data == null) {
            return "";
        } else {
            String jsonData = null;
            try {
                convertDataForStoring(data);
                jsonData = objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
            return jsonData;
        }
    }

    protected void convertDataForStoring(T data) {
        // do nothing
    }

    public T convertToEntityAttribute(String jsonData) {

        if (jsonData == null || jsonData.isEmpty()) {
            return null;
        } else {
            T object = null;
            try {
                object = objectMapper.readValue(jsonData, objectClass);
                convertDataAfterLoading(object);
            } catch (final IOException e) {
                logger.error(e.getMessage(), e);
            }
            return object;
        }
    }

    protected void convertDataAfterLoading(T data) {
        // do nothing
    }

}
