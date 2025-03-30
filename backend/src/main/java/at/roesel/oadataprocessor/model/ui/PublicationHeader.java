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
import at.roesel.oadataprocessor.model.PublicationFlat;

/*
 Daten einer Publikation für die Anzeige von Einzelpublikationen
 */
public class PublicationHeader {
    private String id;
    private String doi;
    private String title;
    private String publisher;
    private String publisherId;
    private String mainPublisher;
    private String mainPublisherId;
    private int year;
    private int pubtypeId;
    private String color;

    public PublicationHeader(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDoi() {
        return doi;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public String getMainPublisher() {
        return mainPublisher;
    }

    public String getMainPublisherId() {
        return mainPublisherId;
    }

    public int getYear() {
        return year;
    }

    public int getPubtypeId() {
        return pubtypeId;
    }

    public String getColor() {
        return color;
    }

    public static PublicationHeader from(Publication publication) {
        PublicationHeader jsonPub = new PublicationHeader((publication.getId()));
        jsonPub.doi = publication.getDoi();
        jsonPub.title = publication.getTitle();
        jsonPub.publisher = publication.getPublisher();
        jsonPub.publisherId = publication.getPublisherId();
        jsonPub.year = publication.getYear();
        jsonPub.pubtypeId = publication.getPubtypeId();
        jsonPub.color = publication.getColor();
        return jsonPub;
    }

    public static PublicationHeader from(PublicationFlat publication) {
        PublicationHeader jsonPub = new PublicationHeader((publication.getId()));
        jsonPub.doi = publication.getDoi();
        jsonPub.title = publication.getTitle();
        jsonPub.publisher = publication.getPublisher().getName();
        jsonPub.publisherId = publication.getPublisher().getId();
        jsonPub.mainPublisher = publication.getMainPublisher().getName();
        jsonPub.mainPublisherId = publication.getMainPublisher().getId();
        jsonPub.year = publication.getYear();
        jsonPub.pubtypeId = publication.getType().getId();
        jsonPub.color = publication.getColor();
        return jsonPub;
    }
}
