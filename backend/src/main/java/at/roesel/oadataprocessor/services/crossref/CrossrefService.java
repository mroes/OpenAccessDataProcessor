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

package at.roesel.oadataprocessor.services.crossref;

import at.roesel.oadataprocessor.model.crossref.CrossrefPrefix;
import at.roesel.oadataprocessor.model.crossref.CrossrefSource;
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;

/*
    Crossref Unified Resource API Documentation
    https://api.crossref.org/swagger-ui/index.html
 */
public interface CrossrefService {
    /*
     fetch from Crossref
     */
    CrossrefWork crossrefWork(String doi);

    CrossrefPrefix crossrefPrefix(String prefix);

    CrossrefDoiSearchResult searchWork(String author, String title);

    CrossrefSource readCrossrefSourcefromDB(String doi);

    void deleteCrossrefSourcefromDB(String doi);
    /*
     read from database or fetch from Crossref
     */
    CrossrefWork getCrossrefWork(String doi);

}
