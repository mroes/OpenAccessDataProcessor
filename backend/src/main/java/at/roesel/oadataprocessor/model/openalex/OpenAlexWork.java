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
public class OpenAlexWork {

    private final static String firstAuthor = "first";

    public String id;
    public String display_name;
    public String title;
    @JsonProperty("publication_date")
    public String publicationDate;
    public List<OpenAlexAuthorship> authorships;
    // 30.5.2024 deprecated, replaced with locations
//    @JsonProperty("host_venue")
//    public OpenAlexHostVenue hostVenue;
//    @JsonProperty("alternate_host_venues")
//    public List<OpenAlexHostVenue> alternateHostVenues;
    @JsonProperty("primary_location")
    public OpenAlexLocation primaryLocation;
    @JsonProperty("best_oa_location")
    public OpenAlexLocation bestOaLocation;
    @JsonProperty("locations")
    public List<OpenAlexLocation> locations;

//    public List<OpenAlexConcept> concepts;    // not used
    @JsonProperty("open_access")
    public OpenAlexOpenAccess openAccess;
    public Map<String, String> ids;
    @JsonProperty("publication_year")
    public int publicationYear;
//    public String cited_by_api_url;   // not used
    public String doi;
    public String type;
    @JsonProperty("type_crossref")
    public String typeCrossref;
    public String updated_date;
    public String created_date;
    /* not used
    @JsonProperty("corresponding_author_ids")
    public List<String> correspondingAuthorIds;
    @JsonProperty("corresponding_institution_ids")
    public List<String> correspondingInstitutionIds;
     */

    public String error;

    @Override
    @JsonIgnore
    public String toString() {
        return "OpenAlexWork{" +
                "id='" + id + '\'' +
                ", display_name='" + display_name + '\'' +
                ", doi='" + doi + '\'' +
                '}';
    }

    public String getError() {
        return error;
    }

    @JsonIgnore
    public boolean isValid() {
        return error == null;
    }

    @JsonIgnore
    public Set<OpenAlexInstitution> affiliations () {
        Set<OpenAlexInstitution> affiliations = new HashSet<>();
        for (OpenAlexAuthorship authorship : authorships) {
            affiliations.addAll(authorship.institutions);
        }
        return affiliations;
    }

    @JsonIgnore
    public OpenAlexAuthorship firstAuthorship () {
        for (OpenAlexAuthorship authorship : authorships) {
            if (authorship.author_position.equals(firstAuthor)) {
                return authorship;
            }
        }
        return null;
    }

    @JsonIgnore
    public List<OpenAlexAuthorship> correspondingAuthorships () {
        List<OpenAlexAuthorship> result = new ArrayList<>();
        for (OpenAlexAuthorship authorship : authorships) {
            if (authorship.isCorresponding) {
                result.add(authorship);
            }
        }
        return result;
    }

    @JsonIgnore
    public String licence () {
        String licence = null;
        if (bestOaLocation != null) {
            licence = licenceFromLocation(bestOaLocation);
        }
        if (licence == null) {
            if (primaryLocation != null) {
                licence = licenceFromLocation(primaryLocation);
            }
        }
        if (licence == null) {
            if (locations != null && !locations.isEmpty()) {
                for (OpenAlexLocation location : locations) {
                    licence = licenceFromLocation(location);
                    if (licence != null) {
                        break;
                    }
                }
            }
        }

        return licence;
    }

    private String licenceFromLocation(OpenAlexLocation location) {
        if (location.getLicense() != null && !location.getLicense().isEmpty()) {
            return location.getLicense();
        }
        return null;
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
        OpenAlexWork that = (OpenAlexWork) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
