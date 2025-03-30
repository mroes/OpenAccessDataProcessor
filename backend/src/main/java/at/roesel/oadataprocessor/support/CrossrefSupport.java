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
import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.crossref.CrossrefAuthor;
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CrossrefSupport {

    private final static Logger logger = LoggerFactory.getLogger(CrossrefSupport.class);

    private final static int MIN_RATIO = 80;

    /*
     * @return: CrossrefWork with the best match, null if the match is not sufficient
     */
    public static CrossrefWork bestFit(Publication publication, List<CrossrefWork> works) {

        CrossrefWork bestWork = null;
        int bestRatio = 0;
        String titlePub = publication.getTitle().toLowerCase();
        for (CrossrefWork work : works) {
            String titleCR = work.firstTitle().toLowerCase();
            int ratio = FuzzySearch.ratio(titlePub, titleCR);
            if (ratio < MIN_RATIO) {
                continue;
            }
            // check publication year
            int pubYear = publication.getYear();
            if (pubYear != 0 && pubYear != work.publishedYear()) {
                // The year may differ due to errors
                logger.warn("different years: publication year = {}, crossref year = {}", pubYear, work.publishedYear());
            }
            boolean sameAuthors = sameAuthors(publication.getAuthors(), work.author);
            if (!sameAuthors) {
                continue;
            }
            if (ratio > bestRatio) {
                bestWork = work;
                bestRatio = ratio;
            }
        }

        return bestWork;
    }

    private static boolean sameAuthors(List<Author> authors, List<CrossrefAuthor> crAuthors) {
        if (authors == null && crAuthors == null) {
            return true;
        }
        if (authors == null) {
            authors = new ArrayList<>();
        }
        if (crAuthors == null) {
            crAuthors = new ArrayList<>();
        }
        if (authors.size() != crAuthors.size()) {
            return false;
        }
        for (CrossrefAuthor crAuthor : crAuthors) {
            boolean found = authors.stream().anyMatch(a -> crAuthor.familyName().equals(a.lastName));
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
