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

package at.roesel.oadataprocessor.support;

import java.util.List;

import static at.roesel.oadataprocessor.support.DoiSupport.parseDoi;

public class DataExtractorDefault implements DataExtractor {

    protected String fieldDoi = "identifier";
    protected String fieldIdentifier = "identifier";
    protected String identifierPrefix = null;

    @Override
    public String doi(MetaDataWrapper wrapper) {
        List<String> identifiers = wrapper.getFieldValues(fieldDoi);
        if (identifiers != null) {
            for (String identifier : identifiers) {
                String doi = parseDoi(identifier);
                if (doi != null) {
                    return doi;
                }
            }
        }

        // search in source
        String source = wrapper.getFieldValue("source");
        if (source != null && !source.isEmpty()) {
            // doi:<a href="https://doi.org/10.1007/978-3-030-45237-7_5">10.1007/978-3-030-45237-7_5</a>
            int pos = source.indexOf("doi.org");
            if (pos > -1) {
                int posEnd = source.indexOf("\">");
                if (posEnd == -1) {
                    posEnd = source.length();
                }
                if (posEnd > pos+8) {
                    String doi = source.substring(pos + 8, posEnd);
                    return parseDoi(doi);
                }
            }
        }

        // search in relation
        List<String> relationFields = wrapper.getFieldValues("relation");
        if (relationFields != null) {
            // <dc:relation>https://doi.org/10.1016/j.ins.2019.08.081</dc:relation>
            for (String relation : relationFields) {
                String doi = parseDoi(relation);
                if (doi != null) {
                    return doi;
                }
            }
        }

        return null;
    }

    @Override
    public String identifier(MetaDataWrapper wrapper) {
        List<String> identifiers = wrapper.getFieldValues(fieldIdentifier);
        if (!identifiers.isEmpty()) {
            return findIdentifier(identifiers);
        }
        return null;
    }

    protected String findIdentifier(List<String> identifiers) {
        // take the shortest id if no other identifier is found
        String result = identifiers.get(0);
        for (String str : identifiers) {
            // use the identifier with the given prefix
            if (identifierPrefix != null && str.startsWith(identifierPrefix)) {
                return str;
            }
            if (str.length() < result.length()) {
                result = str;
            }
        }
        return result;
    }
}
