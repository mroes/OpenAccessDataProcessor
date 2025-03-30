
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "listed_in_doaj_phrases",
    "type",
    "system_metadata",
    "listed_in_doaj",
    "title",
    "id",
    "publisher_policy",
    "issns",
    "publishers",
    "url",
    "type_phrases"
})
@Generated("jsonschema2pojo")
public class Romeo {

    @JsonProperty("listed_in_doaj_phrases")
    public List<Phrase> listedInDoajPhrases = null;
    @JsonProperty("type")
    public String type;
    @JsonProperty("system_metadata")
    public SystemMetadata systemMetadata;
    @JsonProperty("listed_in_doaj")
    public String listedInDoaj;
    @JsonProperty("title")
    public List<Title> title = null;
    @JsonProperty("id")
    public int id;
    @JsonProperty("publisher_policy")
    public List<PublisherPolicy> publisherPolicy = null;
    @JsonProperty("issns")
    public List<Issn> issns = null;
    @JsonProperty("publishers")
    public List<PublisherRelation> publishers = null;
    @JsonProperty("url")
    public String url;
    @JsonProperty("type_phrases")
    public List<Phrase> typePhrases = null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Romeo romeo = (Romeo) o;
        return Objects.equals(issns, romeo.issns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issns);
    }
}
