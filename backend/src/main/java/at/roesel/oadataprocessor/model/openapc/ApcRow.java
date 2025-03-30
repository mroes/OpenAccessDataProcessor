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

package at.roesel.oadataprocessor.model.openapc;

import at.roesel.oadataprocessor.model.CsvRow;

public class ApcRow extends CsvRow implements OpenApcRecord {
    /*
    String institution;
    String period;
    String euro;
    String doi;
    String is_hybrid;
    String publisher;
    String journal_full_title;
    String issn;
    String issn_print;
    String issn_electronic;
    String issn_l;
    String license_ref;
    String indexed_in_crossref;
    String pmid;
    String pmcid;
    String ut;
    String url;
    String doaj;

     */

    public ApcRow() {
        super();
    }

    public boolean isHybrid() {
        return getBooleanField(OpenApcFields.is_hybrid);
    }

    // in Cent
    public long getApcAmount() {
        long result = -1;
        String contents = getField(OpenApcFields.euro);
        if (contents == null || contents.isEmpty() || contents.equals("NA")) {
            return result;
        }
        int posDot = contents.indexOf(".");
        if (posDot == -1) {
            result = Long.parseLong(contents) * 100;
        } else {
            float amount = Float.parseFloat(contents);
            result = Math.round(amount*100);
        }

        return result;
    }

    @Override
    public boolean isTa() {
        String contents = getField(OpenApcFields.agreement);
        return contents != null && !contents.isEmpty();
    }

}
