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

import at.roesel.oadataprocessor.model.Publication;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PublicationRepositoryTest {

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    @Tag("manual")
    void findAllByDoi() {
        Iterable<Publication> result = publicationRepository.findAllByDoi("10.1016/j.shpsb.2017.04.004");
        assertNotNull(result);
    }

    @Test
    @Tag("manual")
    void findAllByPublisherId() {
        PageRequest pageRequest = PageRequest.of(0, 100);
        Page<Publication> pages = publicationRepository.findAllByPublisherId(pageRequest, "");
        assertNotNull(pages);
    }

    @Test
    @Tag("manual")
    void findAllEmptyJournalId() {
        PageRequest pageRequest = PageRequest.of(0, 100);
        Page<Publication> pages = publicationRepository.findAllEmptyJournalId(pageRequest);
        assertNotNull(pages);
    }

    @Test
    @Tag("manual")
    void findAllByInstitution() {
        Iterable<Publication> result = publicationRepository.findAllByInstitution("https://ror.org/03prydq77");
        assertNotNull(result);
    }

    @Test
    @Tag("manual")
    void findAllByTitleLike() {
        Iterable<Publication> result = publicationRepository.findAllByTitleLike("%Austria%");
        assertNotNull(result);
    }

    @Test
    @Tag("manual")
    void findAllByTitleHashTest() {
        Iterable<Publication> result = publicationRepository.findAllByTitleHash(-468279546);
        assertNotNull(result);
    }

    @Test
    @Tag("manual")
    void findAllByRecordSourceId() {
        Publication result = publicationRepository.findBySourceId("4382b434-fe23-4d26-803b-713c14d75bc3");
        assertNotNull(result);
    }

}
