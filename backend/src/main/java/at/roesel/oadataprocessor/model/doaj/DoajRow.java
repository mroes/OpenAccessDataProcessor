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
import at.roesel.oadataprocessor.model.CsvRow;

public class DoajRow extends CsvRow implements DoajJournal {

    public String getLicence() {
        return getField(DoajFields.journal_licence);
    }

    public Amount getApcAmount() {
        String content = getField(DoajFields.apcAmount);
        Amount amount = parseAmountField(content);
        if (amount == null) {
            amount = new Amount();
            if (getBooleanField(DoajFields.apc)) {
                // es gibt einen APC, aber es ist kein Betrag eingetragen
                amount.value = -1;
            } else {
                amount.value = 0;
            }
        }
        return amount;
    }

    @Override
    public String getTitle() {
        return getField(DoajFields.journal_title);
    }

    @Override
    public String getIssn() {
        return getField(DoajFields.journal_issn);
    }

    @Override
    public String getEissn() {
        return getField(DoajFields.journal_eissn);
    }

    @Override
    public int getOaStart() {
        return 0;
    }

    @Override
    public int getStartDate() {
        return 0;
    }

    @Override
    public int getEndDate() {
        return DoajJournal.MAX_DATE;
    }

    // 2000 EUR; 1700 GBP; 2200 USD
    private Amount parseAmountField(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        String[] amounts = content.split(";");
        if (amounts.length == 1) {
            return parseAmount(amounts[0]);
        }
        Amount result = null;
        for (String amount : amounts) {
            Amount am = parseAmount(amount.trim());
            if (am != null) {
                if (result == null || "EUR".equals(am.currency)) {
                    result = am;
                }
            }
        }
        return result;
    }


    private Amount parseAmount(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        Amount amount = new Amount();
        String valuePart;
        int posBlank = content.indexOf(' ');
        if (posBlank > -1) {
            valuePart = content.substring(0, posBlank);
            amount.currency = content.substring(posBlank+1);
        } else {
            valuePart = content;
            amount.currency = "";
        }
        long value = Long.parseLong(valuePart) * 100;
        amount.value = value;
        return amount;
    }
}
