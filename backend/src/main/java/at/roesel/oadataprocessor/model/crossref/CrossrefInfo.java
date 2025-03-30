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

import static at.roesel.common.BeanSupport.buildString;

public class CrossrefInfo {

    public String doi;
    public String type;
    public String source;
    public String title;
    public String publisher;
    public String publisherLocation;
    public String issue;
    public String language;
    public String url;
    public String isbn;
    public String issn;
    public String subject;
    public String institutions;
    public String licenses;
    public String authors;
    public String published;
    public String publishedPrint;
    public String publishedOnline;

    public static CrossrefInfo build(CrossrefWork crossrefWork) {
        CrossrefInfo info = new CrossrefInfo();
        info.doi = crossrefWork.doi;
        info.type = crossrefWork.type;
        info.source = crossrefWork.source;
        info.title = crossrefWork.title == null ? "" : crossrefWork.title.toString();
        info.publisher = crossrefWork.publisher;
        info.publisherLocation = crossrefWork.publisherLocation;
        info.issue = crossrefWork.issue;
        info.language = crossrefWork.language;
        info.url = crossrefWork.url;
        info.isbn = crossrefWork.isbn == null ? "" : crossrefWork.isbn.toString();
        info.issn = crossrefWork.issn == null ? "" : crossrefWork.issn.toString();
        info.subject = crossrefWork.subject == null ? "" : crossrefWork.subject.toString();
        info.institutions = buildString(crossrefWork.institution, inst -> inst.name);
        info.licenses = buildString(crossrefWork.license, licence -> {
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(licence.url);
            builder.append("; ");
            builder.append(licence.contentVersion);
            if (licence.start != null) {
                builder.append("; ");
                builder.append(licence.start.date());
            }
            builder.append(" }");
            return builder.toString();
        });

        info.authors = buildString(crossrefWork.author, author -> {
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(author.given);
            builder.append("; ");
            builder.append(author.family);
            if (author.sequence != null) {
                builder.append("; ");
                builder.append(author.sequence);
            }
            if (author.orcid != null) {
                builder.append("; ");
                builder.append(author.orcid);
            }
            if (author.affiliation != null) {
                builder.append("; ");
                String affiliations = buildString(author.affiliation, a -> a.name);
                builder.append(affiliations);
            }
            builder.append(" }");
            return builder.toString();
        });

        info.published = buildPrint(crossrefWork.published);
        info.publishedPrint = buildPrint(crossrefWork.publishedPrint);
        info.publishedOnline = buildPrint(crossrefWork.publishedOnline);

        return info;
    }

    private static String buildPrint(CrossrefPrint print) {
        if (print == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        builder.append(print.doi);
        builder.append("; ");
        builder.append(print.type);
        if (print.dateParts != null) {
            builder.append("; ");
            builder.append(print.date());
        }
        builder.append(" }");
        return builder.toString();
    }

    public String getDoi() {
        return doi;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublisherLocation() {
        return publisherLocation;
    }

    public String getIssue() {
        return issue;
    }

    public String getLanguage() {
        return language;
    }

    public String getUrl() {
        return url;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getIssn() {
        return issn;
    }

    public String getSubject() {
        return subject;
    }

    public String getInstitutions() {
        return institutions;
    }

    public String getLicenses() {
        return licenses;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublished() {
        return published;
    }

    public String getPublishedPrint() {
        return publishedPrint;
    }

    public String getPublishedOnline() {
        return publishedOnline;
    }
}
