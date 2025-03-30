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
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static at.roesel.common.StringSupport.hasValue;

public class PublisherIdentifier {

    private final static List<String> excludedWords = Arrays.asList(
            "of", "the", "and",
            "Ltd",
            "Ltd.",
            "Inc",
            "Inc.",
            "AG",
            "KG",
            "GmbH",
            "e.V.",
            "Co",
            "Verlag",
            "&",
            "@",
            "-"
    );

    private final static int MIN_RATIO = 80;
    private static final int MAX_RATIO = 100;
    private final Iterable<Publisher> allPublishers;

    private final Logger logger = LoggerFactory.getLogger(PublisherIdentifier.class);

    // stateful, wird in identify gesetzt und ist bis zum nächsten Aufruf von identify vorhanden
    private List<PublisherResult> candidates;

    protected final PublisherSource publisherSource;


    public PublisherIdentifier(PublisherSource publisherSource) {
        this.publisherSource = publisherSource;

        allPublishers = readPublishers();
        normalizeIsni(allPublishers);
    }

    public Iterable<Publisher> readPublishers() {
        return publisherSource.readPublishers();
    }

    private void normalizeIsni(Iterable<Publisher> publishers) {
        for (Publisher publisher : publishers) {
            String isni = publisher.getIsni();
            if (hasValue(isni)) {
                String normalizedIsni = isni.replaceAll(" ", "");
                publisher.setIsni(normalizedIsni);
            }
        }
    }

    private Publisher findPublisherById(String publisherId) {
        for (Publisher publisher : allPublishers) {
            if (publisher.getId().equals(publisherId)) {
                return publisher;
            }
        }
        return null;
    }


    public Publisher identify(String publisherName) {
        if (publisherName == null || publisherName.isEmpty()) {
            return null;
        }

        // all comparisons are in lower case
        publisherName = publisherName.toLowerCase();

        NameParts nameParts = NameParts.stripNameInParenthesis(publisherName);

        String searchName = nameParts.name;

        List<PublisherResult> publishers = findPublisherByName(searchName);
        candidates = publishers;
        Publisher result = bestPublisher(nameParts, publishers);
        return result;
    }

    private Publisher bestPublisher(NameParts nameParts, List<PublisherResult> candidates) {
        if (candidates.isEmpty()) {
            return null;
        } else if (candidates.size() == 1) {
            return mainPublisher(candidates.get(0).publisher);
        }

        nameParts.nameParts = nameParts(nameParts.name);

        PublisherResult bestMatch = new PublisherResult( 0);
        for (PublisherResult candidate : candidates) {
            // If the name matches completely, we can return this publisher
            if (candidate.publisher.isEqualName(nameParts.name)) {
                return mainPublisher(candidate.publisher);
            }
            if (candidate.ratio > bestMatch.ratio) {
                if (includesAllParts(candidate, nameParts)) {
                    bestMatch = candidate;
                }
            }
        }

        return mainPublisher(bestMatch.publisher);
    }

    // Checks if all name parts are present in the candidate
    private boolean includesAllParts(PublisherResult candidate, NameParts nameParts) {
        String name = candidate.publisher.getName().toLowerCase();
        int count = 0;
        for (String part : nameParts.nameParts) {
            int ratio = FuzzySearch.partialRatio(part, name);
            if (ratio > MIN_RATIO) {
                count++;
            }
        }
        return count == nameParts.nameParts.size();
    }

    // using Fuzzy logic to compare publisher names
    private List<PublisherResult> findPublisherByName(String searchName) {
        List<PublisherResult> publishers = new ArrayList<>();
        for (Publisher publisher : allPublishers) {
            int ratio = FuzzySearch.ratio(publisher.getName().toLowerCase(), searchName);
            if (ratio == MAX_RATIO) {
                publishers.add(new PublisherResult(publisher, ratio));
            } else if (ratio > MIN_RATIO) {
                publishers.add(new PublisherResult(publisher, ratio));
            } else {
                String[] alternateNames = publisher.getAlternateNames();
                for (String name : alternateNames) {
                    ratio = FuzzySearch.ratio(name.toLowerCase(), searchName);
                    if (ratio == MAX_RATIO) {
                        publishers.add(new PublisherResult(publisher, ratio));
                    } else if (ratio > MIN_RATIO) {
                        publishers.add(new PublisherResult(publisher, ratio));
                        break;
                    }
                }
            }
        };
        return publishers;
    }

    private Publisher mainPublisher(Publisher publisher) {
        if (publisher == null || !hasValue(publisher.getMainId())) {
            return publisher;
        }
        Publisher mainPublisher = findPublisherById(publisher.getMainId());
        if (mainPublisher != null) {
            return mainPublisher;
        }
        return publisher;
    }

    public Publisher find(int id, String newRor) {
        String romeoId = String.valueOf(id);
        if (newRor == null) {
            newRor = "";
        }
        for (Publisher publisher : allPublishers) {
            if (romeoId.equals(publisher.getRomeoId())) {
                return publisher;
            }
            if (!newRor.isEmpty() && newRor.equals(publisher.getRor())) {
                return publisher;
            }
        }
        return null;
    }

    public List<PublisherResult> candidates() {
        return candidates;
    }

    List<String> nameParts(String publisherName) {
        publisherName = publisherName.replace(",", " ");
        String[] parts = publisherName.split(" ");
        List<String> nameParts = new ArrayList<>();
        for (String part : parts) {
            if (isExcludedWord(part)) {
                continue;
            }
            if (!part.isEmpty()) {
                nameParts.add(part);
            }
        }

        return nameParts;
    }

    private boolean isExcludedWord(String word) {
        for (String excluded : excludedWords) {
            if (word.equalsIgnoreCase(excluded)) {
                return true;
            }
        }

        return false;
    }

}
