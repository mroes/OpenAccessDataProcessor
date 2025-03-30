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

package at.roesel.oadataprocessor.model.crossref;

import at.roesel.oadataprocessor.model.Author;
import at.roesel.oadataprocessor.model.SourceStatus;
import at.roesel.oadataprocessor.support.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CrossrefWork {
    public List<CrossrefInstitution> institution;
    @JsonProperty("publisher-location")
    public String publisherLocation;
    public String publisher;
    public String issue;
    public List<CrossrefLicence> license;
    // public List<HashMap<String, Object>> funder;
    @JsonProperty("published-print")
    public CrossrefPrint publishedPrint;
    @JsonProperty("abstract")
    public String workAbstract;
    @JsonProperty("DOI")
    public String doi;
    public String type;
    public String source;
    public List<String> title;
    public List<CrossrefAuthor> author;
    @JsonProperty("published-online")
    // public CrossrefOnline publishedOnline;
    public CrossrefPrint publishedOnline;
    //    public HashMap<String, Object> reference;
    public String language;
    @JsonProperty("ISBN")
    public List<String> isbn;
    @JsonProperty("URL")
    public String url;
    // https://de.wikipedia.org/wiki/Internationale_Standardnummer_f%C3%BCr_fortlaufende_Sammelwerke#Erweiterungen_(eISSN,_SICI)
    // Internationale Standardnummer für fortlaufende Sammelwerke (engl. International Standard Serial Number, ISSN)
    // ist eine Nummer, die Zeitschriften und Schriftenreihen eindeutig identifiziert
    @JsonProperty("ISSN")
    public List<String> issn;
    public List<String> subject;
    public CrossrefPrint published;

//    public long myCreated;
//    public long myUpdated;
    public String error;

    public CrossrefLicence firstLicence() {
        if (license != null && license.size() > 0) {
            return license.get(0);
        }

        return null;
    }

    @JsonIgnore
    public boolean isValid() {
        return doi != null;
    }

    @JsonIgnore
    public static boolean isValid(CrossrefWork crossrefWork) {
        return crossrefWork != null && crossrefWork.isValid();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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

    @JsonIgnore
    public List<String> getIssns() {
        if (issn != null) {
            return issn;
        }

        return Collections.emptyList();
    }

    @JsonIgnore
    public String firstTitle() {
        if (title == null || title.size() == 0) {
            return "";
        }

        return title.get(0);
    }

    @JsonIgnore
    public List<Author> resolvedAuthors() {
        List<Author> authors = null;
        if (author != null && !author.isEmpty()) {
            authors = new ArrayList<>();
            for (CrossrefAuthor crAuthor : author) {
                Author author = new Author();
                author.lastName = crAuthor.family;
                author.firstName = crAuthor.given;
                author.orcid = crAuthor.orcid;
                author.role = crAuthor.sequence;
                authors.add(author);
            }
        }
        return authors;
    }

    @JsonIgnore
    public int publishedYear() {
        int year = 0;
        /* published nimmt anscheinend publishedOnline, wenn es exisitiert, wir brauchen aber das Print-Datum
        if (published != null) {
            year = published.year();
        } else
        */
        if (publishedPrint != null) {
            year = publishedPrint.year();
        } else if (publishedOnline != null) {
            year = publishedOnline.year();
        }
        return year;
    }

    @JsonIgnore
    public int publishedDate() {
        String date = null;
        /*
        if (published != null) {
            date = published.date();
        } else
         */
        if (publishedPrint != null) {
            date = publishedPrint.date();
        } else if (publishedOnline != null) {
            date = publishedOnline.date();
        }
        if (date != null) {
            try {
                if (date.length() == 7) {
                    date += "-01";   // fiktiver Tag
                }
                else if (date.length() == 4) {
                    date += "-07-01";   // fiktiver Tag
                }
                if (date.length() == 10) {
                    int result = DateUtil.localDateToInt(DateUtil.localDateFromIso8601(date));
                    return result;
                }
            } catch (Exception e) {
                // ignore und berechne ein fiktives Datum
            }
            int year = publishedYear();
            if (year > 0) {
                // wenn es nur das Jahr gibt, dann nehmen wir den 1.7. des Jahres als Datum
                year = year * 10000 + 7 * 100 + 1;
            }
            return year;
        }
        return 0;
    }

    @Override
    @JsonIgnore
    public String toString() {
        return "CrossrefWork{" +
                firstTitle() +
                ", " +
                firstAuthors() +
                "}";
    }

    @JsonIgnore
    private List<CrossrefAuthor> firstAuthors() {
        if (author.size() < 5) {
            return author;
        }

        List<CrossrefAuthor> result = author.stream().limit(5).collect(Collectors.toList());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrossrefWork that = (CrossrefWork) o;
        return Objects.equals(doi, that.doi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }
}
