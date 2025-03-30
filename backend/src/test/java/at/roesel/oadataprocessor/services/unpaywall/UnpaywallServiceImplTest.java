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

package at.roesel.oadataprocessor.services.unpaywall;

import at.roesel.oadataprocessor.model.unpaywall.UnpaywallResource;
import at.roesel.oadataprocessor.support.DoiSupport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.net.URL;

import static at.roesel.common.TestResourceHelper.fileForTestResource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UnpaywallServiceImplTest {

    @Autowired
    private UnpaywallService unpaywallService;

    @Test
    public void testReadFileWithResource() {
        URL url = this.getClass().getResource("/testdata/10.1038_s41467-017-02159-y.json");
        File file = new File(url.getFile());
        assertTrue(file.exists());
    }

    @Test
    void readResource() {
        String doi = "10.1038/s41467-017-02159-y";
        File doiFile = fileForTestResource(DoiSupport.fileNameForDoi(doi)+".json");
        UnpaywallResource resource = unpaywallService.readResource(doiFile, doi);
        assertEquals(doi, resource.doi);
    }

    @Test
    @Tag("manual")
    void getResource() {
        String doi = "10.1038/s41467-017-02159-y";
        UnpaywallResource resource = unpaywallService.getResource(doi);
        assertEquals(doi, resource.doi);
    }
}
