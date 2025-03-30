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

package at.roesel.oadataprocessor.services.oaipmh;

import at.roesel.oadataprocessor.model.DateAndYear;
import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.json.*;
import at.roesel.oadataprocessor.model.oaipmh.jabx.RecordType;
import at.roesel.oadataprocessor.persistance.conversion.ObjectConverter;
import at.roesel.oadataprocessor.services.publicationsource.BasicPublicationCreator;
import at.roesel.oadataprocessor.support.*;

import java.util.List;
import java.util.UUID;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.services.publicationsource.BokuNameParser.authorFromString;
import static at.roesel.oadataprocessor.services.oaipmh.OaiPmhSupport.dateAndYearFrom;
import static at.roesel.oadataprocessor.services.publicationsource.SourceTypes.*;
import static at.roesel.oadataprocessor.support.DateUtil.localDateToInt;
import static at.roesel.oadataprocessor.support.DoiSupport.parseDoi;
import static at.roesel.oadataprocessor.support.IdentifierSupport.coarIdFromUrl;

public class PublicationCreatorOaiPmh extends BasicPublicationCreator {

    private final DataExtractor dataExtractor;

    private final ObjectConverter<OaipmhRecord> recordConverter = new ObjectConverter<>(OaipmhRecord.class);
    protected final ObjectConverter<JsonPublication> jsonPublicationObjectConverter = new ObjectConverter<>(JsonPublication.class);

    public PublicationCreatorOaiPmh(DataExtractor dataExtractor) {
        this.dataExtractor = dataExtractor;
    }

    public PublicationCreatorOaiPmh() {
        this(new DataExtractorDefault());
    }

    public Publication from(PublicationSource source) {

        Publication publication = super.from(source);

        OaipmhRecord oaipmhRecord = recordConverter.convertToEntityAttribute(source.getRecord());

        MetaDataWrapper wrapper = new OaiDcWrapper(oaipmhRecord);

        publication.setTitle(source.getTitle());

        String dateStr = wrapper.getFieldValue("date");
        DateAndYear dateAndYear = dateAndYearFrom(dateStr);
        publication.setYear(dateAndYear.year);
        if (dateAndYear.date != null) {
            publication.setDate(localDateToInt(dateAndYear.date));
        }

        List<String> strAuthors = wrapper.getFieldValues("creator");
        publication.setAuthors(AuthorSupport.authorsFromStringList(strAuthors));
        publication.setPublisher(wrapper.getFieldValue("publisher"));

        if (!oaipmhRecord.issn().isEmpty()) {
            publication.setNormalizedIssn(oaipmhRecord.issn());
        }

        return publication;
    }

    // if there is a coar type, it's used, otherwise the first value is used
    private String preferredPublicationType(List<String> publicationTypes) {
        if (!publicationTypes.isEmpty()) {
            String result = publicationTypes.get(0);    // default: 1.Eintrag
            for (String type : publicationTypes) {
                String id = coarIdFromUrl(type);
                if (id != null) {
                    result = id;
                    break;
                }
            }
            return result;
        }
        return "";
    }

    public PublicationSource sourceFrom(RecordType recordType) {

        MetaDataWrapper wrapper = MetaDataWrapperFactory.createWrapper(recordType.getMetadata());

        PublicationSource source = new PublicationSource();
        source.setId(UUID.randomUUID().toString());

        if (wrapper instanceof ElementMetaDataWrapper) {
            return sourceFromOpenAire(source, recordType, (ElementMetaDataWrapper)wrapper);
        }

        String doi = dataExtractor.doi(wrapper);
        source.setDoi(doi);

        String nativeId = dataExtractor.identifier(wrapper);
        if (nativeId == null) {
            // if there is no identifier found in the data, we use the identifier from the header
            nativeId = recordType.getHeader().getIdentifier();
        }
        source.setNativeId(nativeId);

        source.setTitle(wrapper.getFieldValue("title"));
        String dateStr = wrapper.getFieldValue("date");
        DateAndYear dateAndYear = dateAndYearFrom(dateStr);
        source.setYear(dateAndYear.year);

        List<String> publicationTypes = wrapper.getFieldValues("type");
        source.setPubtype(preferredPublicationType(publicationTypes));

        OaipmhRecord record = wrapper.getData();
        record.setHeader(recordType.getHeader());
        source.setRecord(recordConverter.convertToDatabaseColumn(record));
        source.setDataType(oai_dc);
        return source;
    }

    /*
        <datacite:relatedIdentifiers>
            <datacite:relatedIdentifier relatedIdentifierType="ISSN" relationType="IsPartOf">0175-7598</datacite:relatedIdentifier>
            <datacite:relatedIdentifier relatedIdentifierType="ISSN" relationType="IsPartOf">1432-0614</datacite:relatedIdentifier>
            <datacite:relatedIdentifier relatedIdentifierType="DOI" relationType="IsVersionOf">10.1007/s00253-016-8045-z</datacite:relatedIdentifier>
        </datacite:relatedIdentifiers>

    	"relation": {
			"name": "relation",
			"values": [
				"info:eu-repo/semantics/altIdentifier/doi/10.1016/j.jsb.2020.107633",
				"info:eu-repo/semantics/altIdentifier/issn/1047-8477",
				"info:eu-repo/semantics/altIdentifier/wos/000600997800008",
				"info:eu-repo/grantAgreement/FWF//P33367"
			]
		}

     */
    private PublicationSource sourceFromOpenAire(PublicationSource source, RecordType recordType, ElementMetaDataWrapper wrapper) {

        String title = wrapper.getFieldValue("datacite:title"); // e.g. TU Wien
        if (title == null) {
            title = wrapper.getFieldValue("datacite:titles.datacite:title");    // e.g. FH Campus Wien
        }

        if (title == null) {
            // without title, we treat this record as invalid record
            return null;
        }

        source.setTitle(title);

        JsonPublication publication = new JsonPublication();
        publication.setTitle(title);

        String nativeId = dataExtractor.identifier(wrapper);
        if (nativeId == null) {
            // if we could not take an identifier from the data itself, we take it from the header
            nativeId = recordType.getHeader().getIdentifier();
        }
        source.setNativeId(nativeId);

        String dateStr = wrapper.getFieldValue("datacite:date");
        if (dateStr == null) {
            dateStr = wrapper.getFieldValue("datacite:dates.datacite:date");
        }
        publication.setPubdate(dateStr);
        DateAndYear dateAndYear = dateAndYearFrom(dateStr);
        source.setYear(dateAndYear.year);

        String doi = dataExtractor.doi(wrapper);
        source.setDoi(doi);
        if (!hasValue(doi)) {
            List<String> dois = wrapper.getFieldValues("datacite:relatedIdentifiers.datacite:relatedIdentifier[relatedIdentifierType=DOI]");
            if (dois.isEmpty()) {
                // e.g. Mozarteum
                dois = wrapper.getFieldValues("datacite:alternateIdentifiers.datacite:alternateIdentifier[alternateIdentifierType=DOI]");
            }
            if (dois.isEmpty()) {
                // sometimes, the DOI is sent with alternateIdentifierType=URL, e.g. Mozarteum
                dois = wrapper.getFieldValues("datacite:alternateIdentifiers.datacite:alternateIdentifier[alternateIdentifierType=URL]");
            }
            for (String doiCandidate : dois) {
                String doiResult = parseDoi(doiCandidate);
                if (hasValue(doiResult)) {
                    source.setDoi(doiResult);
                    break;
                }
            }
        }
        publication.setDoi(source.getDoi());

        List<String> publicationTypes = wrapper.getAttributeValues("oaire:resourceType", "uri");
        String pubType = preferredPublicationType(publicationTypes);
        source.setPubtype(pubType);
        publication.setType(pubType);

        String publisherName = wrapper.getFieldValue("dc:publisher");
        if (hasValue(publisherName)) {
            JsonPublisher jsonPublisher = new JsonPublisher();
            jsonPublisher.setName(publisherName);
            publication.setPublisher(jsonPublisher);
        }

        String citationTitle = wrapper.getFieldValue("oaire:citationTitle");
        List<String> issns = wrapper.getFieldValues("datacite:relatedIdentifiers.datacite:relatedIdentifier[relatedIdentifierType=ISSN]");
        if (citationTitle != null || !issns.isEmpty()) {
            JsonJournal jsonJournal = new JsonJournal();
            jsonJournal.setTitle(citationTitle);
            jsonJournal.setIssn(issns);
            publication.setJournal(jsonJournal);
        }

        List<ElementNode> authorNodes = wrapper.getNodesByPath("datacite:creators.datacite:creator");
        for (ElementNode authorNode : authorNodes) {
            JsonAuthor jsonAuthor = new JsonAuthor();
            jsonAuthor.setFamily(authorNode.getFirstChildContent("datacite:familyName"));
            jsonAuthor.setGiven(authorNode.getFirstChildContent("datacite:givenName"));
            if (!hasValue(jsonAuthor.getFamily()) ) {
                String name = authorNode.getFirstChildContent("datacite:creatorName");
                if (hasValue(name)) {
                   jsonAuthor = authorFromString(name);
                }
            }
            if (jsonAuthor != null && hasValue(jsonAuthor.getFamily())) {
                String affiliation = authorNode.getFirstChildContent("datacite:affiliation");
                if (hasValue(affiliation)) {
                    JsonAffiliation jsonAffiliation = new JsonAffiliation();
                    jsonAffiliation.setName(affiliation);
                    jsonAuthor.setAffiliation(jsonAffiliation);
                }
                publication.addAuthor(jsonAuthor);
            }
        }

        source.setRecord(jsonPublicationObjectConverter.convertToDatabaseColumn(publication));
        source.setDataType(json);
        return source;

    }

}

