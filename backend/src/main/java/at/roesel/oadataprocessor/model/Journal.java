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
import at.roesel.oadataprocessor.model.wikidata.WikidataJsonJournal;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

import static at.roesel.common.BeanSupport.addNewValue;
import static at.roesel.common.StringSupport.hasValue;

@Entity
@Table(name = "journal")
public class Journal {

    public final static String listDelimiter = ",";

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    @Column(name = "updated_wikidata")
    private Long updatedWikidata;

    @Column(name = "title")
    private String title;

    @Column(name = "wikiid")
    private String wikiId;

    @Column(name = "name")
    private String name;

    @Column(name = "publisherid")
    private String publisherId;

    @Column(name = "wikipublisherid")
    private String wikiPublisherId;

    @Column(name = "issn")
    private String issn;

    @Column(name = "issnl")
    private String issnl;

    @Column(name = "crjournalid")
    private String crJournalId;

    @Column(name = "wikiinstanceof")
    private String wikiInstanceOf;

    @Column(name = "flag")
    // 0 relevant for datahub
    // 1 ignore
    public final static int RELEVANT = 0;
    private int flag;

    @OneToMany(
            cascade = {CascadeType.ALL},
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "journalid")
    private Set<JournalVar> variable = new HashSet<>();

    @Transient
    private boolean changed;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getWikiPublisherId() {
        return wikiPublisherId;
    }

    public void setWikiPublisherId(String wikiPublisherId) {
        this.wikiPublisherId = wikiPublisherId;
    }

    public String getIssn() {
        if (issn == null) {
            return "";
        }
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getCrJournalId() {
        return crJournalId;
    }

    public void setCrJournalId(String crJournalId) {
        this.crJournalId = crJournalId;
    }

    public String getIssnl() {
        return issnl;
    }

    public void setIssnl(String issnl) {
        this.issnl = issnl;
    }

    public String getWikiInstanceOf() {
        return wikiInstanceOf;
    }

    public void setWikiInstanceOf(String wikiInstanceOf) {
        this.wikiInstanceOf = wikiInstanceOf;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Set<JournalVar> getVariable() {
        return variable;
    }

    public void setVariable(Set<JournalVar> variable) {
//        this.variable = variable;
        this.variable.clear();
        this.variable.addAll(variable);
  }

    public boolean isIgnored() {
        return flag == 1;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public static Journal fromWikidataJson(WikidataJsonJournal jsonJournal) {
        Journal journal = new Journal();
        journal.setId(UUID.randomUUID().toString());
        journal.setTitle(jsonJournal.title);
        if (hasValue(jsonJournal.journalLabel)) {
            journal.setName(jsonJournal.journalLabel);
        } else {
            journal.setName(journal.getTitle());
        }
        journal.setWikiId(jsonJournal.wikiId());
        journal.setWikiPublisherId(jsonJournal.wikiPublisherId());
        journal.setIssn(jsonJournal.issn);
        journal.setCrJournalId(jsonJournal.cr_journal_id);
        journal.setIssnl(jsonJournal.issnl);
        journal.setWikiInstanceOf(jsonJournal.instanceOf());
        return journal;
    }

    public boolean addIssn(String issn) {
        return addNewValue(this, "issn", issn, listDelimiter);
    }

    public boolean addWikiPublisherId(String wikiPublisherId) {
        return addNewValue(this, "wikiPublisherId", wikiPublisherId, listDelimiter);
    }

    public boolean addWikiInstanceOf(String wikiInstanceOf) {
        return addNewValue(this, "wikiInstanceOf", wikiInstanceOf, listDelimiter);
    }

    public boolean hasIssn(List<String> issns) {
        for (String issn : issns) {
            if (issn.equals(issnl)) {
                return true;
            }
            if (this.issn != null) {
                if (this.issn.contains(issn)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> issnsAsList() {
        if (issn != null && !issn.isEmpty()) {
            String[] issns = issn.split(listDelimiter);
            return Arrays.asList(issns);
        } else {
            return Collections.emptyList();
        }
    }

    public boolean hasWikiPublisherId() {
        if (hasValue(wikiPublisherId)) {
            return true;
        }

        if (variable == null || variable.isEmpty()) {
            return false;
        }

        for (JournalVar var : variable) {
            if (hasValue(var.getWikiPublisherId()) ) {
                return true;
            }
        }

        return false;
    }

    public String publisherId() {
        if (publisherId != null && !publisherId.isEmpty()) {
            return publisherId;
        }

        if (variable == null || variable.isEmpty()) {
            return null;
        }

        if (variable.size() == 1) {
            JournalVar var = variable.stream().findFirst().get();
            return var.getPublisherId();
        }

        // search the last record
        List<JournalVar> sortedVars = variable.stream().sorted(Comparator.comparing(JournalVar::getStartDate)).toList();
        return sortedVars.get(sortedVars.size()-1).getPublisherId();
    }

    public String publisherId(LocalDate date) {
        if (publisherId != null && !publisherId.isEmpty()) {
            return publisherId;
        }

        if (variable == null || variable.isEmpty()) {
            return null;
        }

        // Find the JournalVar whose date range includes the given date
        // Sort by latest start date first
        Optional<JournalVar> matchingVar = variable.stream()
                .filter(var -> var.getStartDate() != null && var.getEndDate() != null)
                .filter(var -> (date.isEqual(var.getStartDate()) || date.isAfter(var.getStartDate())) &&
                        (date.isEqual(var.getEndDate()) || date.isBefore(var.getEndDate())))
                .min((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate()));

        if (matchingVar.isPresent()) {
            return matchingVar.get().getPublisherId();
        }

        // If no variable matches the date, find the one with the latest start date
        return variable.stream()
                .filter(var -> var.getStartDate() != null)
                .sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate())) // Sort by latest start date first
                .map(JournalVar::getPublisherId)
                .filter(id -> id != null && !id.isEmpty())
                .findFirst()
                .orElse(null);
    }


    public boolean addVar(JournalVar newVar) {
        List<JournalVar> mergedVars = new ArrayList<>();
        JournalVar mergedVar = newVar; // Start with the new variable

        if (variable != null){
            for (JournalVar existingVar : variable) {
                if (varsOverlapOrAdjacent(existingVar, mergedVar)) {
                    // Merge the existing variable with the new variable
                    mergedVar = mergeVars(existingVar, mergedVar);
                } else {
                    // If they don't overlap or are adjacent, keep the existing variable
                    mergedVars.add(existingVar);
                }
            }
        }

        // Add the merged variable (which may be the newVar if no merge occurred)
        mergedVars.add(mergedVar);

        // Replace the original set with the merged result
        variable.clear();
        variable.addAll(mergedVars);
        return true;
    }

    private boolean varsOverlapOrAdjacent(JournalVar var1, JournalVar var2) {
        // Ensure name and publisherId match before considering date range overlap
        if (!var1.name().equals(var2.name()) || !var1.getWikiPublisherId().equals(var2.getWikiPublisherId())) {
            return false;
        }

        // Check if the date ranges overlap or are adjacent
        return !var1.getEndDate().isBefore(var2.getStartDate().minusDays(1)) && !var2.getEndDate().isBefore(var1.getStartDate().minusDays(1));    }

    private JournalVar mergeVars(JournalVar var1, JournalVar var2) {
        // Merge the start and end dates
        LocalDate start = var1.getStartDate().isBefore(var2.getStartDate()) ? var1.getStartDate() : var2.getStartDate();
        LocalDate end = var1.getEndDate().isAfter(var2.getEndDate()) ? var1.getEndDate() : var2.getEndDate();

        // Create a new JournalVar with the merged date range
        JournalVar mergedVar = new JournalVar();
        mergedVar.setId(var1.getId());
        mergedVar.setName(var1.name());
        mergedVar.setWikiPublisherId(var1.getWikiPublisherId());
        mergedVar.setPublisherId(var1.publisherId());
        mergedVar.setStartDate(start);
        mergedVar.setEndDate(end);
        mergedVar.setFlag(var1.getFlag());
        mergedVar.setCreated(var1.getCreated());
        mergedVar.setUpdated(System.currentTimeMillis());

        return mergedVar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journal journal = (Journal) o;
        return Objects.equals(id, journal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Journal{" +
                "wikiId='" + wikiId + '\'' +
                ", name='" + name + '\'' +
                ", issn='" + issn + '\'' +
                ", issnl='" + issnl + '\'' +
                '}';
    }
}
