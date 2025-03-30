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

import java.io.Serializable;
import java.util.Objects;

public class PublicationColorId implements Serializable {

    private String institutionId;  // (ROR) ID des Institutes
    private String name;
    private int year;

    private String publisherId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicationColorId that = (PublicationColorId) o;
        return year == that.year && Objects.equals(institutionId, that.institutionId) && Objects.equals(name, that.name) && Objects.equals(publisherId, that.publisherId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(institutionId, name, year, publisherId);
    }
}
