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

package at.roesel.oadataprocessor.model.openapc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 Wrapper für die CSV-Rohdaten
 */
public class OpenApcData {
    //
    private final List<ApcRow> csvRows;

    public OpenApcData(List<ApcRow> csvRows) {
        if (csvRows == null) {
            csvRows = new ArrayList<>();
        }
        this.csvRows = csvRows;
    }

    public Optional<ApcRow> searchByDoi(String doi) {
        return csvRows.stream().filter(row -> doi.equals(row.getField(OpenApcFields.doi))).findFirst();
    }

    /*
    public Optional<ApcRow> search(Predicate<ApcRow> searchFilter) {
        return csvRows.stream().filter(searchFilter).findFirst();
    }

    public static class IssnFilter implements Predicate<DoajRow> {

        private final String issn;

        public IssnFilter(String issn) {
            this.issn = issn;
        }

        @Override
        public boolean test(DoajRow row) {
            return row.fieldContains(DoajFields.journal_issn, issn) ||
                    row.fieldContains(DoajFields.journal_eissn, issn);
        }

    }
    */
}
