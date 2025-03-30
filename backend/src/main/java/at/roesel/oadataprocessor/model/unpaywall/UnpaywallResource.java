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

package at.roesel.oadataprocessor.model.unpaywall;

import at.roesel.oadataprocessor.model.Author;
import at.roesel.oadataprocessor.model.SourceStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UnpaywallResource {
    public String doi;
    public String doi_url;
    public String title;
    public String published_date;
    public String journal_name;
    public String journal_issns;
    public String journal_issn_l;
    public boolean journal_is_oa;
    public boolean journal_is_in_doaj;
    public String publisher;
    public boolean is_oa;
    public String oa_status;
    public UpwOaLocation best_oa_location;
    public List<UpwOaLocation> oa_locations;
    @JsonProperty("z_authors")
    public List<UpwAuthor> zAuthors;

//    public long myCreated;
//    public long myUpdated;
    public String error;

    @JsonIgnore
    public boolean isValid() {
        return doi != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @JsonIgnore
    public static boolean isValid(UnpaywallResource unpaywallResource) {
        return unpaywallResource != null && unpaywallResource.isValid();
    }

    @JsonIgnore
    public List<Author> resolvedAuthors() {
        List<Author> authors = null;
        if (zAuthors != null && !zAuthors.isEmpty()) {
            authors = new ArrayList<>();
            for (UpwAuthor upwAuthor : zAuthors) {
                Author author = new Author();
                author.lastName = upwAuthor.family;
                author.firstName = upwAuthor.given;
                author.orcid = upwAuthor.orcid;
                author.role = upwAuthor.sequence;
                authors.add(author);
            }
        }
        return authors;
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
        UnpaywallResource that = (UnpaywallResource) o;
        return Objects.equals(doi, that.doi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }

}
