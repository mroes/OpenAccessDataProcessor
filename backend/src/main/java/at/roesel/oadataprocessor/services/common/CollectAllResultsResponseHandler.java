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

package at.roesel.oadataprocessor.services.common;

import java.util.ArrayList;
import java.util.List;

public class CollectAllResultsResponseHandler<T> implements ResultResponseHandler<T> {

    private final List<T> results = new ArrayList<>();

    // If true, handleResponse returns true. This ends the query after the first page.
    private boolean firstPageOnly = false;

    public boolean isFirstPageOnly() {
        return firstPageOnly;
    }

    public void setFirstPageOnly(boolean firstPageOnly) {
        this.firstPageOnly = firstPageOnly;
    }

    @Override
    public boolean handleResponse(List<T> records) {
        results.addAll(records);
        return firstPageOnly;
    }

    @Override
    public void onFinished() {

    }

    public List<T> getResults() {
        return results;
    }


}
