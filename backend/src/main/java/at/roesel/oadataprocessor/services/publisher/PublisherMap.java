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

package at.roesel.oadataprocessor.services.publisher;

import at.roesel.oadataprocessor.model.Publisher;

import java.util.HashMap;

public class PublisherMap extends HashMap<String, Publisher> {

    private final static ParentPublisherFinder parentPublisherFinder = new ParentPublisherFinderImpl();

    public void fillById(Iterable<Publisher> publishers) {
        publishers.forEach(publisher -> {
            put(publisher.getId(), publisher);
        });
    }

    public void fillByWikiId(Iterable<Publisher> publishers) {
        publishers.forEach(publisher -> {
            put(publisher.getWikiId(), publisher);
        });
    }

    public Publisher findMainPublisher(String publisherId, int year) {
        Publisher parentPublisher;
        while (true) {
            Publisher publisher = null;
            Publisher pubFound = getPublisher(publisherId);
            if (pubFound == null) {
                // parent publisher is not available in the table publishers
                return publisher;
            } else {
                publisher = pubFound;
            }
            publisherId = parentPublisherFinder.findParent(publisher, year);
            // There are also self-references, which we need to catch here
            if (publisherId == null || publisher.getId().equals(publisherId)) {
                // es gibt keinen Parent mehr
                parentPublisher = publisher;
                break;
            }
        }
        // The parent can also be an institution, which should not be used as a parent.
        // e.g. not University Oxford statt, but University Oxford Press
        // if mainId contains an entry then this publisher is used as parent
        String mainId = parentPublisher.getMainId();
        if (mainId != null && !mainId.isEmpty()) {
            parentPublisher = getPublisher(mainId);
        }
        return parentPublisher;
    }

    private Publisher getPublisher(String publisherId) {
        Publisher publisher = get(publisherId);
        return publisher;
    }
}
