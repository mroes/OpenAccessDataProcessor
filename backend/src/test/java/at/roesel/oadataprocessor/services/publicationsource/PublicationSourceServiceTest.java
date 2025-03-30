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

package at.roesel.oadataprocessor.services.publicationsource;

import at.roesel.oadataprocessor.model.Institution;
import at.roesel.oadataprocessor.services.InstitutionService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static at.roesel.oadataprocessor.config.Identifiers.*;

@SpringBootTest
@Disabled
class PublicationSourceServiceTest {

    @Autowired
    private PublicationSourceService publicationSourceService;

    @Autowired
    private InstitutionService institutionService;

    @Test
    @Tag("manual")
    @Disabled
    void fetchPublications() {
        LocalDate from = LocalDate.of(2022, 1, 1);
        publicationSourceService.fetchOaiPmh(idMoz, "https://repository.moz.ac.at/oai", from);
    }

    @Test
    @Tag("manual")
    @Disabled
    void fetchOpenAlex() {
        publicationSourceService.fetchOpenAlexPublications();
    }

    @Test
    @Tag("manual")
    @Disabled
    void testFetchFromPure() {
        String institutionId = idTuGraz;
        LocalDate from = LocalDate.of(2022, 1, 1);
        Institution institution = institutionService.findById(institutionId);
        String proxyConfig = "pure.tugraz.at:localhost:3128";
        publicationSourceService.fetchFromPure(institutionId, institution.getRepositoryUrl(), institution.getRepositoryKey(), proxyConfig, from);
    }

    @Test
    @Tag("manual")
    @Disabled
    void testFetchFromTuWien() {
        String apiUrlTuWien = "https://repositum.tuwien.at/oai/openaire";
        publicationSourceService.fetchTuWien(apiUrlTuWien);
    }

    @Test
    @Tag("manual")
    void updatePublications() {
        publicationSourceService.updatePublications(null);
    }

    @Test
    @Tag("manual")
    @Disabled
    void fetchFromActiveInstitutions() {
        publicationSourceService.fetchFromActiveInstitutions();
    }

    @Test
    @Tag("manual")
    @Disabled
    void fetchFromInstitutions() {
        List<Institution> institutions = Arrays.asList(institutionService.findById("https://ror.org/00t6gnv18"));
        publicationSourceService.fetchFromInstitutions(institutions);
    }

    @Test
    @Tag("manual")
    void fetchPublicationsFromExcel() {
        String path = "Publications.xlsx";
        publicationSourceService.fetchFromExcel(idMedUniGraz, path, 1);
    }

    @Test
    @Tag("manual")
    void checkDoiInSource() {
        publicationSourceService.checkDoiInSource();
    }

}
