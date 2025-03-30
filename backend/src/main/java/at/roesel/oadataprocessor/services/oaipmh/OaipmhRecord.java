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
import at.roesel.oadataprocessor.model.PublicationSourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static at.roesel.oadataprocessor.services.oaipmh.OaiPmhSupport.dateAndYearFrom;

public class OaipmhRecord implements PublicationSourceRecord {

    private final Logger logger = LoggerFactory.getLogger(OaipmhRecord.class);

    private final static String identifierField = "identifier";

    private final static String publisherField = "publisher";
    private final static String publicationTypeField = "type";
    private final static String yearField = "date";
    private final static String relationField = "relation";
    private final static String ISSN_PREFIX = "info:eu-repo/semantics/altIdentifier/issn/";
    private Object header;
    private Object data;

    private boolean affiliated;

    public Object getHeader() {
        return header;
    }

    public void setHeader(Object header) {
        this.header = header;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String publisher() {
        List<String> values = fieldValues(publisherField);
        if (values.size() > 1) {
            logger.warn("OaipmhRecord: multiple publishers {}", values);
        }
        if (!values.isEmpty()) {
            return values.get(0);
        }
        return "";
    }

    @Override
    public String publicationType() {
        List<String> values = fieldValues(publicationTypeField);
        StringBuilder sb = new StringBuilder();
        values.forEach( pubtype -> {
            if (!sb.isEmpty()) {
                sb.append("\t");
            }
            sb.append(pubtype);
        });
        return sb.toString();
    }

    private List<String> fieldValues(String field) {
        if (data instanceof Map) {
            Map<String, Object> map = (Map<String, Object>)((Map<String, Object>)data).get(field);
            if (map != null) {
                List<String> values = (List<String>) map.get("values");
                if (values != null) {
                    return values;
                }
            }
        }
        return Collections.emptyList();
    }

    /*
    ISTA: ISSN stored in realtion with ISSN_PREFIX
    TU-Wien
    		"identifier": {
			"name": "identifier",
			"values": [
				"https://resolver.obvsg.at/urn:nbn:at:at-ubtuw:3-9901",
				"http://hdl.handle.net/20.500.12708/856",
				"urn:nbn:at:at-ubtuw:3-9901",
				"1386-1425"
			]
		},
     */


    private final static Pattern ISSN_PATTERN = Pattern.compile("\\d{4}-\\d{3}[\\dXx]");

    @Override
    public List<String> issn() {
        List<String> relations = fieldValues(relationField);
        if (!relations.isEmpty()) {
            List<String> issns = new ArrayList<>();
            for (String relation : relations) {
                if (relation.startsWith(ISSN_PREFIX)) {
                    String issn = relation.substring(ISSN_PREFIX.length());
                    issns.add(issn);
                }
            }
            return issns;
        }
        List<String> identifiers = fieldValues(identifierField);
        if (!identifiers.isEmpty()) {
            List<String> issns = new ArrayList<>();
            for (String identifier : identifiers) {
                if (identifier.length() == 9) {
                    Matcher matcher = ISSN_PATTERN.matcher(identifier);
                    if (matcher.matches()) {
                        issns.add(identifier);
                    }

                }
            }
            return issns;
        }
        return Collections.emptyList();
    }

    @Override
    public int year() {
        List<String> values = fieldValues(yearField);
        if (!values.isEmpty()) {
            DateAndYear dateAndYear = dateAndYearFrom(values.get(0));
            return dateAndYear.year;
        }
        return 0;
    }

    @Override
    public boolean isAffiliated() {
        return affiliated;
    }

    public void setAffiliated(boolean affiliated) {
        this.affiliated = affiliated;
    }
}
