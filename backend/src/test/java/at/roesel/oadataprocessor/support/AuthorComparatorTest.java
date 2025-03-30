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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorComparatorTest {

    @Test
    void compare() {
        AuthorComparator comparator = new AuthorComparator();

        Author author1 = createAuthor("Wolfgang", "Bauer");
        Author author2 = createAuthor("Wolfgang", "Müller");
        assertNotEquals(0, comparator.compare(author1, author2));

        Author author3 = createAuthor("Wolfgang", "Bauer");
        assertEquals(0, comparator.compare(author1, author3));

        Author author4 = createAuthor("W", "Bauer");
        assertEquals(0, comparator.compare(author1, author4));

        Author author5 = createAuthor("Werner", "Bauer");
        assertNotEquals(0, comparator.compare(author1, author5));
    }

    private Author createAuthor(String firstName, String lastName) {
        Author author = new Author();
        author.firstName = firstName;
        author.lastName = lastName;
        return author;
    }
}
