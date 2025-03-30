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

import at.roesel.common.PolynomialRollingHash;

public class TitleSupport {

    public static String normalizeTitle(String rawTitle) {
        String title = rawTitle.trim();
        // Replace line breaks with spaces
        title = title.replace("\r", " ");
        title = title.replace("\n", " ");
        // Collapse multiple spaces into a single space
        title = title.replaceAll(" +", " ");
        return title;
    }

    public static int calculateTitleHash(String title) {
        String str = core(title);
        // A hash is generated for the title to quickly check during the search if the title matches the publication.
        int hash = PolynomialRollingHash.hash(str);
        return hash;
    }

    // Converts the string to lowercase and removes all characters outside the code range 32-126
    private static String core(String value) {
        String result = value.toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (char ch : result.toCharArray()) {
            if (ch >= ' ' && ch <= '~') {
                sb.append(ch);
            }
        }
        if (sb.length() < 5) {
            // If the text consists, for example, of Chinese characters
            return result;
        }
        return sb.toString();
    }

}
