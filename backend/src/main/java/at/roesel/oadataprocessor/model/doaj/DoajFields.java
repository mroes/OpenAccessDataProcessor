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

package at.roesel.oadataprocessor.model.doaj;

public class DoajFields {
    public final static String url_in_doaj = "URL in DOAJ";
    public final static String journal_title = "Journal title";
    public final static String journal_alt_title = "Alternative title";
    public final static String journal_issn = "Journal ISSN (print version)";
    public final static String journal_eissn = "Journal EISSN (online version)";
    public final static String journal_licence = "Journal license";
    // Angabe, ob es eine APC gibt, boolean
    public final static String apc = "APC";
    // Betrag der APC, String mit Betrag und Währung
    public final static String apcAmount = "APC amount";
    public final static String publisher = "Publisher";
    public final static String oa_start = "When did the journal start to publish all content using an open license?";

}
