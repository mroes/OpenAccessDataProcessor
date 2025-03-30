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

import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.services.common.ClientParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OaiPmhServiceImpl implements OaiPmhService {

    private final Logger logger = LoggerFactory.getLogger(OaiPmhServiceImpl.class);

    @Autowired
    private AppSettings appSettings;

     /*
       @param url Service URL
       @param loadXml Read XML data from a file instead of over the network
       @param saveXml Save XML data from the network to a file
     */
    @Override
    public OaiPmhImporter createOaiPmhImporter(String url, boolean loadXml, boolean saveXml) {
        if (loadXml) {
            OaiPmhImporterFile importer = new OaiPmhImporterFile(url, appSettings.getDataPath());
            return importer;
        } else {
            OaiPmhImporterImpl importer = new OaiPmhImporterImpl(url);
            importer.setSaveXml(saveXml);
            importer.setDataPath(appSettings.getDataPath());
            return importer;
        }
    }

    @Override
    public OaiPmhImporter createOaiPmhImporter(String url) {
        return createOaiPmhImporter(url, false, false);
    }

    @Override
    public void fetchPublications(ClientParameter parameter, OaiPmhResultResponseHandler resultResponseHandler) {
        OaiPmhImporter importer = createOaiPmhImporter(parameter.getUrl());
        importer.fetchRecords(parameter.getStartDate(), parameter.getQueryFormat(), parameter.getSetName(), resultResponseHandler);
    }


}
