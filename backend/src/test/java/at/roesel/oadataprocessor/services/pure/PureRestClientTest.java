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

package at.roesel.oadataprocessor.services.pure;

import at.roesel.oadataprocessor.config.Identifiers;
import at.roesel.oadataprocessor.model.Institution;
import at.roesel.oadataprocessor.services.InstitutionService;
import at.roesel.oadataprocessor.services.pure.model.PureResearchOutput;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class PureRestClientTest {

    @Autowired
    private InstitutionService institutionService;

    @Test
    @Tag("manual")
    void getResearchOutputs() {
        Institution institution = institutionService.findById(Identifiers.idUniSalzburg);
        PureRestClient client = new PureRestClient(institution.getRepositoryUrl(), institution.getRepositoryKey(), null);
        LocalDate modifiedAfter = LocalDate.of(2022,3,1);
        List<PureResearchOutput> results = client.getResearchOutputs(modifiedAfter);
        System.out.println(results.size());
    }
}