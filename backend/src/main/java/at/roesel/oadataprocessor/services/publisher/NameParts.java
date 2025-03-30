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

package at.roesel.oadataprocessor.services.publisher;

import java.util.List;

public class NameParts {
    public String name;
    public String secondName;  // oder Abkürzung
    public List<String> nameParts;

    public String firstPart() {
        if (nameParts != null && !nameParts.isEmpty()) {
            return nameParts.get(0);
        }

        return null;
    }

    public static NameParts stripNameInParenthesis(String publisherName) {
        NameParts nameParts = new NameParts();
        int posParStart = publisherName.indexOf("(");
        if (posParStart > -1) {
            int posParEnd = publisherName.indexOf(")");
            if (posParEnd == -1) {
                posParEnd = publisherName.length();
            }
            // ohne Klammern
            int start = posParStart + 1;
            int end = posParEnd;
            if (start < publisherName.length() &&
                    end >= 0 &&
                    start < end) {
                nameParts.secondName = publisherName.substring(start, end);
            }
            nameParts.name = "";
            if (posParStart - 1 >= 0) {
                nameParts.name += publisherName.substring(0, posParStart - 1);
            }
            if (posParEnd + 1 < publisherName.length()) {
                nameParts.name += publisherName.substring(posParEnd + 1);
            }
            nameParts.name = nameParts.name.trim();
        } else {
            nameParts.name = publisherName;
        }
        return nameParts;
    }

}
