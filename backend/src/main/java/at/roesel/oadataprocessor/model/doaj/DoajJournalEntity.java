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

package at.roesel.oadataprocessor.model.doaj;

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.model.Amount;

import jakarta.persistence.*;

@Entity
@Table(name = "doaj")
public class DoajJournalEntity implements DoajJournal {
    @Id
    private String id;
    private String title;
    @Column(name = "alternativetitle")
    private String alternativeTitle;
    private String issn;
    private String eissn;
    private String licence;
    private long apc_amount;
    private String apc_currency;

    @Column(name = "oa_start")
    private int oaStart;

    private String publisher;

    @Column(name = "startdate")
    private int startDate;  // in Doaj seit
    @Column(name = "enddate")
    private int endDate;  // in Doaj bis

    private long created;

    private long updated;

    private long deleted;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getEissn() {
        return eissn;
    }

    public void setEissn(String eissn) {
        this.eissn = eissn;
    }

    public String getLicence() {
        return licence;
    }

    @Transient
    @Override
    public Amount getApcAmount() {
        return new Amount(apc_amount, apc_currency);
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public long getApc_amount() {
        return apc_amount;
    }

    public void setApc_amount(long apc_amount) {
        this.apc_amount = apc_amount;
    }

    public String getApc_currency() {
        return apc_currency;
    }

    public void setApc_currency(String apc_currency) {
        this.apc_currency = apc_currency;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public int getOaStart() {
        return oaStart;
    }

    public void setOaStart(int oaStart) {
        this.oaStart = oaStart;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
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

    public long getDeleted() {
        return deleted;
    }

    public void setDeleted(long deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "DoajJournal{" +
                "title='" + title + '\'' +
                ", issn='" + issn + '\'' +
                ", eissn='" + eissn + '\'' +
                '}';
    }

    public boolean isDeleted() {
        return deleted > 0;
    }
}

