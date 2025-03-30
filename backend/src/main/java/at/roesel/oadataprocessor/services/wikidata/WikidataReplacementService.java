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

package at.roesel.oadataprocessor.services.wikidata;

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.model.Journal;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.persistance.JournalRepository;
import at.roesel.oadataprocessor.persistance.PublicationRepository;
import at.roesel.oadataprocessor.persistance.PublisherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WikidataReplacementService {

    private final Logger logger = LoggerFactory.getLogger(WikidataReplacementService.class);

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Transactional
    public void replaceAndDeletePublisher(Publisher redirectedPublisher, Publisher publisher) {
        // replace Publisher.wikiParentId, Publisher.parentId
        int result1 = publisherRepository.replaceWikidataPublisher(redirectedPublisher.getWikiId(), publisher.getWikiId(), publisher.getId());
        // replace PublisherVar.wikiParentId, PublisherVar.parentId
        int result2 = publisherRepository.replaceWikidataPublisherVar(redirectedPublisher.getWikiId(), publisher.getWikiId(), publisher.getId());
        // replace Journal.wikiPublisherId, Journal.publisherId
        int result3 = journalRepository.replaceWikidataJournal(redirectedPublisher.getWikiId(), publisher.getWikiId(), publisher.getId());
        // replace JournalVar.wikiPublisherId, JournalVar.publisherId
        int result4 = journalRepository.replaceWikidataJournalVar(redirectedPublisher.getWikiId(), publisher.getWikiId(), publisher.getId());

        // set new publisherId in publication and reset mainPublisherId (will be set next time in publisherIdentificationService.identifyMainPublisher() )
        int result5 = publicationRepository.replacePublisher(redirectedPublisher.getId(), publisher.getId());

        logger.debug(String.format("Replaced publisher %s with %s in Publisher: %d, PublisherVar: %d, Journal: %d, JournalVar: %d, Publication: %d",
                redirectedPublisher.getWikiId(), publisher.getWikiId(), result1, result2, result3, result4, result5));

        // delete replaced Publisher
        publisherRepository.deleteById(redirectedPublisher.getId());
    }

    // no need for @Transactional, it's ok if the journal is replaced in the publications, but cannot be deleted
    public void replaceAndDeleteJournal(Journal redirecedJournal, Journal journal) {
        // replace journal in publications
        int count = publicationRepository.replaceJournal(redirecedJournal.getId(), journal.getId(), SystemTime.currentTimeMillis());
        logger.debug(String.format("Replace journal %s with %s in Publication: %d", redirecedJournal.getWikiId(), journal.getWikiId(), count));

        // delete replaced Journal
        journalRepository.deleteById(redirecedJournal.getId());
    }
}
