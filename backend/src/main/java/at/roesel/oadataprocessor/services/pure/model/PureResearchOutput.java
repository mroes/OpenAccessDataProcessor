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

package at.roesel.oadataprocessor.services.pure.model;

import at.roesel.oadataprocessor.model.Author;
import at.roesel.oadataprocessor.model.PublicationSourceRecord;
import at.roesel.oadataprocessor.support.DoiSupport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static at.roesel.common.StringSupport.hasValue;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PureResearchOutput implements PublicationSourceRecord {
    public long pureId;
    public String externalId;
    public String externalIdSource;
    public UUID uuid;
    public PureTitle title;
    public Boolean peerReview;
    public Integer numberOfPages;
    public Boolean confidential;
    public PureManagingOrganisationalUnit managingOrganisationalUnit;
    public PureInfo info;
    public PureJournalAssociation journalAssociation;
    public String journalNumber;
    public PureType type;
    public PureType category;
    public PureType language;
    public PureFormattedLocaleText translatedTitle;
    /* excluded from import
    @JsonProperty("abstract")
    public PureFormattedLocaleText summary;
     */
    public int totalNumberOfAuthors;
    public PureType openAccessPermission;
    public PureBibliographicalNote bibliographicalNote;
    public PureWorkflow workflow;
    public PureVisbility visibility;
    public List<PurePublicationStatus> publicationStatuses;
    public List<PurePersonAssociation> personAssociations;
    public List<PureOrganisation> organisationalUnits;
    public List<PureOrganisation> externalOrganisations;
    public List<PureElectronicVersion> electronicVersions;
    /* excluded from import
    public List<PureKeywordGroup> keywordGroups;
    */

    public String error;

    public boolean isValid() {
        return error == null;
    }

    @JsonIgnore
    public String resolvedTitle() {
        if (title != null) {
            return title.value;
        }
        return "";
    }

    @JsonIgnore
    public String resolvedDoi() {
        if (electronicVersions != null) {
            for (PureElectronicVersion electronicVersion : electronicVersions) {
                if (electronicVersion.doi != null) {
                    return electronicVersion.doi;
                }
            }
        }

        if (bibliographicalNote != null) {
            for (PureLocaleText textEntry : bibliographicalNote.text) {
                if (textEntry.value != null) {
                    // Value can contain multiple lines separated by \r
                    // or separated by \r\n
                    String[] lines = textEntry.value.split("\r");
                    for (String line : lines) {
                        String doi = DoiSupport.parseDoi(line.trim());
                        if (doi != null && !doi.isEmpty()) {
                            return doi;
                        }
                    }
                }
            }
        }
        return "";
    }

    @JsonIgnore
    public String resolvedJournalType() {
        if (journalAssociation != null) {
            PureJournal journal = journalAssociation.journal;
            if (journal != null && journal.type != null) {
                return journal.type.uri;
            }
        }
        return "";
    }

    @JsonIgnore
    public int resolvedPublicationYear() {
        int year = 0;
        if (publicationStatuses != null) {
            for (PurePublicationStatus purePublicationStatus : publicationStatuses) {
                if (purePublicationStatus.publicationDate != null) {
                    year = purePublicationStatus.publicationDate.year;
                    if (purePublicationStatus.publicationStatus != null) {
                        String statusUri = purePublicationStatus.publicationStatus.uri;
                        if (statusUri != null && statusUri.contains("published")) {
                            return year;
                        }
                    }
                }
            }
        }
        return year;
    }

    public List<Author> resolvedAuthors() {
        List<Author> authors = new ArrayList<>();
        if (personAssociations != null) {
            for (PurePersonAssociation personAssociation : personAssociations) {
                if (personAssociation.name == null) {
                    // e.g. UniWien			"pureId": 302266271,
                    // "uuid": "d478c628-9fe0-40e4-ba1b-567c72b09a5c",
                    continue;
                }
                Author author = new Author();
                authors.add(author);
                author.lastName = personAssociation.name.lastName;
                author.firstName = personAssociation.name.firstName;
                if (personAssociation.organisationalUnits != null) {
                    for (PureOrganisation organisation : personAssociation.organisationalUnits) {
                        author.organisation = organisation.name.firstText();
                    }
                }
                if (personAssociation.isCcorrespondingAuthor()) {
                    author.setRole(Author.CORRESPONDING_AUTHOR);
                }
            }
        }
        return authors;
    }

    // The data from Pure does not contain a publisher
    @Override
    public String publisher() {
        return null;
    }

    @Override
    public String publicationType() {
        if (type != null) {
            return type.uri;
        }
        return "";
    }

    @Override
    public List<String> issn() {
        if (journalAssociation != null) {
            String issn = journalAssociation.resolvedIssn();
            if (hasValue(issn)) {
                return Collections.singletonList(issn);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public int year() {
        return resolvedPublicationYear();
    }

    public boolean isAffiliated() {
        // A publication belongs to the institution if there is at least one associated organization
        return organisationalUnits != null && !organisationalUnits.isEmpty();
    }
}

