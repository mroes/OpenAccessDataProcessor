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

import at.roesel.oadataprocessor.model.SourceStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class OpenAlexPublisher {
    @JsonProperty("id")
    private String id;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("alternate_titles")
    private List<Object> alternateTitles;
    @JsonProperty("hierarchy_level")
    private int hierarchyLevel;
    @JsonProperty("parent_publisher")
    private Object parentPublisher;
    @JsonProperty("country_codes")
    private List<String> countryCodes;
    @JsonProperty("works_count")
    private int worksCount;
    @JsonProperty("cited_by_count")
    private int citedByCount;
    @JsonProperty("ids")
    private OpenAlexIds ids;
    @JsonProperty("counts_by_year")
    private List<Object> countsByYear;
    @JsonProperty("sources_api_url")
    private String sourcesApiUrl;
    @JsonProperty("updated_date")
    private String updatedDate;
    @JsonProperty("created_date")
    private String createdDate;

    public String error;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("display_name")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("display_name")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("alternate_titles")
    public List<Object> getAlternateTitles() {
        return alternateTitles;
    }

    @JsonProperty("alternate_titles")
    public void setAlternateTitles(List<Object> alternateTitles) {
        this.alternateTitles = alternateTitles;
    }

    @JsonProperty("hierarchy_level")
    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    @JsonProperty("hierarchy_level")
    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    @JsonProperty("parent_publisher")
    public Object getParentPublisher() {
        return parentPublisher;
    }

    @JsonProperty("parent_publisher")
    public void setParentPublisher(Object parentPublisher) {
        this.parentPublisher = parentPublisher;
    }

    @JsonProperty("country_codes")
    public List<String> getCountryCodes() {
        return countryCodes;
    }

    @JsonProperty("country_codes")
    public void setCountryCodes(List<String> countryCodes) {
        this.countryCodes = countryCodes;
    }

    @JsonProperty("works_count")
    public int getWorksCount() {
        return worksCount;
    }

    @JsonProperty("works_count")
    public void setWorksCount(int worksCount) {
        this.worksCount = worksCount;
    }

    @JsonProperty("cited_by_count")
    public int getCitedByCount() {
        return citedByCount;
    }

    @JsonProperty("cited_by_count")
    public void setCitedByCount(int citedByCount) {
        this.citedByCount = citedByCount;
    }

    @JsonProperty("ids")
    public OpenAlexIds getIds() {
        return ids;
    }

    @JsonProperty("ids")
    public void setIds(OpenAlexIds ids) {
        this.ids = ids;
    }

    @JsonProperty("counts_by_year")
    public List<Object> getCountsByYear() {
        return countsByYear;
    }

    @JsonProperty("counts_by_year")
    public void setCountsByYear(List<Object> countsByYear) {
        this.countsByYear = countsByYear;
    }

    @JsonProperty("sources_api_url")
    public String getSourcesApiUrl() {
        return sourcesApiUrl;
    }

    @JsonProperty("sources_api_url")
    public void setSourcesApiUrl(String sourcesApiUrl) {
        this.sourcesApiUrl = sourcesApiUrl;
    }

    @JsonProperty("updated_date")
    public String getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty("updated_date")
    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    @JsonProperty("created_date")
    public String getCreatedDate() {
        return createdDate;
    }

    @JsonProperty("created_date")
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getError() {
        return error;
    }

    @JsonIgnore
    public boolean isValid() {
        return error == null;
    }

    @Override
    public String toString() {
        return "OpenAlexPublisher{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    @JsonIgnore
    public SourceStatus status() {
        SourceStatus status;
        String error = getError();
        if (error == null) {
            status = SourceStatus.OK;
        } else if (error.contains("404")) {
            status = SourceStatus.NOT_FOUND;
        } else {
            status = SourceStatus.OTHER_ERROR;
        }
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenAlexPublisher that = (OpenAlexPublisher) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
