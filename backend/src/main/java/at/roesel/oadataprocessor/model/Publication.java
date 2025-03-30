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

package at.roesel.oadataprocessor.model;

import at.roesel.common.DateSupport;
import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.persistance.conversion.AuthorConverter;
import at.roesel.oadataprocessor.persistance.conversion.SimpleSetConverter;
import at.roesel.oadataprocessor.support.TitleSupport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.model.ui.PublicationColor.UNKNOWN;
import static at.roesel.oadataprocessor.support.IssnSupport.normalizeIssn;

/*
 * Main publication table
 * Every publication should be unique in this table
 * But it's not always possible to find an already existing publication, so in reality it will happen that
 * a publication could appear multiple times in this table
 */
@Entity
@Table(name = "publication")
public class Publication implements IPublication {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String doi;

    @Column(name = "doi_src")
    private int doiSource;
    public static final int DOI_NOT_FOUND_CROSSREF = -1;
    public static final int DOI_TOO_MANY_RESULTS = -2;
    public static final int DOI_FOUND_FALSE_POSITIVE_CROSSREF = -3;
    public static final int DOI_NOT_FOUND_DATACITE = -4;
    public static final int DOI_SOURCE_REPOSITORY = 0;
    public static final int DOI_SOURCE_CROSSREF = 1;
    public static final int DOI_SOURCE_MANUAL = 2;
    public static final int DOI_SOURCE_DATACITE = 3;

    private String title;

    @Column(name = "titlehash")
    private int titleHash;

    @Column(name = "author")
    @Convert(converter = AuthorConverter.class)
    private List<Author> authors;

    // published in year
    private int year;

    // publish date
    @Column(name = "pubdate")
    private int date;

    // type of publication
    @Column(name = "pubtypeid")
    private int pubtypeId;

    @Transient
    private PublicationType publicationType;

    // name of the publisher as given by the institution
    @Column(name = "publisher")
    private String publisher;

    // Publisher identified for the publication year
    @Column(name = "publisherid")
    private String publisherId;

    // Main Publisher for the publisher in the publication year
    @Column(name = "mainpublisherid")
    private String mainPublisherId;

    @Column(name = "issn")
    @Convert(converter = SimpleSetConverter.class)
    private Set<String> issn;

    // identified journal
    @Column(name = "journalid")
    private String journalId;
    public static String UNKNOWN_PREFIX = "-";
    public static String NO_ISSN_AVAILABLE = "-ni";
    public static String UNKNOWN_JOURNAL = "-uj";

    private String coat;
    private String color = UNKNOWN;

    @Column(name = "colorupw")
    private String colorUpw = UNKNOWN;    // Color from Unpaywall

    // normalized licence
    @Column(name = "licence")
    private String licence;

    @Column(name = "classification")
    private int classificationStatus;
    public static final int CLASSIFICATION_TODO = 0;
    public static final int CLASSIFICATION_OK = 1;
    public static final int CLASSIFICATION_NOT_POSSIBLE = -1;

    @Column(name = "status")
    private int status;
    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_INACTIVE = 1; // publication record was created, but is excluded from considering for statistics

    @OneToMany(
            cascade = {CascadeType.ALL},
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "publicationid")
    private Set<SourceReference> sources;

    @Column(name = "props")
    private String props;

    private String comment;

    private long created;
    private long updated;

    @PrePersist
    protected void onCreate() {
        created = SystemTime.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = SystemTime.currentTimeMillis();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void overwriteId(String id) {
        setId(id);
        adjustPublicationIdForSources(id);
    }

    @Override
    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public int getDoiSource() {
        return doiSource;
    }

    public void setDoiSource(int doiSource) {
        this.doiSource = doiSource;
    }

    @Override
    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleAndHash(String title) {
        this.title = title;
        // A hash is generated for the title to quickly check during the search if the title matches the publication.
        int hash = TitleSupport.calculateTitleHash(this.title);
        setTitleHash(hash);
    }

    public int getTitleHash() {
        return titleHash;
    }

    public void setTitleHash(int titleHash) {
        this.titleHash = titleHash;
    }

    @Override
    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @Override
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public int getPubtypeId() {
        return pubtypeId;
    }

    public void setPubtypeId(int pubtypeId) {
        this.pubtypeId = pubtypeId;
    }

    public PublicationType getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(PublicationType publicationType) {
        this.publicationType = publicationType;
        if (publicationType != null) {
            setPubtypeId(publicationType.getId());
        }
    }

    @Override
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String getPublisherId() {
        if (publisherId == null) {
            return "";
        }
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getMainPublisherId() {
        if (mainPublisherId == null) {
            return "";
        }
        return mainPublisherId;
    }

    public void setMainPublisherId(String mainPublisherId) {
        this.mainPublisherId = mainPublisherId;
    }

    public Set<String> getIssn() {
        return issn;
    }

    public void setIssn(Collection<String> issn) {
        if (issn != null && !issn.isEmpty()) {
            this.issn = new HashSet<>(issn);
        }
    }

    public void setNormalizedIssn(Collection<String> issns) {
        if (issns != null && !issns.isEmpty()) {
            this.issn = new HashSet<>();
            for (String issn : issns) {
                String normalizedIssn = normalizeIssn(issn);
                if (hasValue(normalizedIssn)) {
                    this.issn.add(normalizedIssn);
                }
            }
        }
    }

    public boolean addIssn(Collection<String> issn) {
        if (issn != null && !issn.isEmpty()) {
            if (this.issn == null) {
                this.issn = new HashSet<>();
            }
            return this.issn.addAll(issn);
        }
        return false;
    }

    @Override
    public String getJournalId() {
        return journalId;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    @Override
    public String getCoat() {
        return coat;
    }

    public void setCoat(String coat) {
        this.coat = coat;
    }

    @Override
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String getColorUpw() {
        return colorUpw;
    }

    public void setColorUpw(String colorUpw) {
        this.colorUpw = colorUpw;
    }

    @Override
    public String getLicence() {
        if (licence == null) {
            return "";
        }
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public int getClassificationStatus() {
        return classificationStatus;
    }

    public void setClassificationStatus(int classificationStatus) {
        this.classificationStatus = classificationStatus;
    }

    @Override
    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JsonIgnore
    public boolean isActive() {
        return status == STATUS_ACTIVE;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }

    @Override
    public Set<SourceReference> getSources() {
        return sources;
    }

    public void setSources(Set<SourceReference> sources) {
        this.sources = sources;
    }

    private SourceReference findSource(String sourceId) {
        if (sources == null) {
            return null;
        }
        for (SourceReference source : sources) {
            if (source.getSourceId().equals(sourceId)) {
                return source;
            }
        }
        return null;
    }

    public boolean updateSources(Set<SourceReference> newSources) {
        boolean changed = false;
        if (sources == null) {
            sources = new HashSet<>();
        }
        for (SourceReference newSource : newSources) {
            newSource.setPublicationId(getId());
            SourceReference source = findSource(newSource.getSourceId());
            if (source == null) {
                sources.add(newSource);
                changed = true;
            } else {
                if (!Objects.equals(source.getOrganisation(), newSource.getOrganisation())) {
                    source.setOrganisation(newSource.getOrganisation());
                    changed = true;
                }
                if (!Objects.equals(source.getNativeId(), newSource.getNativeId())) {
                    source.setNativeId(newSource.getNativeId());
                    changed = true;
                }
            }
        }
        return changed;
    }

    public void addReference(SourceReference ref) {
        if (sources == null) {
            sources = new HashSet<>();
        }
        if (ref != null) {
            // remove ref if existing, so that the new ref with eventually updated fields (e.g. organisation) is added
            sources.remove(ref);
            sources.add(ref);
        }
    }

    private void adjustPublicationIdForSources(String publicationId) {
        if (sources != null) {
            for (SourceReference reference : sources) {
                reference.setPublicationId(publicationId);
            }
        }
    }

    public List<String> institutions() {
        Set<String> result = new HashSet<>();
        if (sources != null) {
            for (SourceReference reference : sources) {
                result.add(reference.getInstitutionId());
            }
        }
        return new ArrayList<>(result);
    }

    public List<String> nativeIds() {
        Set<String> result = new HashSet<>();
        if (sources != null) {
            for (SourceReference reference : sources) {
                result.add(reference.getNativeId());
            }
        }
        return new ArrayList<>(result);
    }

    @Override
    public String toString() {
        return "Publication{" +
                "id='" + id + '\'' +
                ", doi='" + doi + '\'' +
                '}';
    }

    public boolean hasAuthor(String lastName, String firstName) {
        firstName = firstName.toLowerCase();
        if (authors != null && !authors.isEmpty()) {
            for (Author author : authors) {
                if (author.getLastName().equalsIgnoreCase(lastName)) {
                    if (author.getFirstName().toLowerCase().startsWith(firstName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public LocalDate publicationDate() {
        // return the publication date if there is one
        if (date != 0) {
            return DateSupport.fromIntDate(date);
        }
        // create a date of the publication year, take the middle of the year
        return LocalDate.of(year, 6, 30);

    }

    // if the journalId has a special value, reset it, so that it is considered in the next search
    public void resetJournalIdStatus() {
        if (journalId != null && journalId.startsWith(UNKNOWN_PREFIX)) {
            journalId = null;
        }
    }

    // if the publisherId has a special value, reset it, so that it is considered in the next search
    public void resetPublisherIdStatus() {
        if (publisherId != null && publisherId.startsWith(UNKNOWN_PREFIX)) {
            publisherId = "";
        }
    }
}
