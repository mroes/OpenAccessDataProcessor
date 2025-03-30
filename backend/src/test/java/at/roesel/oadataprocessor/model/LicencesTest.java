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

package at.roesel.oadataprocessor.model;

import at.roesel.common.StringSupport;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LicencesTest {


    @Test
    void standardizeCreativeCommonsName() {
        assertEquals("CC BY-SA", Licences.standardizeCreativeCommonsName("cc-by-sa"));
    }

    @Test
    void buildNormalizedLicence() {
        assertEquals("CC BY-SA", Licences.buildNormalizedLicence("cc-by-sa").getNormalizedLicence());
        assertEquals("none", Licences.buildNormalizedLicence("").getNormalizedLicence());
        assertEquals("other", Licences.buildNormalizedLicence("https://doi.org/10.1128/ASMCopyrightv2").getNormalizedLicence());
        assertEquals("CC0", Licences.buildNormalizedLicence("CC0").getNormalizedLicence());

        NormalizedLicence result = Licences.buildNormalizedLicence("http://creativecommons.org/licenses/by-nc-nd/2.0/de/deed.de");
        assertEquals("CC BY-NC-ND", result.getNormalizedLicence());
        assertEquals("2.0", result.getVersion());

        result = Licences.buildNormalizedLicence("https://creativecommons.org/licenses/by/4.0");
        assertEquals("CC BY", result.getNormalizedLicence());
        assertEquals("4.0", result.getVersion());

        result = Licences.buildNormalizedLicence("https://pubs.acs.org/page/policy/authorchoice_ccby_termsofuse.html");
        assertEquals("CC BY", result.getNormalizedLicence());

        result = Licences.buildNormalizedLicence("https://pubs.acs.org/page/policy/authorchoice_ccbyncnd_termsofuse.html");
        assertEquals("CC BY-NC-ND", result.getNormalizedLicence());
    }

    @Test
    void testLicences() throws URISyntaxException, IOException {
        URL resource = StringSupport.class.getClassLoader().getResource("testdata/licences.txt");
        List<String> licences = Files.readAllLines(Paths.get(resource.toURI()));
        for (String licenceRow : licences) {
            String[] parts = licenceRow.split("\t");
            NormalizedLicence result = Licences.buildNormalizedLicence(parts[0]);
            LicenceType licenceType = Licences.getLicenceType(parts[0]);
            assertEquals(parts[1], result.getNormalizedLicence());
            assertEquals(parts[2], result.getVersion());
            assertEquals(parts[3], licenceType.toString());
//            System.out.println(licence + "\t" + result.getNormalizedLicence() + "\t" + result.getVersion() + "\t" + licenceType);
        }
    }

}
