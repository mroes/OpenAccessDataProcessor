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

package at.roesel.oadataprocessor.services.publisher;

import at.roesel.oadataprocessor.model.Journal;
import at.roesel.oadataprocessor.model.Publisher;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublisherIdentifierTest {

    @Test
    void identify() {
    }

    @Test
    void stripNameInParenthesis() {
        NameParts nameParts = NameParts.stripNameInParenthesis("Ovid Technologies");
        assertEquals("Ovid Technologies", nameParts.name);
        assertNull(nameParts.secondName);

        nameParts = NameParts.stripNameInParenthesis("Ovid Technologies (Wolters Kluwer Health)");
        assertEquals("Ovid Technologies", nameParts.name);
        assertEquals("Wolters Kluwer Health", nameParts.secondName);

        nameParts = NameParts.stripNameInParenthesis("Ovid Technologies (Wolters Kluwer Health");
        assertEquals("Ovid Technologies", nameParts.name);
        assertEquals("Wolters Kluwer Health", nameParts.secondName);

        nameParts = NameParts.stripNameInParenthesis("Ovid Technologies (Wolters Kluwer Health) end");
        assertEquals("Ovid Technologies end", nameParts.name);
        assertEquals("Wolters Kluwer Health", nameParts.secondName);
    }

    @Test
    void nameParts() {
        PublisherIdentifier identifier = new PublisherIdentifier(new PublisherSource() {
            @Override
            public Iterable<Publisher> readPublishers() {
                return Collections.emptyList();
            }

            @Override
            public List<Journal> readJournals() {
                return List.of();
            }

            @Override
            public Journal searchJournal(List<String> issns) {
                return null;
            }
        });

        List<String> nameParts = identifier.nameParts("Duncker & Humblot GmbH");
        assertEquals(nameParts.size(), 2);
        assertEquals(nameParts.get(0), "Duncker");
        assertEquals(nameParts.get(1), "Humblot");
    }
}
