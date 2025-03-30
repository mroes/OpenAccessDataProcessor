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

import at.roesel.common.CharacterSupport;
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Converter
public class CrossrefWorkConverter implements AttributeConverter<CrossrefWork, String> {

    private final static String BASE64_PREFIX = "base64:";
    private final boolean encodeAbstract = false;

    private final Logger logger = LoggerFactory.getLogger(CrossrefWorkConverter.class);

    protected ObjectMapper objectMapper;

    public CrossrefWorkConverter() {
        this(null);
    }

    public CrossrefWorkConverter(ObjectMapper objectMapper) {
        if (objectMapper == null) {
            objectMapper = ObjectMapperFactory.create();
        }
        this.objectMapper = objectMapper;
    }

    public String convertToDatabaseColumn(CrossrefWork data) {

        if (data == null) {
            return "";
        } else {
            String jsonData = null;
            try {
                String workAbstract = data.workAbstract;
                // If the abstract contains Unicode characters with 4 bytes, we encode it
                if (encodeAbstract && workAbstract != null && CharacterSupport.has4ByteCharacters(workAbstract)) {
                    data.workAbstract = BASE64_PREFIX + Base64.getEncoder().encodeToString(workAbstract.getBytes(StandardCharsets.UTF_8));
                }

                jsonData = objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
            return jsonData;
        }

    }

    public CrossrefWork convertToEntityAttribute(String jsonData) {

        if (jsonData == null || jsonData.isEmpty()) {
            return null;
        } else {
            CrossrefWork work = new CrossrefWork();
            try {
                work = objectMapper.readValue(jsonData, CrossrefWork.class);
                String workAbstract = work.workAbstract;
                if (workAbstract != null && workAbstract.startsWith(BASE64_PREFIX)) {
                    byte[] decodedBytes = Base64.getDecoder().decode(workAbstract.substring(BASE64_PREFIX.length()));
                    String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
                    work.workAbstract = decodedString;
                }
            } catch (final IOException e) {
                logger.error(e.getMessage(), e);
            }
            return work;
        }
    }

}
