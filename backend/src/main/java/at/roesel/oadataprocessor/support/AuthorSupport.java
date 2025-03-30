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

import at.roesel.oadataprocessor.model.Author;

import java.util.ArrayList;
import java.util.List;

import static at.roesel.common.StringSupport.hasValue;

public class AuthorSupport {

    public static List<Author> authorsFromStringList(List<String> strAuthors) {
        List<Author> authors = new ArrayList<>();
        for (String str : strAuthors) {
            authors.add(authorFromString(str));
        }
        return authors;
    }

    public static Author authorFromString(String str) {
        Author author = new Author();
        int posSemicolon = str.indexOf(';');
        // Optionally, an ORCID could appear after a `;`
        if (posSemicolon > -1) {
            String orcid = str.substring(posSemicolon+1);
            author.setOrcid(prepareOrcid(orcid));
            str = str.substring(0, posSemicolon-1);
        }
        int posKomma = str.indexOf(',');
        if (posKomma == -1) {
            author.lastName = str;
        } else {
            author.lastName = str.substring(0, posKomma).trim();
            author.firstName = str.substring(posKomma + 1).trim();
        }
        return author;
    }

    private static String prepareOrcid(String orcid) {
        if (!hasValue(orcid)) {
            return "";
        }
        orcid = orcid.trim();
        if (orcid.length() > 6 && orcid.substring(0, 6).equalsIgnoreCase("orcid:")) {
            orcid = orcid.substring(6);
        }
        return orcid;
    }

    public static boolean containsInitial(String firstName) {
        if (!hasValue(firstName)) {
            return false;
        }
        // If the first name consists of multiple parts, check each part separately.
        String[] parts = firstName.split(" ");
        for (String part : parts) {
            boolean initial = part.contains(".") || part.length() < 3;
            if (initial) {
                return true;
            }
        }
        return false;
    }

    public static String initialFromName(String firstName) {
        if (firstName.isEmpty()) {
            return "";
        }
        String name = firstName.replace(" ", ".")
                .replace("-", ".");
        String[] parts = name.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (part.length() < 3) {
                    sb.append(part);
                } else {
                    sb.append(part.charAt(0));
                }
            }
        }
        return sb.toString();
    }

}
