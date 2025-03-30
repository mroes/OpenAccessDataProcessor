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
import at.roesel.oadataprocessor.persistance.conversion.DateConverter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "publishervar")
public class PublisherVar {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "parentid")
    private String parentId;

    @Column(name = "wikiparentid")
    private String wikiParentId;

    @Column(name = "startdate")
    @Convert(converter = DateConverter.class)
    private LocalDate startDate;

    @Column(name = "enddate")
    @Convert(converter = DateConverter.class)
    private LocalDate endDate;

    @Column(name = "created")
    private Long created;

    @Column(name = "updated")
    private Long updated;

    public PublisherVar() {
        startDate = DateSupport.MIN_DATE;
        endDate = DateSupport.MAX_DATE;
    }

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
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

    public String getWikiParentId() {
        return wikiParentId;
    }

    public void setWikiParentId(String wikiParentId) {
        this.wikiParentId = wikiParentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startdate) {
        this.startDate = startdate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate enddate) {
        this.endDate = enddate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublisherVar that = (PublisherVar) o;
        // publisherId ist not included in equals because only objects of the same publisher are compared
        return /* publisherId.equals(that.publisherId) && */ Objects.equals(wikiParentId, that.wikiParentId) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wikiParentId, startDate, endDate);
    }

    @Override
    public String toString() {
        return "PublisherVar{" +
                "wikiParentId='" + wikiParentId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
