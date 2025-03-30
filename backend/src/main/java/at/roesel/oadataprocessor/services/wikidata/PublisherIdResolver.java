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

import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.PublisherVar;
import at.roesel.oadataprocessor.services.publisher.PublisherMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static at.roesel.common.StringSupport.hasValue;

/*
    search and set internal publisherIds for publisher.wikiParentId and publisher.variable.wikiParentId
 */
public class PublisherIdResolver {

    private final Logger logger = LoggerFactory.getLogger(PublisherIdResolver.class);

    private final PublisherMap publisherPerWikiId;

    public PublisherIdResolver(PublisherMap publisherPerWikiId) {
        this.publisherPerWikiId = publisherPerWikiId;
    }

    // search internal publisherIds for wikidataId
    public boolean resolvePublisherIds(Publisher publisher) {
        boolean changed = false;
        if (hasValue(publisher.getWikiParentId())) {
            Publisher parentPublisher = findPublisher(publisher, publisher.getWikiParentId());
            if (parentPublisher != null) {
                if (!Objects.equals(parentPublisher.getId(), publisher.getParentId())) {
                    changed = true;
                    publisher.setParentId(parentPublisher.getId());
                }
            }
        }
        else {
            if (!Objects.equals("", publisher.getParentId())) {
                changed = true;
                publisher.setParentId("");
            }
        }

        if (publisher.getVariable() != null) {
            for (PublisherVar var : publisher.getVariable()) {
                if (hasValue(var.getWikiParentId())) {
                    Publisher foundPublisher = findPublisher(publisher, var.getWikiParentId());
                    if (foundPublisher != null) {
                        if (!Objects.equals(foundPublisher.getId(), var.getParentId())) {
                            changed = true;
                            var.setParentId(foundPublisher.getId());
                        }
                    }
                } else {
                    if (!Objects.equals("", var.getParentId())) {
                        changed = true;
                        var.setParentId("");
                    }
                    var.setParentId("");
                }
            }
        }
        return changed;
    }

    private Publisher findPublisher(Publisher publisher, String wikiId) {
        Publisher result = null;
        if (wikiId != null && !wikiId.isEmpty()) {
            result = publisherPerWikiId.get(wikiId);
            if (result == null) {
                logger.warn(String.format("%s (%s) no publisher found for wikidataId=%s", publisher.getName(), publisher.getWikiId(), wikiId));
            }
        }
        return result;
    }

}
