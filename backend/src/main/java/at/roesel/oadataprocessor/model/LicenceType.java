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

package at.roesel.oadataprocessor.model;

/*
2. License
Description: License type
Metadata field:
– Link to license
Evaluation classes:
1) Open License (CC BY or CC BY-SA, CC0 or comparable license
complying with the Open Definition4)
2) Free License (Other CC license or comparably licensed)
3) Proprietary licenses (e.g. Publisher specific)
4) No license / license unknown
 */
public enum LicenceType {
    OpenLicence(1),
    FreeLicence(2),
    ProprietaryLicence(3),
    UnknownLicence(4);

    public int code;

    LicenceType(int code) {
        this.code = code;
    }

    public boolean isBetter(LicenceType bestLicenceType) {
        if (bestLicenceType == null) {
            return true;
        }
        return code < bestLicenceType.code;
    }
}
