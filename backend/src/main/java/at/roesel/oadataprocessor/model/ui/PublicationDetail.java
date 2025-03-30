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

package at.roesel.oadataprocessor.model.ui;

import at.roesel.oadataprocessor.model.Publication;

import static org.springframework.beans.BeanUtils.copyProperties;

public class PublicationDetail extends Publication {
    private String publisherName;
    private String publisherWikiId;
    private String journalName;
    private String journalWikiId;
    private String licence;
    private String costs;

    private String oaPlace;
    private String oaUrl;
    private String embargoTime;

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherWikiId() {
        return publisherWikiId;
    }

    public void setPublisherWikiId(String publisherWikiId) {
        this.publisherWikiId = publisherWikiId;
    }

    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public String getJournalWikiId() {
        return journalWikiId;
    }

    public void setJournalWikiId(String journalWikiId) {
        this.journalWikiId = journalWikiId;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    public String getOaPlace() {
        return oaPlace;
    }

    public void setOaPlace(String oaPlace) {
        this.oaPlace = oaPlace;
    }

    public String getOaUrl() {
        return oaUrl;
    }

    public void setOaUrl(String oaUrl) {
        this.oaUrl = oaUrl;
    }

    public String getEmbargoTime() {
        return embargoTime;
    }

    public void setEmbargoTime(String embargoTime) {
        this.embargoTime = embargoTime;
    }

    public static PublicationDetail from(Publication publication) {
        PublicationDetail targetPublication = new PublicationDetail();
        copyProperties(publication, targetPublication);
        return targetPublication;
    }

}
