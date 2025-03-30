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

import java.util.Comparator;

public class AuthorComparator implements Comparator<Author> {

    @Override
    public int compare(Author author1, Author author2) {
        if (author1.equals(author2)) {
            return 0;
        }
        int result = author1.lastName().compareToIgnoreCase(author2.lastName());
        if (result == 0) {
            String firstName1 = author1.firstName();
            String firstName2 = author2.firstName();
            result = firstName1.compareToIgnoreCase(firstName2);
            if (result != 0) {
                if (!firstName1.isEmpty() && !firstName2.isEmpty()
                && (isInitial(firstName1) || isInitial(firstName2)) ) {
                    Character ch1 = firstName1.charAt(0);
                    Character ch2 = firstName2.charAt(0);
                    result = ch1.compareTo(ch2);
                }
            }
        }
        return result;
    }

    private boolean isInitial(String name) {
        return name.contains(".") || name.length() < 2;
    }
}
