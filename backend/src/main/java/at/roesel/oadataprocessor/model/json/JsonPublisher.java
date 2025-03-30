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

package at.roesel.oadataprocessor.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class JsonPublisher {

    @JsonProperty("name")
    private String name;
    @JsonProperty("isni")
    private String isni;
    @JsonProperty("ror")
    private String ror;
    @JsonProperty("romeoid")
    private String romeoid;
    @JsonProperty("ringgold")
    private String ringgold;
    @JsonProperty("wikidata")
    private String wikidata;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("isni")
    public String getIsni() {
        return isni;
    }

    @JsonProperty("isni")
    public void setIsni(String isni) {
        this.isni = isni;
    }

    @JsonProperty("ror")
    public String getRor() {
        return ror;
    }

    @JsonProperty("ror")
    public void setRor(String ror) {
        this.ror = ror;
    }

    @JsonProperty("romeoid")
    public String getRomeoid() {
        return romeoid;
    }

    @JsonProperty("romeoid")
    public void setRomeoid(String romeoid) {
        this.romeoid = romeoid;
    }

    @JsonProperty("ringgold")
    public String getRinggold() {
        return ringgold;
    }

    @JsonProperty("ringgold")
    public void setRinggold(String ringgold) {
        this.ringgold = ringgold;
    }

    @JsonProperty("wikidata")
    public String getWikidata() {
        return wikidata;
    }

    @JsonProperty("wikidata")
    public void setWikidata(String wikidata) {
        this.wikidata = wikidata;
    }

}
