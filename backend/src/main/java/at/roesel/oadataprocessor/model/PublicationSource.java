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

import jakarta.persistence.*;

import java.util.Objects;

/*
 * "raw" publication data from the sources (institutions)
 * Every imported publication has a record in the table source
 */
@Entity
@Table(name = "source")
public class PublicationSource {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "nativeid")
    private String nativeId;

    @Lob
    @Column(name = "title")
    private String title;

    @Column(name = "doi")
    private String doi;

    @Column(name = "pubtype")
    private String pubtype; // article, book, ..

    @Column(name = "year")
    private int year; // publication year

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    @Column(name = "institution")
    private String institution;

    @Column(name = "affiliated")
    private int affiliated = AFFILIATED_YES;
    public static final int AFFILIATED_NO = 0;
    public static final int AFFILIATED_YES = 1;

    @Lob
    @Column(name = "record")
    private String record;

    @Column(name = "datatype")
    private String dataType;

    /*
    0 : not taken over into the publication table
    1 : taken over into the publication table
    -1 : invalid doi
     */
    @Column(name = "status")
    private PublicationSourceStatus status = PublicationSourceStatus.NEW_OR_CHANGED;

    @PrePersist
    protected void onCreate() {
        created = SystemTime.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = SystemTime.currentTimeMillis();
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public int getAffiliated() {
        return affiliated;
    }

    public void setAffiliated(int affiliated) {
        this.affiliated = affiliated;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNativeId() {
        return nativeId;
    }

    public void setNativeId(String nativeId) {
        this.nativeId = nativeId;
    }

    public String getPubtype() {
        return pubtype;
    }

    public void setPubtype(String pubtype) {
        this.pubtype = pubtype;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PublicationSourceStatus getStatus() {
        return status;
    }

    public void setStatus(PublicationSourceStatus status) {
        this.status = status;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isDataEqual(PublicationSource source) {
        if (!Objects.equals(getTitle(), source.getTitle())) {
            return false;
        }
        if (!Objects.equals(getDoi(), source.getDoi())) {
            return false;
        }
        if (!Objects.equals(getDataType(), source.getDataType())) {
            return false;
        }
        if (!Objects.equals(getPubtype(), source.getPubtype())) {
            return false;
        }
        if (getYear() != source.getYear()) {
            return false;
        }
        if (!getRecord().equals(source.getRecord())) {
            return false;
        }
        return true;
    }
}
