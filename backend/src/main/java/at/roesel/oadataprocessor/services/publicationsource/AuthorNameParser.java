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

package at.roesel.oadataprocessor.services.publicationsource;

import at.roesel.oadataprocessor.model.json.JsonAuthor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// von UniInnsbruckNameParser übernommen
public class AuthorNameParser {

    // names from Uni Innsbruck that are not real names
    private final static List<String> excludeNames = Arrays.asList(
            "et al",
            "...",  // z.B. in 10.1111/gcb.17046
            "\u0085" // z.B. in 10.1093/nsr/nwad138
    );

    public static List<JsonAuthor> authorsFromString(String text) {
        List<JsonAuthor> authors = new ArrayList<>();
        String[] names = text.split(";");
        for (String str : names) {
            JsonAuthor author = authorFromString(str.trim());
            if (author != null && !excludeNames.contains(author.getFamily())) {
                authors.add(author);
            }
        }
        return authors;
    }

    // von BokuNameParser
    public static JsonAuthor authorFromString(String name) {
        if (name.isEmpty()) {
            return null;
        }
        JsonAuthor author = new JsonAuthor();
        String[] nameParts = name.split(",");
        if (nameParts.length > 1) {
            author.setFamily(nameParts[0].trim());
            author.setGiven(nameParts[1].trim());
        } else {
            // sometimes the author is written in the form GivenName FamilyName and sometimes FamilyName GivenName
            // we assume the later case if the 2nd name part is short or abbreviated
            nameParts = name.split(" ");
            if (nameParts.length > 1) {
                String first = nameParts[0].trim();
                String second = nameParts[1].trim();
                if (second.length() < 3 || second.contains(".")) {
                    author.setFamily(first);
                    author.setGiven(second);
                } else {
                    author.setFamily(second);
                    author.setGiven(first);
                }
            } else {
                author.setFamily(nameParts[0].trim());
            }
        }
        return author;
    }


}
