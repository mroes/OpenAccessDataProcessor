
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
    "article_version_phrases",
    "additional_oa_fee_phrases",
    "article_version",
    "conditions",
    "additional_oa_fee",
    "location",
    "embargo",
    "publisher_deposit",
    "copyright_owner_phrases",
    "copyright_owner",
    "license",
    "prerequisites"
})
@Generated("jsonschema2pojo")
public class PermittedOa {

    @JsonProperty("article_version_phrases")
    public List<Phrase> articleVersionPhrases = null;
    @JsonProperty("additional_oa_fee_phrases")
    public List<Phrase> additionalOaFeePhrases = null;
    @JsonProperty("article_version")
    public List<String> articleVersion = null;
    @JsonProperty("conditions")
    public List<String> conditions = null;
    @JsonProperty("additional_oa_fee")
    public String additionalOaFee;
    @JsonProperty("location")
    public Location location;
    @JsonProperty("embargo")
    public Embargo embargo;
    @JsonProperty("publisher_deposit")
    public List<PublisherDeposit> publisherDeposit = null;
    @JsonProperty("copyright_owner_phrases")
    public List<Phrase> copyrightOwnerPhrases = null;
    @JsonProperty("copyright_owner")
    public String copyrightOwner;
    @JsonProperty("license")
    public List<License> license = null;
    @JsonProperty("prerequisites")
    public Prerequisites prerequisites;

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

    @JsonIgnore
    public boolean hasAdditionalOaFee() {
        if (additionalOaFee == null) {
            return false;
        }

        return additionalOaFee.equals("yes");
    }

    @JsonIgnore
    public boolean hasPrerequisites() {
        return prerequisites != null &&
                prerequisites.prerequisiteFunders != null && !prerequisites.prerequisiteFunders.isEmpty();
    }

    public boolean hasArticleVersion(String version) {
      if (articleVersion != null && !articleVersion.isEmpty()) {
          return articleVersion.contains(version);
      }

      return false;
    };
}
