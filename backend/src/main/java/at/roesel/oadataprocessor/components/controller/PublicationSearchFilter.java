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

package at.roesel.oadataprocessor.components.controller;

import java.util.Objects;

public class PublicationSearchFilter {
    public String institution;
    public int year;
    public String doi;
    public String title;
    public String author;
    public String color;
    public String upwColor;
    public String publisherId;
    public String publisher;
    public boolean filterMainPublisher; // Filter by main publisher
    public String journal;
    public boolean includeUnknown;
    public ExcelDownloadOptions options;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicationSearchFilter that = (PublicationSearchFilter) o;
        return year == that.year && filterMainPublisher == that.filterMainPublisher && includeUnknown == that.includeUnknown && Objects.equals(institution, that.institution) && Objects.equals(doi, that.doi)
                && Objects.equals(title, that.title) && Objects.equals(author, that.author) && Objects.equals(color, that.color) && Objects.equals(upwColor, that.upwColor)
                && Objects.equals(publisherId, that.publisherId) && Objects.equals(publisher, that.publisher) && Objects.equals(journal, that.journal)
                && Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(institution, year, doi, title, author, color, upwColor, publisherId, publisher, filterMainPublisher, journal, includeUnknown, options);
    }

    public String getCacheKey() {
        String key = String.valueOf(hashCode());
        return key;

    }

    public static class ExcelDownloadOptions {
        public boolean includePublicationId;
        public boolean includeNativeIds;
        public boolean includeMetaSources;
        public boolean includeAuthor;
        public boolean multipleRows; // Output has one row for every publication for every connected institution

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExcelDownloadOptions that = (ExcelDownloadOptions) o;
            return includePublicationId == that.includePublicationId && includeNativeIds == that.includeNativeIds && includeMetaSources == that.includeMetaSources && includeAuthor == that.includeAuthor && multipleRows == that.multipleRows;
        }

        @Override
        public int hashCode() {
            return Objects.hash(includePublicationId, includeNativeIds, includeMetaSources, includeAuthor, multipleRows);
        }
    }
}
