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

package at.roesel.oadataprocessor.persistance;

import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.PublisherVar;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PublisherRepositoryTest {

    @Autowired
    private PublisherRepository publisherRepository;

    @Test
    @Tag("manual")
    void findByNameLike() {
        Publisher publisher = publisherRepository.findByNameLike("%Thor%");
        assertNotNull(publisher);
    }

    @Test
    @Tag("manual")
    void create() {
        Publisher publisher = new Publisher();
        publisher.setId("_test");
        PublisherVar var1 = new PublisherVar();
        var1.setWikiParentId("_w1");
        publisher.addPublisherVar(var1);
        PublisherVar var2 = new PublisherVar();
        var2.setWikiParentId("_w2");
        publisher.addPublisherVar(var2);
        publisherRepository.save(publisher);
        assertNotNull(publisher);

        publisher.getVariable().remove(var1);
        publisherRepository.save(publisher);

        publisher.getVariable().clear();

        publisherRepository.save(publisher);

        publisherRepository.delete(publisher);
    }

}
