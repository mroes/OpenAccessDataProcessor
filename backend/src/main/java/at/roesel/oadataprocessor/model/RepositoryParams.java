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

import com.fasterxml.jackson.annotation.JsonInclude;

import static at.roesel.oadataprocessor.services.oaipmh.OaiPmhImporter.mdf_oai_dc;

/*
    Parameters for Repositories
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RepositoryParams {

    private final static String default_oaipmhMetadataFormat = mdf_oai_dc;

    // for OaiPmh clients
    private String oaipmhSetName;
    private String oaipmhMetadataFormat;

    // for Uibk client
    private String clientId;

    public String getOaipmhSetName() {
        return oaipmhSetName;
    }

    public void setOaipmhSetName(String oaipmhSetName) {
        this.oaipmhSetName = oaipmhSetName;
    }

    public String getOaipmhMetadataFormat() {
        if (oaipmhMetadataFormat != null && !oaipmhMetadataFormat.isEmpty()) {
            return oaipmhMetadataFormat;
        }
        return default_oaipmhMetadataFormat;
    }

    public void setOaipmhMetadataFormat(String oaipmhMetadataFormat) {
        this.oaipmhMetadataFormat = oaipmhMetadataFormat;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
