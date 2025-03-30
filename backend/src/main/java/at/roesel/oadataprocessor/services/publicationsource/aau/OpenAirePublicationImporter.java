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

package at.roesel.oadataprocessor.services.publicationsource.aau;

import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.json.JsonAuthor;
import at.roesel.oadataprocessor.model.json.JsonJournal;
import at.roesel.oadataprocessor.model.json.JsonPublication;
import at.roesel.oadataprocessor.persistance.conversion.ObjectConverter;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// using StAX for parsing the OpenAire-Format
public class OpenAirePublicationImporter {

    private final static String publicationsTag = "publications";
    private final static String titleTag = "Title";
    private final static String publicationDateTag = "PublicationDate";
    private final static String typeTag = "Type";
    private final static String urlTag = "URL";
    private final static String authorsTag = "Authors";
    private final static String authorTag = "Author";
    private final static String personTag = "Person";
    private final static String personNameTag = "PersonName";
    private final static String familyNamesTag = "FamilyNames";
    private final static String firstNamesTag = "FirstNames";
    private final static String issnTag = "ISSN";
    private final static String doiTag = "DOI";
    private XMLEventReader reader;
    private XMLEvent nextEvent;

    private final Logger logger = LoggerFactory.getLogger(OpenAirePublicationImporter.class);

    protected final ObjectConverter<JsonPublication> recordConverter = new ObjectConverter<>(JsonPublication.class);

    public List<PublicationSource> readFromStream(InputStream inputStream) throws IOException {

        List<PublicationSource> result = new ArrayList<>();
        readFromStream(inputStream, new ResultResponseHandler<PublicationSource>() {
            @Override
            public boolean handleResponse(PublicationSource record) {
                        result.add(record);
                        return false;
                    }
        });
        return result;
    }

    // UTF8FilterInputStream is used, because from time to time there appear invalid characters in the data of AAU
    // https://www.google.com/search?client=firefox-b-d&q=java+filterinputstram+for+invalid+utf-8+characters
    // https://docs.oracle.com/en/database/oracle/oracle-database/19/jvgdk/oracle/i18n/text/UTF8ValidationFilter.html

    public void readFromStream(InputStream inputStream, ResultResponseHandler<PublicationSource> handler) throws IOException {
        PublicationSource publicationSource = null;
        JsonPublication publication = new JsonPublication();
        JsonAuthor author = new JsonAuthor();
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            reader = xmlInputFactory.createXMLEventReader(new UTF8FilterInputStream(inputStream));
            while (reader.hasNext()) {
                try {
                    nextEvent = reader.nextEvent();
                    if (nextEvent.isStartElement()) {
                        StartElement startElement = nextEvent.asStartElement();
                        switch (startElement.getName().getLocalPart()) {
                            case publicationsTag:
                                publicationSource = new PublicationSource();
                                publication = new JsonPublication();
                                Attribute id = startElement.getAttributeByName(new QName("id"));
                                if (id != null) {
                                    publicationSource.setNativeId(id.getValue());
                                }
                                break;
                            case titleTag:
                                    String title = contents();
                                    publicationSource.setTitle(title);
                                    publication.setTitle(title);
                                break;
                            case publicationDateTag:
                                String date = contents();
                                if (date != null && !date.isEmpty()) {
                                    publication.setPubdate(date);
                                }
                                break;
                            case typeTag:
                                String type = contents();
                                publication.setType(type);
                                break;
                            case doiTag:
                                String doi = contents();
                                publicationSource.setDoi(doi);
                                publication.setDoi(doi);
                                break;
                            case urlTag:
                                String url = contents();
                                if (url != null) {

                                }
                                break;
                            case personNameTag:
                                author = new JsonAuthor();
                                publication.addAuthor(author);
                                break;
                            case familyNamesTag:
                                author.setFamily(contents());
                                break;
                            case firstNamesTag:
                                author.setGiven(contents());
                                break;
                            case issnTag:
                                String issn = contents();
                                if (issn != null) {
                                    JsonJournal journal = publication.getJournal();
                                    if (journal == null) {
                                        journal = new JsonJournal();
                                        publication.setJournal(journal);
                                    }
                                    journal.addIssn(issn);
                                }
                                break;
                        }
                    }
                    if (nextEvent.isEndElement()) {
                        EndElement endElement = nextEvent.asEndElement();
                        if (endElement.getName().getLocalPart().equals(publicationsTag)) {
                            // <publications xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
                            if (publicationSource.getNativeId() != null) {
                                deduplicateAuthors(publication);
                                publicationSource.setRecord(recordConverter.convertToDatabaseColumn(publication));
                                publicationSource.setPubtype(publication.getType());
                                boolean stop = handler.handleResponse(publicationSource);
                                if (stop) {
                                    break;
                                }
                            }
                        }
                    }
                } catch (XMLStreamException e) {
                    logger.error("{}\n{}", e.getMessage(), nextEvent.toString());
                    // ignore error and continue with next Tag if possible
                    nextEvent = reader.nextEvent();
                }
            }
        } catch (XMLStreamException e) {
            logger.error(e.getMessage());
        }

    }

    // Records from AAU sometimes contain multiple author entries.
    private void deduplicateAuthors(JsonPublication publication) {
        List <JsonAuthor> authors = publication.getAuthors();
        List <JsonAuthor> cleanedAuthors = new ArrayList<>();
        for (JsonAuthor author : authors) {
            List<JsonAuthor> simAuthors = cleanedAuthors.stream().filter( a -> a.getFamily().equals(author.getFamily())).toList();
            if (simAuthors.isEmpty()) {
                cleanedAuthors.add(author);
            } else {
                JsonAuthor foundAuthor;
                char initial = author.getGiven().charAt(0);
                List<JsonAuthor> simAuthors2 = simAuthors.stream().filter( a -> a.getGiven().charAt(0) == initial).toList();
                if (simAuthors2.isEmpty()) {
                    cleanedAuthors.add(author);
                    continue;
                } else if (simAuthors2.size() == 1) {
                    foundAuthor = simAuthors2.get(0);
                    // falls der volle Name angegeben ist, prüfen wir auf Übereinstimmung
                    if (author.getGiven().length() > 2 && foundAuthor.getGiven().length() > 2
                        && !author.getGiven().equals(foundAuthor.getGiven())) {
                        logger.warn("found author has same initial, but different given name {}\t{}", foundAuthor, authors);
                        cleanedAuthors.add(author);
                        continue;
                    }
                } else {
                    logger.warn("author cannot be identified uniquely {}", authors);
                    foundAuthor = simAuthors2.get(0);
                }
                // we prefer the full name
                if (author.getGiven().length() > foundAuthor.getGiven().length()) {
                    foundAuthor.setGiven(author.getGiven());
                }
            }
        }
        publication.setAuthors(cleanedAuthors);
    }

    private String contents() throws XMLStreamException {
        nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String data = nextEvent.asCharacters().getData();
            return data;
        }
        return null;
    }
}
