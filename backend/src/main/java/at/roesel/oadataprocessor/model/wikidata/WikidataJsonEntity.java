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

package at.roesel.oadataprocessor.model.wikidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static at.roesel.common.StringSupport.hasValue;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WikidataJsonEntity {


    public final static String instanceOfId = "P31";
    public final static String titleId = "P1476";
    public final static String publisherId = "P123";
    public final static String issnId = "P236";
    public final static String issnLId = "P7363";
    public final static String crJournalId = "P8375";
    public final static String parentOrganizationPropId = "P749";

    public final static String isniId = "P213";
    public final static String ringgoldId = "P3500";
    public final static String rorId = "P6782";
    public final static String romeoiId = "P6617";

    private final static String startTimePropId = "P580";
    private final static String endTimePropId = "P582";

    private final static String wikibaseItem = "wikibase-item";
    private final static String wikiExternalId = "external-id";
    private final static String wikiMonolingualtext = "monolingualtext";

    private final static String idAttr = "id";
    private final static String valueAttr = "value";
    private final static String timeAttr = "time";

    public long pageid;
    public String id;
    public String title;
    public String modified;
    public Map<String, Label> labels;
    public Map<String, Label> descriptions;
    public Map<String, List<Label>> aliases;
    public Map<String, List<Claim>> claims;

    private final String[] languages = {"de", "en", "fr"};

    public String label(String language) {
        if (labels != null) {
            Label label = labels.get(language);
            if (label != null) {
                return label.value;
            };
        }
        return "";
    }

    public String preferredLabel() {
        for (String lang : languages) {
            String label = label(lang);
            if (label != null && !label.isEmpty()) {
                return label;
            }
        }
        return "";
    }

    public List<String> aliases(String language) {
        List<String> result = new ArrayList<>();
        if (labels != null) {
            List<Label> aliasesLang = this.aliases.get(language);
            if (aliasesLang != null) {
                aliasesLang.forEach(alias -> {
                    result.add(alias.value);
                });
            }
        }
        return result;
    }

    public List<WikidataValue> valuesForPropertyId(String propertyId) {
        List<WikidataValue> result = new ArrayList<>();
        List<Claim> claims = claimsForPropertyId(propertyId);
        for (Claim claim : claims) {
            WikidataValue value = claim.value();
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    public WikidataMonolingualtext monolingualtext(String propertyId) {
        List<Claim> claims = claimsForPropertyId(propertyId);
        for (Claim claim : claims) {
            return claim.textValue();
        }
        return WikidataMonolingualtext.empty;
    }


    private List<Claim> claimsForPropertyId(String propertyId) {
        List<Claim> result = null;
        if (claims != null) {
            result = claims.get(propertyId);
        }
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    public static class Label {
        public String language;
        public String value;

        @Override
        public String toString() {
            return "Label{" +
                    "language='" + language + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class Claim {
        public Snak mainsnak;
        public String id;
        public String rank;
        public String type;
        public Map<String, List<Snak>> qualifiers;
        @JsonProperty("qualifiers-order")
        public List<String> qualifiersOrders;

        public WikidataValue value() {
            if (mainsnak != null) {
                WikidataValue value = mainsnak.value();
                if (value != null) {
                    value.startTime = startTime();
                    value.endTime = endTime();
                }
                return value;
            }
            return null;
        }

        public WikidataMonolingualtext textValue() {
            if (mainsnak != null) {
                return mainsnak.textValue();
            }
            return null;
        }

        public List<Snak> qualifiersForPropertyId(String propertyId) {
            List<Snak> result = null;
            if (qualifiers != null) {
                result = qualifiers.get(propertyId);
            }
            if (result == null) {
                result = Collections.emptyList();
            }
            return result;
        }

        public String startTime() {
            return time(startTimePropId);
        }

        public String endTime() {
            return time(endTimePropId);
        }

        private String time(String propertyId) {
            List<Snak> snaks = qualifiersForPropertyId(propertyId);
            for (Snak snak : snaks) {
                if (snak.property.equals(propertyId)) {
                    Object datavalue = snak.datavalue;
                    if (datavalue != null) {
                        Map<String, String> values = snak.datavalue.valueMap();
                        if (values != null) {
                            String time = values.get(timeAttr);
                            return time;
                        }
                    }
                }
            }
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class Snak {
        public String snaktype;
        public String property;
        public DataValue datavalue;
        public String datatype;

        public WikidataValue value() {
            WikidataValue value = null;
            if (datavalue != null) {
                if (datatype.equals(wikibaseItem)) {
                    Map<String, String> data = datavalue.valueMap();
                    if (data != null) {
                        value = new WikidataValue(data.get(idAttr));
                    }
                } else if (datatype.equals(wikiExternalId)) {
                    value = new WikidataValue(datavalue.valueString());
                }
            }
            return value;
        }

        public WikidataMonolingualtext textValue() {
            if (datatype.equals(wikiMonolingualtext)) {
                if (datavalue != null) {
                    Map<String, String> data = datavalue.valueMap();
                    if (data != null) {
                        WikidataMonolingualtext text = new WikidataMonolingualtext(data.get("text"), data.get("language"));
                        return text;
                    }
                }
            }
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class DataValue {
        public Object value;
        public String type;

        public String value() {
            if (value instanceof String) {
                return (String)value;
            }
            return null;
        }

        public Map<String, String> valueMap() {
            if (value instanceof Map) {
                return (Map<String,String>)value;
            }
            return null;
        }

        public String valueString() {
            if (value instanceof String) {
                return (String)value;
            }
            return null;
        }
    }

}

