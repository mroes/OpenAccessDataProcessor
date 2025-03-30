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

package at.roesel.oadataprocessor.model.openapc;

import at.roesel.common.SystemTime;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "openapc")
public class OpenApcEntity implements OpenApcRecord {

    public final static int RECORD = 0;
    public final static int AGREEMENT = 1;
    private static final long TA_AMOUNT = 9999999L;

    @Id
    private String id; // doi

    private String institution;
    private int period;
    private long amount;
    private String doi;
    private boolean is_hybrid;
    private String publisher;
    private String journal_full_title;
    private String issn;
    private String issn_print;
    private String issn_electronic;
    private String issn_l;
    private String license_ref;
    private boolean indexed_in_crossref;
    private String pmid;
    private String pmcid;
    private String ut;
    private String url;
    private boolean doaj;

    private String agreement;

    private boolean ta; // tranformative Agreement

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public boolean isIs_hybrid() {
        return is_hybrid;
    }

    public void setIs_hybrid(boolean is_hybrid) {
        this.is_hybrid = is_hybrid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getJournal_full_title() {
        return journal_full_title;
    }

    public void setJournal_full_title(String journal_full_title) {
        this.journal_full_title = journal_full_title;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getIssn_print() {
        return issn_print;
    }

    public void setIssn_print(String issn_print) {
        this.issn_print = issn_print;
    }

    public String getIssn_electronic() {
        return issn_electronic;
    }

    public void setIssn_electronic(String issn_electronic) {
        this.issn_electronic = issn_electronic;
    }

    public String getIssn_l() {
        return issn_l;
    }

    public void setIssn_l(String issn_l) {
        this.issn_l = issn_l;
    }

    public String getLicense_ref() {
        return license_ref;
    }

    public void setLicense_ref(String license_ref) {
        this.license_ref = license_ref;
    }

    public boolean isIndexed_in_crossref() {
        return indexed_in_crossref;
    }

    public void setIndexed_in_crossref(boolean indexed_in_crossref) {
        this.indexed_in_crossref = indexed_in_crossref;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getPmcid() {
        return pmcid;
    }

    public void setPmcid(String pmcid) {
        this.pmcid = pmcid;
    }

    public String getUt() {
        return ut;
    }

    public void setUt(String ut) {
        this.ut = ut;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDoaj() {
        return doaj;
    }

    public void setDoaj(boolean doaj) {
        this.doaj = doaj;
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

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public boolean isTa() {
        return ta;
    }

    public void setTa(boolean ta) {
        this.ta = ta;
    }

    @Transient
    @Override
    public boolean isHybrid() {
        return is_hybrid;
    }

    @Transient
    public long getApcAmount() {
        if (isTa()) {
            // Transformative Agreements always have an amount
            if (amount < 0) {
                return TA_AMOUNT;
            }
        }
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenApcEntity that = (OpenApcEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

