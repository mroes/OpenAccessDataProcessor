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

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "publication_institution")
public class SourceReference {

    @Column(name = "publicationid")
    private String publicationId;

    @Column(name = "institutionid")
    private String institutionId;  // ROR ID of institution

    @Id
    @Column(name = "sourceid")
    private String sourceId;  // UUID in source

    @Column(name = "nativeid")
    private String nativeId;  // native Identifier in source

    @Column(name = "organisation")
    private String organisation;

    public final static int CORRESPONDING = 1;
    @Column(name = "corr")
    private int corresponding;  // 1 if institution has a corresponding author


    public SourceReference(String publicationId, String institutionId, String sourceId, String nativeId) {
        this.publicationId = publicationId;
        this.institutionId = institutionId;
        this.sourceId = sourceId;
        this.nativeId = nativeId;
    }

    public SourceReference() {
        this(null, null, null, null);
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getNativeId() {
        return nativeId;
    }

    public void setNativeId(String nativeId) {
        this.nativeId = nativeId;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public int getCorresponding() {
        return corresponding;
    }

    public void setCorresponding(int corresponding) {
        this.corresponding = corresponding;
    }

    public boolean isCorresponding() {
        return corresponding == CORRESPONDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceReference that = (SourceReference) o;
        return sourceId.equals(that.sourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId);
    }

    @Override
    public String toString() {
        return "SourceReference{" +
                "institutionId='" + institutionId + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", nativeId='" + nativeId + '\'' +
                '}';
    }
}
