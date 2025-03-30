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

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.model.wikidata.WikidataJsonPublisher;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import static at.roesel.common.BeanSupport.addNewValue;

@Entity
@Table(name = "publisher")
public class Publisher {

    public static String UNKNOWN_PUBLISHER = "-up";

    public final static String listDelimiter = ",";
    public final static String nameDelimiter = "~";

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "parentid")
    private String parentId;

    @JsonIgnore
    @Column(name = "mainid")
    private String mainId;

    @JsonIgnore
    @Column(name = "created")
    private Long created;

    @JsonIgnore
    @Column(name = "updated")
    private Long updated;

    @JsonIgnore
    @Column(name = "updated_wikidata")
    private Long updatedWikidata;

    @Column(name = "wikiid")
    private String wikiId;

    // used if there is only one parent from WikiData, otherwise the parents are stored in variable
    @JsonIgnore
    @Column(name = "wikiparentid")
    private String wikiParentId;

    @Column(name = "name")
    private String name;

    // alternative names are stored as string list, separated by nameDelimiter
    @JsonIgnore
    @Column(name = "alias")
    private String alias;

    @JsonIgnore
    @Column(name = "ror")
    private String ror;

    @JsonIgnore
    @Column(name = "isni")
    private String isni;

    @JsonIgnore
    @Column(name = "ringgoldid")
    private String ringgoldId;

    @JsonIgnore
    @Column(name = "romeoid")
    private String romeoId;

    @JsonIgnore
    public final static int RELEVANT = 0;
    @JsonIgnore
    @Column(name = "flag")
    // 0 relevant for datahub
    // 1 ignore
    private int flag;

    // 0
    // 1 highest publisher level, stop searching for a main publisher if a publisher has set this flag
    @JsonIgnore
    @Column(name = "top")
    private int top;

    @JsonIgnore
    @Column(name = "wikiinstanceof")
    private String wikiInstanceOf;

    @JsonIgnore
    @Column(name = "prefix")
    private String prefix;

    @JsonIgnore
    @OneToMany(
            cascade = {CascadeType.ALL},
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "publisherid")
    private Set<PublisherVar> variable = new HashSet<>();

    @Transient
    private int publicationCount;

    @PrePersist
    protected void onCreate() {
        created = SystemTime.currentTimeMillis();
        updated = 0L;
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updated = SystemTime.currentTimeMillis();
    }

    public Publisher() {
    }

    public Publisher(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Publisher(String id, String name, int publicationCount) {
        this(id, name);
        this.publicationCount = publicationCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public String getWikiParentId() {
        return wikiParentId;
    }

    public void setWikiParentId(String wikiParentId) {
        this.wikiParentId = wikiParentId;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Long getUpdatedWikidata() {
        return updatedWikidata;
    }

    public void setUpdatedWikidata(Long updateWikidata) {
        this.updatedWikidata = updateWikidata;
    }

    public String getWikiId() {
        return wikiId;
    }

    public void setWikiId(String wikiId) {
        this.wikiId = wikiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRor() {
        return ror;
    }

    public void setRor(String ror) {
        this.ror = ror;
    }

    public String getIsni() {
        return isni;
    }

    public void setIsni(String isni) {
        this.isni = isni;
    }

    public String getRinggoldId() {
        return ringgoldId;
    }

    public void setRinggoldId(String ringgoldId) {
        this.ringgoldId = ringgoldId;
    }

    public String getRomeoId() {
        return romeoId;
    }

    public void setRomeoId(String romeoId) {
        this.romeoId = romeoId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getWikiInstanceOf() {
        return wikiInstanceOf;
    }

    public void setWikiInstanceOf(String wikiInstanceOf) {
        this.wikiInstanceOf = wikiInstanceOf;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Set<PublisherVar> getVariable() {
        return variable;
    }

    public void setVariable(Set<PublisherVar> variable) {
        // this.variable = variable;
        this.variable.clear();
        this.variable.addAll(variable);
    }

    public void addPublisherVar(PublisherVar publisherVar) {
        this.variable.add(publisherVar);
    }

    public int getPublicationCount() {
        return publicationCount;
    }

    public void setPublicationCount(int publicationCount) {
        this.publicationCount = publicationCount;
    }

    public List<PublisherVar> getParentEntries(String parentPublisherId) {
        List<PublisherVar> result = new ArrayList<>();
        if (variable != null) {
            for (PublisherVar var : variable) {
                if (var.getParentId().equals(parentPublisherId)) {
                    result.add(var);
                }
            }
        }
        return result;
    }

    public static Publisher fromWikidataJson(WikidataJsonPublisher jsonPublisher) {
        Publisher publisher = new Publisher();
        publisher.setFlag(Publisher.RELEVANT);   // standardmäßig wird der Publisher aktiviert
        publisher.setId(UUID.randomUUID().toString());
        publisher.setName(jsonPublisher.publisherLabel);
        publisher.setWikiId(jsonPublisher.wikiId());
        publisher.setWikiParentId(jsonPublisher.wikiParentId());
        publisher.setIsni(jsonPublisher.isni);
        publisher.setRinggoldId(jsonPublisher.ringgoldid);
        publisher.setRor(jsonPublisher.ror);
        publisher.setRomeoId(jsonPublisher.romeoid);
        publisher.setWikiInstanceOf(jsonPublisher.instanceOf());
        return publisher;
    }

    @Override
    public String toString() {
        return name;
    }

    public PublisherVar variable(LocalDate date) {
        if (variable != null && (!variable.isEmpty())) {
            for (PublisherVar var : variable) {
                if (!var.getStartDate().isAfter(date) && !var.getEndDate().isBefore(date)) {
                    return var;
                }
            }
        }
        return null;
    }

    /*
     * @return true if added
     */
    public boolean addWikiParentId(String wikiParentId) {
        return addNewValue(this, "wikiParentId", wikiParentId, listDelimiter);
    }

    /*
     * @return true if added
     */
    public boolean addIsni(String isni) {
        return addNewValue(this, "isni", isni, listDelimiter);
    }

    /*
     * @return true if added
     */
    public boolean addRor(String ror) {
        return addNewValue(this, "ror", ror, listDelimiter);
    }

    /*
     * @return true if added
     */
    public boolean addRinggoldId(String ringgoldId) {
        return addNewValue(this, "ringgoldId", ringgoldId, listDelimiter);
    }

    /*
     * @return true if added
     */
    public boolean addWikiInstanceOf(String wikiInstanceOf) {
        return addNewValue(this, "wikiInstanceOf", wikiInstanceOf, listDelimiter);
    }

    @JsonIgnore
    public boolean isIgnored() {
        return flag != RELEVANT;
    }

    public String[] getAlternateNames() {
        if (alias == null || alias.isEmpty()) {
            return new String[]{};
        }
        String[] alternateNames = alias.split(Pattern.quote(nameDelimiter));
        return alternateNames;
    }

    public boolean isEqualName(String name) {
        boolean result = getName().equalsIgnoreCase(name);
        if (!result) {
            String[] aliasNames = getAlternateNames();
            for (String alias : aliasNames) {
                result = alias.equalsIgnoreCase(name);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    public boolean addAlias(String alias) {
        return addNewValue(this, "alias", alias, nameDelimiter);
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public boolean isTopPublisher() {
        return top == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publisher publisher = (Publisher) o;
        return Objects.equals(id, publisher.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
