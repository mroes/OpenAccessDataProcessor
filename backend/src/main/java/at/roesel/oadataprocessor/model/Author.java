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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "author")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Author {

    @Id
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private String id;

    @Column(name = "firstname")
    public String firstName;
    @Column(name = "lastname")
    public String lastName;
    @Column(name = "orcid")
    public String orcid;
    public String initial; // Initiale (Vorname)

    public static final int AUTHOR_SOURCE_ORCID = 1;
    public static final int AUTHOR_SOURCE_ORCID_EDITED = 2;   // von Orcid, aber bearbeitet
    public static final int AUTHOR_SOURCE_ORCID_MANUEL = 3;   // von Orcid, aber per manueller Suche gefunden
    public int source;

    private long created;
    private long updated;


    @Transient
    public String role;
    // von Pure, personRole = 5032, uri = /dk/atira/pure/researchoutput/roles/contributiontojournal/corresponding_author
    public static final String CORRESPONDING_AUTHOR = "corr";

    @Transient
    public boolean isCorresponding;

    @Transient
    public String organisation;

    @PrePersist
    protected void onCreate() {
        created = SystemTime.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = SystemTime.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Author{" + lastName + ", " + firstName + " (" + organisation + ")}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(firstName, author.firstName) && Objects.equals(lastName, author.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        if (firstName == null) {
            return "";
        }
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if (lastName == null) {
            return "";
        }
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public boolean isCorresponding() {
        return isCorresponding;
    }

    public void setCorresponding(boolean corresponding) {
        isCorresponding = corresponding;
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

    public String firstName() {
        if (firstName == null) {
            return "";
        }
        return firstName;
    }

    public String lastName() {
        if (lastName == null) {
            return "";
        }
        return lastName;
    }

    public String initial() {
        if (initial == null) {
            return "";
        }
        return initial;
    }



}
