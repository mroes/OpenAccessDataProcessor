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

package at.roesel.oadataprocessor.config;

public final class ElasticsearchSettingsBuilder {
    private String host;
    private int port;
    private String pathToCaCert;
    private String fingerprint;
    private String user;
    private String password;

    private ElasticsearchSettingsBuilder() {
    }

    public static ElasticsearchSettingsBuilder anElasticsearchSettings() {
        return new ElasticsearchSettingsBuilder();
    }

    public ElasticsearchSettingsBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    public ElasticsearchSettingsBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public ElasticsearchSettingsBuilder withPathToCaCert(String pathToCaCert) {
        this.pathToCaCert = pathToCaCert;
        return this;
    }

    public ElasticsearchSettingsBuilder withFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public ElasticsearchSettingsBuilder withUser(String user) {
        this.user = user;
        return this;
    }

    public ElasticsearchSettingsBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public ElasticsearchSettings build() {
        ElasticsearchSettings elasticsearchSettings = new ElasticsearchSettings();
        elasticsearchSettings.setHost(host);
        elasticsearchSettings.setPort(port);
        elasticsearchSettings.setPathToCaCert(pathToCaCert);
        elasticsearchSettings.setFingerprint(fingerprint);
        elasticsearchSettings.setUser(user);
        elasticsearchSettings.setPassword(password);
        return elasticsearchSettings;
    }
}
