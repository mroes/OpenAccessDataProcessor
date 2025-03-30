
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

package at.roesel.oadataprocessor.model.sherpa;

import com.fasterxml.jackson.annotation.*;
import jakarta.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uri",
    "name",
    "url",
    "country",
    "publication_count",
    "country_phrases",
    "identifiers",
    "imprints_id",
    "id"
})
@Generated("jsonschema2pojo")
public class RomeoPublisher {

    private final Logger logger = LoggerFactory.getLogger(RomeoPublisher.class);

    @JsonProperty("uri")
    public String uri;
    @JsonProperty("name")
    public List<Name> name = null;
    @JsonProperty("url")
    public String url;
    @JsonProperty("country")
    public String country;
    @JsonProperty("publication_count")
    public int publicationCount;
    @JsonProperty("country_phrases")
    public List<Phrase> countryPhrases = null;
    @JsonProperty("identifiers")
    public List<Identifier> identifiers = null;
    @JsonProperty("imprints_id")
    public List<Integer> imprintsId = null;
    @JsonProperty("id")
    public int id;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public List<String> names() {
        List<String> names = name.stream().map(name -> name.name).collect(Collectors.toList());
        return names;
    }

    public List<String> identifiers() {
        if (identifiers == null) {
            return Collections.emptyList();
        }
        List<String> result = this.identifiers.stream().map(identifier -> identifier.identifier + "|" + identifier.type)
                .collect(Collectors.toList());
        return result;
    }

    public List<String> names(String language) {
        List<String> names = name.stream().filter(name -> language.equals(name.language))
            .map(name -> name.name).collect(Collectors.toList());
        return names;
    }

    public String identifier(String idType) {
        if (identifiers != null) {
            List<String> ids = identifiers.stream().filter(id -> idType.equals(id.type))
                    .map(id -> id.identifier).collect(Collectors.toList());
            if (ids.size() == 1) {
                return ids.get(0);
            } else if (ids.size() > 1) {
                logger.warn("Romeopublisher {} has multiple ids of type {}", names(), idType);
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return names().toString();
    }
}
