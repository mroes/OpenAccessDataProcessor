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

package at.roesel.oadataprocessor.services;

import at.roesel.oadataprocessor.model.*;
import at.roesel.oadataprocessor.services.publisher.JournalProvider;
import at.roesel.oadataprocessor.services.publisher.PublisherMap;

import java.util.HashMap;
import java.util.Map;

import static at.roesel.common.StringSupport.hasValue;

public class PublicationFlattener {
    private final Map<String, Institution> institutionMap;
    private final Map<Integer, PublicationType> pubTypeMap;
    private final PublisherMap publisherMap;

    private final JournalProvider journalProvider;
    private final Map<String, Journal> journalMap;

    public PublicationFlattener(Map<String, Institution> institutionMap, Map<Integer, PublicationType> pubTypeMap,
                                PublisherMap publisherMap, JournalProvider journalProvider) {
        this.pubTypeMap = pubTypeMap;
        this.institutionMap = institutionMap;
        this.publisherMap = publisherMap;
        this.journalProvider = journalProvider;
        journalMap = new HashMap<>();
    }

    public PublicationFlat flatten(Publication publication) {
        PublicationFlat publicationFlat = PublicationFlat.from(publication);

        PublicationType publicationType = pubTypeMap.get(publication.getPubtypeId());
        if (publicationType != null) {
            publicationFlat.getType().setCoarId(publicationType.getCoarId());
            publicationFlat.getType().setName(publicationType.getName());
        }
        String publisherId = publication.getPublisherId();
        if (hasValue(publisherId) && !publisherId.startsWith(Publication.UNKNOWN_PREFIX)) {
            Publisher publisher = publisherMap.get(publisherId);
            if (publisher != null) {
                publicationFlat.getPublisher().setName(publisher.getName());
                publicationFlat.getPublisher().setWikidataId(publisher.getWikiId());
            }
        }
        String mainPublisherId = publication.getMainPublisherId();
        if (hasValue(mainPublisherId) && !publisherId.startsWith(Publication.UNKNOWN_PREFIX)) {
            Publisher publisher = publisherMap.get(mainPublisherId);
            if (publisher != null) {
                publicationFlat.getMainPublisher().setName(publisher.getName());
                publicationFlat.getMainPublisher().setWikidataId(publisher.getWikiId());
            }
        }
        String journalId = publication.getJournalId();
        if (hasValue(journalId) && !journalId.startsWith(Publication.UNKNOWN_PREFIX)) {
            Journal journal = findJournal(journalId);
            if (journal != null) {
                publicationFlat.getJournal().setTitle(journal.getName());
                publicationFlat.getJournal().setWikidataId(journal.getWikiId());
                publicationFlat.getJournal().setIssn(journal.issnsAsList());
            }
        }

        return publicationFlat;
    }

    private Journal findJournal(String journalId) {
        Journal journal = null;
        if (hasValue(journalId)) {
            journal = journalMap.get(journalId);
            if (journal == null) {
                journal = journalProvider.readJournalById(journalId);
                journalMap.put(journalId, journal);
            }
        }
        return journal;
    }
}
