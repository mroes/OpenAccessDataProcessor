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

package at.roesel.oadataprocessor.model.coat;

public class ClassificationOutput {
    public String id;
    public String doi;
    public String identifier;
    public int year;
    public String institution;
    public String institutionName;
    public String institutionCorresponding;
    public String nativeId;
    public String title;

    public String pubType;
    public String pubTypeName;

    public String issn;
    public String journalTitle;
    public String journalId;

    public String publisherName;
    public String publisherId;
    public String mainPublisherName;
    public String mainPublisherId;

    public String licence;
    public String licenceUrl;
    public String licenceSource;
    public String version;
    public String versionSource;
    public String costs;
    public String costsCurrency;
    public String costsSource;
    public String embargoTime;    // 0 if there is no embargo, or the number of months
    public String publicationDate;
    public String embargoDate;
    public String embargoSource;
    public String oaversionLink;
    public String oaPlace;
    public String oaPlaceSource;

    public String coat;
    public String color;
    public String upwColor;

    public String author;

}
