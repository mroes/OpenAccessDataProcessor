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

package at.roesel.oadataprocessor.model.ui;

import jakarta.persistence.*;


@Entity
@IdClass(PublicationColorId.class)
public class PublicationColor {

    public static final String UNKNOWN = "Unknown";
    public static final String ERROR = "error";
    @Id
    @Column(name = "institutionid")
    public String institutionId;
    @Id
    public String name;
    @Id
    public int year;
    @Id
    @Column(name = "publisherid")
    public String publisherId;

    public int value;

    @Column(name = "mainpublisherid")
    public String mainPublisherId;

    // true, if at least one of the corresponding authors belongs to the institution
    @Column(name = "corr")
    public boolean corresponding;

    public PublicationColor() {
        this(null, 0);
    }

    public PublicationColor(String name, int value) {
        if (name == null || name.equals(ERROR)) {
            this.name = UNKNOWN;
        } else {
            this.name = name;
        }
        this.value = value;
        institutionId = "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name() {
        if (name == null || name.equals(ERROR)) {
            return UNKNOWN;
        }
        return name;
    }

    public boolean isCorresponding() {
        return corresponding;
    }

    public void setCorresponding(boolean corresponding) {
        this.corresponding = corresponding;
    }

    @Override
    public String toString() {
        return "PublicationColor{" +
                "institutionId='" + institutionId + '\'' +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", value=" + value +
                '}';
    }
}
