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

import at.roesel.oadataprocessor.model.Amount;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static at.roesel.common.DateSupport.fromISO9601;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DoajPayload {
    public String id;
    public String last_updated;
    public DoajBibjson bibjson;

    public DoajJournalEntity buildJournal() {
        DoajJournalEntity journal = new DoajJournalEntity();
        journal.setId(id);
        journal.setUpdated(fromISO9601(last_updated).toEpochMilli());
        if (bibjson != null) {
            journal.setIssn(bibjson.issn);
            journal.setEissn(bibjson.eissn);
            journal.setTitle(bibjson.title);
            journal.setAlternativeTitle(bibjson.alternativeTitle);
            journal.setLicence(bibjson.bestLicence());
            Amount amount = bibjson.bestAmount();
            if (amount != null) {
                journal.setApc_amount(amount.value);
                journal.setApc_currency(amount.currency);
            } else {
                journal.setApc_amount(-2);
                journal.setApc_currency("");
            }
            journal.setOaStart(bibjson.oaStart);
            journal.setPublisher(bibjson.publisher());
        }
        return journal;
    }

}
