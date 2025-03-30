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
import at.roesel.oadataprocessor.model.LicenceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static at.roesel.oadataprocessor.model.Licences.getLicenceType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DoajBibjson {

    @JsonIgnore
    private static final long AMOUNT = 9999999L;

    public String title;
    @JsonProperty("alternative_title")
    public String alternativeTitle;

    @JsonProperty("pissn")
    public String issn;
    public String eissn;
    public List<DoajLicence> license;
    public DoajApc apc;

    @JsonProperty("oa_start")
    public int oaStart;

    public DoajPublisher publisher;

    public String bestLicence() {
        String result = null;
        if (license != null) {
            if (license.size() == 1) {
                return license.get(0).type;
            }
            // wenn es mehrere Lizenzen gibt, dann suchen wir die beste
            LicenceType bestLicenceType = LicenceType.UnknownLicence;
            for (DoajLicence doajLicence : license) {
                LicenceType doajLicenceType = getLicenceType(doajLicence.type);
                if (result == null || doajLicenceType.isBetter(bestLicenceType)) {
                    result = doajLicence.type;
                }
            }
        }
        return result;
    }

    public Amount bestAmount() {
        Amount amount = null;
        if (apc.hasApc) {
            if (apc.max != null && !apc.max.isEmpty()) {
                DoajAmount foundAmount = null;
                for (DoajAmount doajAmount : apc.max) {
                    if ("EUR".equals(doajAmount.currency)) {
                        foundAmount = doajAmount;
                        break;
                    }
                }
                // es gibt keinen EUR-Betrag, wir nehmen den ersten Betrag
                if (foundAmount == null) {
                    foundAmount = apc.max.get(0);
                }
                amount = new Amount();
                amount.value = foundAmount.price * 100;
                amount.currency = foundAmount.currency;
            }
            if (amount == null) {
                // es gibt einen APC, aber kein konkreter Betrag ist angegeben
                amount = new Amount(AMOUNT, "???");
            }
        } else {
            // APC = 0
            amount = new Amount();
        }
        return amount;
    }

    public String publisher() {
        if (publisher != null) {
            return publisher.name;
        }
        return null;
    }
}
