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

package at.roesel.oadataprocessor.services.openalex;

import at.roesel.oadataprocessor.model.Author;
import at.roesel.oadataprocessor.model.openalex.OpenAlexAuthor;
import at.roesel.oadataprocessor.model.openalex.OpenAlexAuthorship;

public class OpenAlexSupport {

    public static Author authorFromOpenAlex(OpenAlexAuthorship authorship) {
        OpenAlexAuthor alexAuthor = authorship.author;
        String name = alexAuthor.display_name;
        String[] nameParts = name.split(" ");
        Author searchAuthor = new Author();
        if (nameParts.length == 2) {
            searchAuthor.setLastName(nameParts[1]);
            searchAuthor.setFirstName(nameParts[0]);
        } else if (nameParts.length > 2) {
            searchAuthor.setLastName(nameParts[nameParts.length-1]);
            searchAuthor.setFirstName(nameParts[0]);
        } else {
            searchAuthor.setLastName(name);
        }
        return searchAuthor;
    }
}
