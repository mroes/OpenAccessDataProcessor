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

package at.roesel.oadataprocessor.services.publicationsource;

import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.json.JsonAuthor;
import at.roesel.oadataprocessor.model.json.JsonJournal;
import at.roesel.oadataprocessor.model.json.JsonPublication;
import at.roesel.oadataprocessor.model.json.JsonPublisher;
import at.roesel.oadataprocessor.services.common.FieldAccessor;
import at.roesel.oadataprocessor.support.DoiSupport;

import java.util.ArrayList;
import java.util.List;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.services.publicationsource.AuthorNameParser.authorsFromString;
import static at.roesel.oadataprocessor.support.CsvSupport.yearFromString;
import static at.roesel.oadataprocessor.support.IssnSupport.normalizeIssn;

/*
 * import publications from default excel format
 * */
public class DefaultPublicationRowHandler extends JsonPublicationImportRowHandler {

    public DefaultPublicationRowHandler(String institution, PublicationSourceService publicationSourceService) {
        super(institution, publicationSourceService);
    }

    @Override
    public boolean mapRecordToSource(FieldAccessor record, PublicationSource source, JsonPublication publication) {

        source.setNativeId(record.getString("id"));
        if (!hasValue(source.getNativeId())) {
            return false;
        }

        String doi = DoiSupport.parseDoi(record.getString("doi"));
        source.setDoi(doi);
        source.setTitle(record.getString("title"));
        String pubType = record.getString("type");
        source.setPubtype(pubType);
        publication.setType(pubType);
        int year;
        try {
            year = record.getInteger("publicationyear");
        } catch (Exception e) {
            year = yearFromString(record.getString("publicationyear"));
        }
        source.setYear(year);
        publication.setYear(year);

        String authorText = record.getString("author");
        publication.setRawAuthors(authorText);
        List<JsonAuthor> authors = parseAuthors(authorText);
        for (JsonAuthor author : authors) {
            publication.addAuthor(author);
        }

        String publisherName = record.getString("publishername");
        if (hasValue(publisherName)) {
            JsonPublisher publisher = new JsonPublisher();
            publication.setPublisher(publisher);
            publisher.setName(publisherName);
        }

        String journalName = record.getString("journalname");
        List<String> issns = new ArrayList<>();
        addIssn(record.getString("issn"), issns);
        addIssn(record.getString("eissn"), issns);
        addIssn(record.getString("issn-l"), issns);

        if (hasValue(journalName) || !issns.isEmpty()) {
            JsonJournal journal = new JsonJournal();
            publication.setJournal(journal);
            journal.setTitle(journalName);
            journal.setIssn(issns);
        }

        return true;
    }

    protected List<JsonAuthor> parseAuthors(String authorText) {
        List<JsonAuthor> authors = authorsFromString(authorText);
        return authors;
    }

    private void addIssn(String issn, List<String> issns) {
        if (hasValue(issn)) {
            issn = normalizeIssn(issn);
            if (!issn.isEmpty()) {
                issns.add(issn);
            }
        }
    }

}

