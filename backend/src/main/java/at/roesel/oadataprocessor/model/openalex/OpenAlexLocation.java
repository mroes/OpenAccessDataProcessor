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

package at.roesel.oadataprocessor.model.openalex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("jsonschema2pojo")
public class OpenAlexLocation {
      @JsonProperty("is_oa")
        private Boolean isOa;
        @JsonProperty("landing_page_url")
        private String landingPageUrl;
        @JsonProperty("pdf_url")
        private String pdfUrl;
        @JsonProperty("source")
        private OpenAlexSource source;
        @JsonProperty("license")
        private String license;
        @JsonProperty("version")
        private String version;

        @JsonProperty("is_oa")
        public Boolean getIsOa() {
            return isOa;
        }

        @JsonProperty("is_oa")
        public void setIsOa(Boolean isOa) {
            this.isOa = isOa;
        }

        @JsonProperty("landing_page_url")
        public String getLandingPageUrl() {
            return landingPageUrl;
        }

        @JsonProperty("landing_page_url")
        public void setLandingPageUrl(String landingPageUrl) {
            this.landingPageUrl = landingPageUrl;
        }

        @JsonProperty("pdf_url")
        public String getPdfUrl() {
            return pdfUrl;
        }

        @JsonProperty("pdf_url")
        public void setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
        }

        @JsonProperty("source")
        public OpenAlexSource getSource() {
            return source;
        }

        @JsonProperty("source")
        public void setSource(OpenAlexSource source) {
            this.source = source;
        }

        @JsonProperty("license")
        public String getLicense() {
            return license;
        }

        @JsonProperty("license")
        public void setLicense(String license) {
            this.license = license;
        }

        @JsonProperty("version")
        public String getVersion() {
            return version;
        }

        @JsonProperty("version")
        public void setVersion(String version) {
            this.version = version;
        }

    }
