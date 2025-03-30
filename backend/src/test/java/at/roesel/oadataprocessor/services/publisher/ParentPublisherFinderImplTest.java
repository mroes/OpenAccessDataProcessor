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

import at.roesel.common.DateSupport;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.PublisherVar;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ParentPublisherFinderImplTest {

    @Test
    void findParent() {
        ParentPublisherFinder finder = new ParentPublisherFinderImpl();

        String publisherId = "100";
        Publisher publisher = new Publisher(publisherId, "ChildPublisher");
        assertNull(finder.findParent(publisher, 2022));

        publisher.setParentId("200");
        assertEquals("200", finder.findParent(publisher, 2022));
    }

    @Test
    void findParent1() {
        ParentPublisherFinder finder = new ParentPublisherFinderImpl();

        String publisherId = "100";
        Publisher publisher = new Publisher(publisherId, "ChildPublisher");

        PublisherVar var = new PublisherVar();
        var.setParentId("300");
        var.setStartDate(DateSupport.MIN_DATE);
        var.setEndDate(DateSupport.MAX_DATE);
        publisher.addPublisherVar(var);
        assertEquals("300", finder.findParent(publisher, 2022));

    }

    @Test
    void findParent2() {
        ParentPublisherFinder finder = new ParentPublisherFinderImpl();

        String publisherId = "100";
        Publisher publisher = new Publisher(publisherId, "ChildPublisher");

        PublisherVar var = new PublisherVar();
        var.setParentId("300");
        var.setStartDate(LocalDate.of(2000, 1, 1));
        var.setEndDate(LocalDate.of (2017, 10, 31));
        publisher.addPublisherVar(var);

        PublisherVar var2 = new PublisherVar();
        var2.setParentId("400");
        var2.setStartDate(LocalDate.of(2017, 11, 1));
        var2.setEndDate(DateSupport.MAX_DATE);
        publisher.addPublisherVar(var2);
        assertEquals("400", finder.findParent(publisher, 2022));

    }
}
