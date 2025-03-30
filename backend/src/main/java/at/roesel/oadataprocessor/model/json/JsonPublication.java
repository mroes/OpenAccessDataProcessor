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

import at.roesel.oadataprocessor.model.PublicationSourceRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class JsonPublication implements PublicationSourceRecord {

    @JsonProperty("id")
    private String id;
    @JsonProperty("pubmedid")
    private long pubmedId;
    @JsonProperty("sciid")
    private String sciId;
    @JsonProperty("doi")
    private String doi;
    @JsonProperty("title")
    private String title;
    @JsonProperty("author")
    private List<JsonAuthor> authors;
    @JsonProperty("rawauthor")
    private String rawAuthors;

    @JsonProperty("corrauthaddr")
    private String corrAuthorAddress;

    @JsonProperty("year")
    private Integer year;
    @JsonProperty("pubdate")
    private String pubdate;
    @JsonProperty("type")
    private String type;
    @JsonProperty("publisher")
    private JsonPublisher publisher;
    @JsonProperty("journal")
    private JsonJournal journal;
    @JsonProperty("lastmodified")
    private String lastmodified;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("pubmedid")
    public long getPubmedId() {
        return pubmedId;
    }

    @JsonProperty("pubmedid")
    public void setPubmedId(long pubmedId) {
        this.pubmedId = pubmedId;
    }

    @JsonProperty("sciid")
    public String getSciId() {
        return sciId;
    }

    @JsonProperty("sciid")
    public void setSciId(String sciId) {
        this.sciId = sciId;
    }

    @JsonProperty("doi")
    public String getDoi() {
        return doi;
    }

    @JsonProperty("doi")
    public void setDoi(String doi) {
        this.doi = doi;
    }

    @JsonProperty("title")
    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("author")
    public List<JsonAuthor> getAuthors() {
        return authors;
    }

    @JsonProperty("author")
    public void setAuthors(List<JsonAuthor> authors) {
        this.authors = authors;
    }

    @JsonProperty("rawauthor")
    public String getRawAuthors() {
        return rawAuthors;
    }

    @JsonProperty("rawauthor")
    public void setRawAuthors(String rawAuthors) {
        this.rawAuthors = rawAuthors;
    }

    @JsonProperty("corrauthaddr")
    public String getCorrAuthorAddress() {
        return corrAuthorAddress;
    }

    @JsonProperty("corrauthaddr")
    public void setCorrAuthorAddress(String corrAuthorAddress) {
        this.corrAuthorAddress = corrAuthorAddress;
    }

    @JsonProperty("year")
    public Integer getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(Integer year) {
        this.year = year;
    }

    @JsonProperty("pubdate")
    public String getPubdate() {
        return pubdate;
    }

    @JsonProperty("pubdate")
    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("publisher")
    public JsonPublisher getPublisher() {
        return publisher;
    }

    @JsonProperty("publisher")
    public void setPublisher(JsonPublisher publisher) {
        this.publisher = publisher;
    }

    @JsonProperty("journal")
    public JsonJournal getJournal() {
        return journal;
    }

    @JsonProperty("journal")
    public void setJournal(JsonJournal journal) {
        this.journal = journal;
    }

    @JsonProperty("lastmodified")
    public String getLastmodified() {
        return lastmodified;
    }

    @JsonProperty("lastmodified")
    public void setLastmodified(String lastmodified) {
        this.lastmodified = lastmodified;
    }

    public void addAuthor(JsonAuthor author) {
        if (this.authors == null) {
            this.authors = new ArrayList<>();
        }
        this.authors.add(author);
    }

    @Override
    public String publisher() {
        JsonPublisher jsonPublisher = getPublisher();
        if (jsonPublisher != null) {
            return jsonPublisher.getName();
        }
        return null;
    }

    @Override
    public String publicationType() {
        return getType();
    }

    @Override
    public List<String> issn() {
        JsonJournal jsonJournal = getJournal();
        if (jsonJournal != null) {
            List<String> issns = jsonJournal.getIssn();
            if (issns != null) {
                return issns;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public int year() {
        Integer year = getYear();
        return year == null ? 0 : year;
    }

    @Override
    public boolean isAffiliated() {
        return true;
    }

}
