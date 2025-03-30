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

public class DataExtractorTuWien extends DataExtractorDefault {

    public DataExtractorTuWien() {
        fieldDoi = "datacite:identifier";
        fieldIdentifier = "datacite:identifier";
    }

    // auch https://resolver.obvsg.at/urn:nbn:at:at-ubtuw:1-30159
    // http://hdl.handle.net/20.500.12708/80239
    @Override
    protected String findIdentifier(List<String> identifiers) {
        String id = null;
        for (String identifier : identifiers) {
            int pos = identifier.indexOf("urn:");
            if (pos > -1) {
                id = identifier.substring(pos);
                break;
            }
        }
        if (id == null) {
            for (String identifier : identifiers) {
                int pos = identifier.indexOf("http://hdl.handle.net/");
                if (pos > -1) {
                    id = identifier.substring(pos);
                    break;
                }
            }
        }
        return id;
    }

}
