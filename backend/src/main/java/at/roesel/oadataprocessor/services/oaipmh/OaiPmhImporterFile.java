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

package at.roesel.oadataprocessor.services.oaipmh;

import static at.roesel.oadataprocessor.support.FileSupport.nameFromUrl;
import static at.roesel.oadataprocessor.support.FileSupport.readStringFromFile;

/*
  Reads the OAI-PMH data from files with stored XML responses
 */
public class OaiPmhImporterFile extends OaiPmhImporterImpl {

    private int count;
    private String pathTemplate;

    public OaiPmhImporterFile(String url, String dataPath) {
        super(url);
        this.saveXml = false;
        this.dataPath = dataPath;
        count = 1;
        pathTemplate = dataPath + "\\" + nameFromUrl(url) + "%03d.xml" ;
    }

    @Override
    protected String fetchXmlResponse(String requestUrl) {
        // fetchXmlResponse can be called more frequently due to fragmentation
        String path = String.format(pathTemplate, count);
        String contents = readStringFromFile(path);
        count++;
        return contents;
    }


}
