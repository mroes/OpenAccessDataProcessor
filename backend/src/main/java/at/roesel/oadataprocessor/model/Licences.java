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

import java.util.Arrays;
import java.util.List;

public class Licences {

    public final static List<String> licenceOpen = Arrays.asList(
            "CC0",
            "CC BY",
            "CC BY-SA",
            "PD"
    );
    public final static List<String> licenceFree = Arrays.asList(
            "CC BY-NC",
            "CC BY-NC-SA",
            "CC BY-ND",
            "CC BY-ND-NC",
            "CC BY-NC-ND"
    );

    public static LicenceType getLicenceType(String licence) {

        if (licence == null || licence.isEmpty()) {
            return LicenceType.UnknownLicence;
        }

        String normalizedLicence = buildNormalizedLicence(licence).getNormalizedLicence();

        if (licenceOpen.contains(normalizedLicence)) {
            return LicenceType.OpenLicence;
        }

        if (licenceFree.contains(normalizedLicence)) {
            return LicenceType.FreeLicence;
        }

        return LicenceType.ProprietaryLicence;
    }


    static String standardizeCreativeCommonsName(String name) {

        if (name != null && name.length() > 1) {
            StringBuilder sb = new StringBuilder(name.toUpperCase());
            if (!sb.substring(0,2).equals("CC")) {
                sb.insert(0, "CC ");
            } else if (sb.length() > 3 && sb.charAt(2) == '-') {
                sb.setCharAt(2, ' ');
            }

            return sb.toString();
        }
        return "";
    }

    public static NormalizedLicence buildNormalizedLicence(String licence) {
        String normalizedLicence;
        String version;

        NormalizedLicence result;

        if (licence == null || licence.isEmpty()) {
            return new NormalizedLicence("none", "");
        }

        licence = licence.toLowerCase();

        // In DOAJ, e.g., 86672aafcc434cee81859ba99f0bdfc1, it happens that multiple licenses are specified
        String[] multLicences = licence.split(",");
        if (multLicences.length > 1) {
            // wir nehmen die erste Lizenz
            licence = multLicences[0];
        }

        // Direct license specification
        if (licence.startsWith("cc")) {
            normalizedLicence = standardizeCreativeCommonsName(licence);
            version = "";
            return new NormalizedLicence(normalizedLicence, version);
        }

        // creativecommons.org/licenses
        int pos = licence.indexOf("creativecommons.org/licenses/");
        if (pos > -1) {
            int slashPos = licence.indexOf("/", pos+29);
            if (slashPos == -1) {
                slashPos = licence.length();
                version = "";
            } else {
                int endPos = licence.indexOf("/", slashPos+1);
                if (endPos == -1) {
                    endPos = licence.length();
                }
                version = licence.substring(slashPos+1, endPos);
            }
            normalizedLicence = standardizeCreativeCommonsName(licence.substring(pos+29, slashPos));
            return new NormalizedLicence(normalizedLicence, version);
        }

        if (licence.equals("pd") || licence.contains("public domain") || licence.contains("publicdomain")) {
            return new NormalizedLicence("PD", "");
        }

        pos = licence.indexOf("authorchoice_");
        if (pos > -1) {
            int endPos = licence.indexOf("_", pos+13);
            if (endPos == -1) {
                endPos = licence.length();
            }
            String term = licence.substring(pos+13, endPos);
            if (term.startsWith("cc")) {
                normalizedLicence = standardizeCreativeCommonsName(adjustStyle(term));
                return new NormalizedLicence(normalizedLicence, "");
            }
        }

        // https://doi.org/10.1364/OA_License_v1#CCBY
        pos = licence.indexOf("#cc");
        if (pos > -1) {
            String term = licence.substring(pos + 1);
            normalizedLicence = standardizeCreativeCommonsName(adjustStyle(term));
            return new NormalizedLicence(normalizedLicence, "");
        }

        return new NormalizedLicence("other", "");
    }

    // Space after CC, hyphen between the additional details
    static String adjustStyle(String term) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < term.length()-1; i = i+2) {
            if (sb.length() > 2) {
                sb.append("-");
            } else if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(term, i, i+2);
        }
        return sb.toString();
    }
}
