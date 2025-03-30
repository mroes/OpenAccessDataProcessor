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

package at.roesel.oadataprocessor.services.impexp.export;

import at.roesel.oadataprocessor.components.controller.PublicationSearchFilter;
import at.roesel.oadataprocessor.model.coat.ClassificationOutput;
import at.roesel.oadataprocessor.services.impexp.OutputDescriptor;

import java.util.Arrays;
import java.util.List;

public class PublicationOutputDescriptor extends OutputDescriptor<ClassificationOutput> {
    private final static List<String> headers = Arrays.asList(
            "id",
            "doi", "title", "year",
            "institution",
            "institution.name",
            "institution.corresponding",
            "institution.pub.id",
            "type",
            "type.name",
            // "publication.date",
            "journal.title",
            "journal.id",
            "issn",
            "publisher.name", "publisher.id",
            "mainPublisher.name", "mainPublisher.id",
            "licence", "licence.url","licence.source",
            "version",
            // "version.source",
            "costs", "costs.currency", "costs.source",
            "embargo.time",
            // "embargo.date",
            "embargo.source",
            "oaversion.link","oa.place",
            //"oa.place.source",
            "coat", "color", "upw.color",
            "author"
    );

    public PublicationOutputDescriptor(PublicationSearchFilter.ExcelDownloadOptions options) {
        super(ClassificationOutput.class, headers.stream().filter(header -> {
            return switch (header) {
                case "id" -> options.includePublicationId;
                case "institution.pub.id" -> options.includeNativeIds;
                case "licence.source", "costs.source", "embargo.source" -> options.includeMetaSources;
                case "author" -> options.includeAuthor;
                default -> true;
            };
        }).toList());
    }


    @Override
    protected String mapField(String fieldName) {
        switch(fieldName) {
            case "institution.name" : return "institutionName";
            case "institution.corresponding" : return "institutionCorresponding";
            case "institution.pub.id" : return "nativeId";
            case "type" : return "pubType";
            case "type.name" : return "pubTypeName";
            case "publisher.name" : return "publisherName";
            case "publisher.id" : return "publisherId";
            case "mainPublisher.name" : return "mainPublisherName";
            case "mainPublisher.id" : return "mainPublisherId";
            case "journal.title" : return "journalTitle";
            case "journal.id" : return "journalId";
            case "licence.url" : return "licenceUrl";
            case "licence.source" : return "licenceSource";
            case "version.source" : return "versionSource";
            case "costs.currency" : return "costsCurrency";
            case "costs.source" : return "costsSource";
            case "embargo.time" : return "embargoTime";
            case "publication.date" : return "publicationDate";
            case "embargo.date" : return "embargoDate";
            case "embargo.source" : return "embargoSource";
            case "oaversion.link" : return "oaversionLink";
            case "oa.place" : return "oaPlace";
            case "oa.place.source" : return "oaPlaceSource";
            case "upw.color" : return "upwColor";
        }
        return fieldName;
    }

}
