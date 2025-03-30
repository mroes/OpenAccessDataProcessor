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

import at.roesel.oadataprocessor.services.pure.model.PureResearchOutput;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import java.io.IOException;

public class PureResearchOutputConverter implements AttributeConverter<PureResearchOutput, String> {
    protected ObjectMapper jsonMapper;

    public PureResearchOutputConverter() {
        jsonMapper = new ObjectMapper();
//            jsonMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
//        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        /*
        JavaTimeModule module = new JavaTimeModule();
        LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDateSerializer localDateSerializer = new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE);
        module.addDeserializer(LocalDate.class, localDateDeserializer);
        module.addSerializer(LocalDate.class, localDateSerializer);
        jsonMapper.registerModule(module);
        */
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String convertToDatabaseColumn(PureResearchOutput data) {

        if (data == null) {
            return "";
        } else {
            String jsonData = null;
            try {
                jsonData = jsonMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
//            logger.error("JSON writing error", e);
            }
            return jsonData;

        }
    }

    @Override
    public PureResearchOutput convertToEntityAttribute(String jsonData) {

        if (jsonData == null || "".equals(jsonData)) {
            return new PureResearchOutput();
        } else {

            PureResearchOutput researchOutput;
            try {
                researchOutput = jsonMapper.readValue(jsonData, PureResearchOutput.class);

            } catch (final IOException e) {
                //            logger.error("JSON reading error", e);
                researchOutput = new PureResearchOutput();
            }
            return researchOutput;
        }
    }

}
