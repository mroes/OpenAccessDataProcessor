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
import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.PublicationType;
import at.roesel.oadataprocessor.services.common.PublicationTypeMapper;
import at.roesel.oadataprocessor.services.publicationsource.PublicationCreator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PublicationSourceRepositoryTest {

    @Autowired
    private PublicationSourceRepository publicationSourceRepository;

    @Test
    @Tag("manual")
    void findSourceByPublicationId() {
        List<PublicationSource> sources = publicationSourceRepository.findSourceByPublicationId("56e8c63d-dd7c-4d7e-a61d-85141bfa2442");
        assertTrue(sources.size() > 0);
    }

    @Test
    @Tag("manual")
    void findSourceById() {
        Optional<PublicationSource> source = publicationSourceRepository.findById("2e358232-81b7-4eea-bdb2-91c9dd17f902");
        assertTrue(source.isPresent());

        PublicationCreator publicationCreator = new PublicationCreator(new PublicationTypeMapper() {
            @Override
            public PublicationType mapType(String institutionId, String rawPublicationType) {
                return null;
            }
        });
        Publication publication = publicationCreator.from(source.get());
        assertNotNull(publication);
    }


}
