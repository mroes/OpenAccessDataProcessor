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

public class IdentifierSupport {

    private final static String coarUrl = "http://purl.org/coar/resource_type/";
    private final static String coarUrl2 = "vocabularies.coar-repositories.org/resource_types/";
    public final static String coarPrefix = "coar:";

    public static String extractIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        // remove a slash at the very end
        int startIndex = url.length() - 1;
        if (url.charAt(startIndex) == '/') {
            startIndex--;
        }
        int posSlash = url.lastIndexOf("/", startIndex);
        if (posSlash > -1) {
            return url.substring(posSlash+1, startIndex+1);
        }
        return url;
    }

    /*
     * If the URL is a coarUrl, the ID in the URL is returned; otherwise, null
     */
    public static String coarIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String urlLC = url.toLowerCase();
        if (urlLC.startsWith(coarUrl)) {
            return coarPrefix + extractIdFromUrl(url);
        }
        if (urlLC.contains(coarUrl2)) {
            return coarPrefix + extractIdFromUrl(url);
        }
        return null;
    }
}
