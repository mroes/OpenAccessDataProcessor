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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoiSupport {

    // https://support.datacite.org/docs/doi-basics
    // reserved Characters
    // ; | / | ? | : | @ | & | = | + | $ | ,
    private final static List<Character> reservedChars = Arrays.asList(';', '/', '?', ':', '@', '&', '=', '+', '$', ',');
    // Without spaces, because DOIs often mistakenly contain a space
    private final static List<Character> endChars = Arrays.asList(/*' ', */ ';', ',', '\n', '\r', '\t', '\"');

    /*
     * @return True if the DOI could be valid
     */
    public static boolean checkDoi(String doi) {
        if (doi == null || doi.isEmpty()) {
            return false;
        }

        // es sollte einen / geben
        int posSlash = doi.indexOf("/");
        if (posSlash == -1) {
            return false;
        }

        // The first part should have at least 6 characters
        if (posSlash < 7) {
            return false;
        }

        // The second part should have at least 1 character
        int length2ndPart = doi.length() - posSlash - 1;
        if (length2ndPart < 1) {
            return false;
        }

        if (!doi.startsWith("10.")) {
            return false;
        }

        return true;
    }

    private final static Pattern DOI_PATTERN = Pattern.compile("10\\.\\d{4,5}/.+", Pattern.DOTALL);
    public static String extractDoi(String identifier) {
        String doi = null;
        if (identifier != null && !identifier.isEmpty()) {
            // Decode potentially encoded slashes
            identifier = identifier.replace("%2F", "/");
            // Leerzeichen nach / entfernen
            identifier = identifier.replace("/ ", "/");

            if (identifier.startsWith("10.")) {
                // More precise validation
                Matcher matcher = DOI_PATTERN.matcher(identifier);
                if (matcher.matches()) {
                    doi = identifier;
                }
            } else if (identifier.startsWith("doi:") || identifier.startsWith("DOI:")) {
                doi = identifier.substring(4).trim();
            } else if (identifier.startsWith("info:doi:")) {
                doi = identifier.substring(9);
            } else if (identifier.startsWith("https://doi.org/")) {
                doi = identifier.substring(16);
            } else {
                // is there a string with "10." somewhere ?
                int pos = identifier.indexOf("10.");
                if (pos > -1) {
                    // More precise validation
                    identifier = identifier.substring(pos);
                    Matcher matcher = DOI_PATTERN.matcher(identifier);
                    if (matcher.matches()) {
                        doi = identifier;
                    }
                }
            }
        }

        // Trim any remaining leading or trailing spaces
        if (doi != null && !doi.isEmpty()) {
            doi = doi.trim();
        }

        return doi;
    }

    /*
     * Attempts to find a DOI in the identifier.
     * Since DOIs are often incorrectly specified, their validity is checked.
     */
    public static String parseDoi(String identifier) {
        String doi = identifier.trim();
        int count = 0;
        while (true) {
            count++;
            // Emergency brake: No more than 3 recursive calls to prevent an infinite loop.
            // @see DoiSupportTest.parseNonDoi()
            if (count > 3) {
                break;
            }
            doi = extractDoi(doi);
            if (doi != null && !doi.isEmpty()) {
                if (checkDoi(doi)) {
                    return trimDoi(doi);
                }
                // Possibly, a prefix appears again
            } else {
                break;
            }
        }

        return null;
    }

    // Additional information is often appended to the DOI in the same field.
    // We try to trim it here
    private static String trimDoi(String doi) {
        if (doi != null && !doi.isEmpty()) {
            doi = doi.trim();
            for (int i = 0; i < doi.length(); i++) {
                char ch = doi.charAt(i);
                if (endChars.contains(ch)) {
                    doi = doi.substring(0, i);
                    break;
                }
            }
        }
        return doi;
    }

    public static boolean containsReservedCharacter(String doi) {
        if (doi != null && !doi.isEmpty()) {
            doi = suffix(doi);
            for (int i = 0; i < doi.length(); i++) {
                char ch = doi.charAt(i);
                if (reservedChars.contains(ch)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String fileNameForDoi(String doi) {
        String result = doi.replace("/", "_");
        return result;
    }


    public static String prefix(String doi) {
        int pos = doi.indexOf("/");
        if (pos == -1) {
            return "";
        }

        return doi.substring(0, pos);
    }

    public static String suffix(String doi) {
        int pos = doi.indexOf("/");
        if (pos == -1) {
            return "";
        }

        return doi.substring(pos+1);
    }

}
