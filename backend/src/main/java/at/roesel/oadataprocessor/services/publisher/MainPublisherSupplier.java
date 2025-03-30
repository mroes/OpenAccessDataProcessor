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
import at.roesel.oadataprocessor.model.ui.PublicationColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static at.roesel.common.StringSupport.hasValue;

public class MainPublisherSupplier {

    private final Logger logger = LoggerFactory.getLogger(MainPublisherSupplier.class);

    private final int year;
    private final PublisherMap publisherMap = new PublisherMap();
    private final PublisherMap mainPublisherMap = new PublisherMap();

    /*
     * @param year: set to 0, if the publication year (parameter year) shall be used to determine the parent publisher.
     */
    public MainPublisherSupplier(Iterable<Publisher> publishers, int year) {
        this.year = year;
        publisherMap.fillById(publishers);
    }

    public PublisherMap getMainPublisherMap() {
        return mainPublisherMap;
    }

    /*
     * search the main publisher for a publisher in the aggregated publication data and replace the publisherId with it
     */
    public void supplyMainPublisher(List<PublicationColor> values) {

        for (PublicationColor pc : values) {
            String publisherId = pc.publisherId;
            if (!hasValue(publisherId) || publisherId.equals(Publisher.UNKNOWN_PUBLISHER)) {
                continue;
            }
            Publisher mainPublisher = findMainPublisher(publisherId, pc.year);
            if (mainPublisher == null) {
                pc.mainPublisherId = publisherId;
            } else {
                pc.mainPublisherId = mainPublisher.getId();
            }
        }
    }

    /*
     * searches for the main publisher of the given publisher
     * publicationYear is used if no specific year was set in the constructor
     *
     * @param publisherId search main publisher for publisher with publisherId
     * @param publicationYear usually the publishing date of a publication issued by the publisher or the year of interest
     */
    public Publisher findMainPublisher(String publisherId, int publicationYear) {
        int searchYear;
        String key;
        if (this.year != 0) {
            // use the given year
            searchYear = year;
            key = publisherId;  // the year is constant, we just need the publisher in the key
        } else {
            // use year of publication
            searchYear = publicationYear;
            // Include the year in the map because the main publisher can be different for different years.
            key = searchYear + ":" + publisherId;
        }
        Publisher mainPublisher = mainPublisherMap.get(key);
        if (mainPublisher == null) {
            mainPublisher = publisherMap.findMainPublisher(publisherId, searchYear);
            if (mainPublisher != null) {
                mainPublisherMap.put(key, mainPublisher);
            } else {
                logger.warn("Found main publisher for publisherId = {}, but no active record was found for main publisher", publisherId);
            }
        }
        return mainPublisher;
    }

}
