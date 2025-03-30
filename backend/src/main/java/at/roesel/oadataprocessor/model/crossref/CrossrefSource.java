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

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.model.SourceStatus;
import at.roesel.oadataprocessor.persistance.conversion.CrossrefWorkConverter;

import jakarta.persistence.*;

@Entity
@Table(name = "crossref")
public class CrossrefSource {
    @Id
    @Column(name = "doi", nullable = false)
    private String doi;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    @Lob
    @Column(name = "record")
    @Convert(converter = CrossrefWorkConverter.class)
    private CrossrefWork record;

    @Column(name = "status")
    private SourceStatus status;

    public CrossrefSource() {
    }

    public CrossrefSource(String doi, CrossrefWork crossrefWork) {
        this.doi = doi;
        this.record = crossrefWork;
        setStatus(crossrefWork.status());
    }

    @PrePersist
    protected void onCreate() {
        created = SystemTime.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = SystemTime.currentTimeMillis();
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

    public CrossrefWork getRecord() {
        return record;
    }

    public void setRecord(CrossrefWork record) {
        this.record = record;
    }

    public SourceStatus getStatus() {
        return status;
    }

    public void setStatus(SourceStatus status) {
        this.status = status;
    }
}
