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

import at.roesel.oadataprocessor.model.Author;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static at.roesel.oadataprocessor.support.AuthorSupport.authorsFromStringList;

/**
 * Converts a list of authors into a JSON string for storage
 */
@Converter
public class AuthorConverter implements AttributeConverter<List<Author>, String> {

    private final Logger logger = LoggerFactory.getLogger(AuthorConverter.class);

    protected ObjectMapper objectMapper;

    public AuthorConverter() {
        this(null);
    }

    public AuthorConverter(ObjectMapper objectMapper) {
        if (objectMapper == null) {
            objectMapper = ObjectMapperFactory.create();
        }
        this.objectMapper = objectMapper;
    }

    public String convertToDatabaseColumn(List<Author> list) {

        if (list == null) {
            return "[]";    // The field is JSON. We need a valid JSON object.
        } else {
            String listJson = null;
            try {
                listJson = objectMapper.writeValueAsString(list);
            } catch (final JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
            return listJson;

        }
    }

    public List<Author> convertToEntityAttribute(String jsonData) {

        if (jsonData == null || jsonData.isEmpty()) {
            return emptyList();
        } else {

            List<Author> list = null;
            try {
                list = objectMapper.readValue(jsonData, authorReference());
            } catch (final IOException e) {
                try {
                    List<String> strAuthors = objectMapper.readValue(jsonData, stringReference());
                    list = authorsFromStringList(strAuthors);
                } catch (JsonProcessingException ex) {
                    logger.error(e.getMessage(), e);
                }
            }
            return list;
        }
    }


    protected List<Author> emptyList() {
        return Collections.emptyList();
    }

    protected TypeReference<ArrayList<Author>> authorReference() {
        return new TypeReference<ArrayList<Author>>() {
        };
    }

    protected TypeReference<ArrayList<String>> stringReference() {
        return new TypeReference<ArrayList<String>>() {
        };
    }
}
