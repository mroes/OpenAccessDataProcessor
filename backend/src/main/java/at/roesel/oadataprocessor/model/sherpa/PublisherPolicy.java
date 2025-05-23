
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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "permitted_oa",
    "urls",
    "uri",
    "open_access_prohibited_phrases",
    "open_access_prohibited",
    "publication_count",
    "internal_moniker",
    "id"
})
@Generated("jsonschema2pojo")
public class PublisherPolicy {

    @JsonProperty("permitted_oa")
    public List<PermittedOa> permittedOa = null;
    @JsonProperty("urls")
    public List<Url> urls = null;
    @JsonProperty("uri")
    public String uri;
    @JsonProperty("open_access_prohibited_phrases")
    public List<Phrase> openAccessProhibitedPhrases = null;
    @JsonProperty("open_access_prohibited")
    public String openAccessProhibited;
    @JsonProperty("publication_count")
    public int publicationCount;
    @JsonProperty("internal_moniker")
    public String internalMoniker;
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

}
