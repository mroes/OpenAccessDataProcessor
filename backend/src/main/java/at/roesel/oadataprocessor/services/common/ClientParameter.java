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

package at.roesel.oadataprocessor.services.common;

import java.time.LocalDate;

public class ClientParameter {
    private final String url;
    private final String apiKey;  // for e.g. Pure API
    private String proxyConfig;  // needed for local development for TU Graz
    private LocalDate startDate;  // start date for the query of publications
    private String queryFormat;   // format of the data, e.g. OaiPmhImporter.mdf_oai_dc
    private String setName;   // optional, set name for OaiPmh

    private ClientParameter(String url, String apiKey, String proxyConfig) {
        this.url = url;
        this.apiKey = apiKey;
        this.proxyConfig = proxyConfig;
    }

    private ClientParameter(String url, String apiKey) {
        this(url, apiKey, "");
    }

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getProxyConfig() {
        return proxyConfig;
    }

    public static ClientParameter of(String url, String apiKey, String proxyConfig) {
        ClientParameter parameter = new ClientParameter(url, apiKey);
        parameter.proxyConfig = proxyConfig;
        return parameter;
    }

    public static ClientParameter of(String url, String apiKey) {
        return of(url, apiKey, null);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getQueryFormat() {
        return queryFormat;
    }

    public void setQueryFormat(String queryFormat) {
        this.queryFormat = queryFormat;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
}
