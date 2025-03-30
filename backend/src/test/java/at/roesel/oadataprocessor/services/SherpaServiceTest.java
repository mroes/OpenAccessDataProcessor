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

package at.roesel.oadataprocessor.services;

import at.roesel.oadataprocessor.model.sherpa.Romeo;
import at.roesel.oadataprocessor.model.sherpa.SherpaObjectResponse;
import at.roesel.oadataprocessor.services.sherpa.SherpaRomeoSupport;
import at.roesel.oadataprocessor.services.sherpa.SherpaService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SherpaServiceTest {

    @Autowired
    private SherpaService sherpaService;

    @Test
    @Tag("manual")
    public void sherpa_test() {
        String issn = "0273-1177";

        SherpaObjectResponse result = sherpaService.objectByID(issn);

        if (result != null && result.items != null) {
            Romeo romeo = result.items.get(0);
            int embargo = SherpaRomeoSupport.findEmbargoTime(romeo);
            assertEquals(24, embargo);
        }
    }

    @Test
    @Tag("manual")
    public void romeoForIssnTest() {
        String issn = "0027-8424";

        Romeo result = sherpaService.romeoForIssn(issn);
        assertNotNull(result);
    }


}
