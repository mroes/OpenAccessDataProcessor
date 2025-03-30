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

import at.roesel.oadataprocessor.model.oaipmh.jabx.*;
import at.roesel.oadataprocessor.support.MetaDataWrapper;
import at.roesel.oadataprocessor.support.MetaDataWrapperFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static at.roesel.oadataprocessor.services.oaipmh.OaiPmhImporter.mdf_cerif;
import static at.roesel.oadataprocessor.services.oaipmh.OaiPmhImporter.mdf_oai_dc;
import static at.roesel.oadataprocessor.support.MetaDataWrapperFactory.createWrapper;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OaiPmhServiceImplTest {

    private final static String urlJournalsWu = "https://openjournals.wu.ac.at/ojs/index.php/region/oai";
    private final static String urlUniWien = "https://ucris.univie.ac.at/ws/oai";

    @Autowired
    private OaiPmhService service;

    @Test
    @Tag("manual")
    public void identify_test() {
        OaiPmhImporter importer = service.createOaiPmhImporter(urlUniWien);
        IdentifyType response = importer.identify();
        assertEquals(urlUniWien, response.getBaseURL());
    }

    @Test
    @Tag("manual")
    public void listFirstRecords_test() {
        LocalDate from = LocalDate.of(2022, 1, 1);
        OaiPmhImporter importer = service.createOaiPmhImporter(urlUniWien);
        List<RecordType> records = importer.listFirstRecords(from, mdf_cerif, null);
        assertTrue(records.size() > 0);
    }

    @Test
    @Tag("manual")
    public void listIdentifiers_test() {
        LocalDate from = LocalDate.of(2021, 9, 1);
        OaiPmhImporter importer = service.createOaiPmhImporter(urlUniWien);
        List<HeaderType> headers = importer.listIdentifiers(from);
        assertTrue(headers.size() > 0);
    }

    @Test
    @Tag("manual")
    public void listRecords_test() throws IOException {
        LocalDate from = LocalDate.of(2021, 10, 1);
        OaiPmhImporter importer = service.createOaiPmhImporter(urlUniWien);
        List<RecordType> records = importer.listRecords(from, mdf_oai_dc, null);
        assertTrue(!records.isEmpty());
        List<String> headers = null;
        List<List<String>> rows = new ArrayList<>();
        for (RecordType recordType : records) {
            try {
                MetaDataWrapper wrapper = MetaDataWrapperFactory.createWrapper(recordType.getMetadata());
                if (headers == null) {
                    headers = wrapper.getFieldNames();
                }
                List<String> row = new ArrayList<>();
                for (String header : headers) {
                    List<String> values = wrapper.getFieldValues(header);
                    if (values != null) {
                        row.add(listToString(values));
                    } else {
                        row.add("");
                    }
                }
                rows.add(row);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String listToString(List<String> values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            result.append(value);
            result.append(" - ");
        }
        result.delete(result.length()-3, result.length());
        return result.toString();
    }

    @Test
    @Tag("manual")
    public void listMetadataFormats_test() {
        OaiPmhImporter importer = service.createOaiPmhImporter(urlJournalsWu);
        String identifier = "oai:ojs.openjournals.wu.ac.at:article/9";
        List<MetadataFormatType> formats = importer.listMetadataFormats(identifier);
        assertTrue(!formats.isEmpty());
    }

    @Test
    @Tag("manual")
    void listSets_test() {
        OaiPmhImporter importer = service.createOaiPmhImporter(urlJournalsWu);
        List<SetType> sets = importer.listSets();
    }

    @Test
    @Tag("manual")
    public void getRecord_test() {
        OaiPmhImporter importer = service.createOaiPmhImporter(urlJournalsWu);
        String identifier = "oai:ojs.openjournals.wu.ac.at:article/9";
        RecordType record = importer.getRecord(identifier, mdf_oai_dc);
        assertNotNull(record);
        MetaDataWrapper wrapper = createWrapper(record.getMetadata());
        assertEquals("Editorial: REGION - the online open-access journal of ERSA", wrapper.getFieldValue("title"));
    }

}
