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

import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.ui.*;
import at.roesel.oadataprocessor.services.publisher.MainPublisherSupplier;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import static at.roesel.oadataprocessor.config.CachingConfig.*;

@Component
public class DataService {

    private final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private PublicationTypeService publicationTypeService;

    @Cacheable(value = CommonCacheName, sync = true)
    public CommonData commonData() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CommonData commonData = new CommonData();
        commonData.setOaColors(appSettings.getOpenaccessColors());
        commonData.setOaColorsReduced(appSettings.getOpenaccessColorsReduced());
        commonData.setInstitutions(institutionService.findAllActive().stream().map(JsonInstitution::from).sorted().collect(Collectors.toList()));
        commonData.setInactiveInstitutions(institutionService.findAllInactive().stream().map(JsonInstitution::from).sorted().collect(Collectors.toList()));
        commonData.setPublicationTypes(publicationTypeService.findAllByEnabled().stream().map(JsonPublicationType::from).sorted().collect(Collectors.toList()));
        logger.debug(String.format("commonData(), prepare time=%d", stopWatch.getTime()));
        stopWatch.stop();
        return commonData;
    }

    @Cacheable(value = PublisherCacheName, sync = true)
    public PublisherData publisherData() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        PublisherData publisherData = new PublisherData();
        Collection<Publisher> publishers = publisherService.readActivePublishers();
        Collator germanCollator = Collator.getInstance(Locale.GERMAN);
        germanCollator.setStrength(Collator.PRIMARY); // Ignores case differences
        List<Publisher> sortedPublishers = publishers.stream()
                .filter(pub -> pub.getName() != null) // Exclude null names
                .sorted((pub1, pub2) -> germanCollator.compare(pub1.getName(), pub2.getName()))
                .collect(Collectors.toList());
        publisherData.setPublishers(sortedPublishers);
        logger.debug(String.format("publisherData(), prepare time=%d", stopWatch.getTime()));
        stopWatch.stop();
        return publisherData;
    }

    @Cacheable(value = PublicationsCacheName, key = "'stats'", sync = true)
    public PublicationPublisherStats publicationStats() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        PublicationPublisherStats stats = new PublicationPublisherStats();
        List<PublicationColor> values = publicationService.readColorsPerYearAndPublisher();
        logger.debug(String.format("readColorsPerYearAndPublisher(), prepare time=%d", stopWatch.getTime()));

        stopWatch.reset();
        stopWatch.start();
        List<PublicationColor> valuesPerInstitution = publicationService.readColorsPerYearAndInstitutionAndPublisher();
        logger.debug(String.format("readColorsPerYearAndInstitutionAndPublisher(), time=%d", stopWatch.getTime()));
        int year = 0;  // take the year from the publishing date of the publication itself
        stopWatch.reset();
        stopWatch.start();
        stats.setNumbers(values);
        stats.setNumbersPerInstitution(valuesPerInstitution);
        logger.debug(String.format("readPublishers(), time=%d", stopWatch.getTime()));
        stopWatch.stop();
        return stats;
    }

    @Cacheable(value = PublicationsCacheName, key = "'statsByPublisher'", sync = true)
    public List<PublicationColor> publicationStatsPerPublisher() {
        List<PublicationColor> values = publicationService.readColorsPerYearAndInstitutionAndPublisher();
        Iterable<Publisher> publishers = publisherService.readPublishers();
        MainPublisherSupplier supplier = new MainPublisherSupplier(publishers, 0);
        supplier.supplyMainPublisher(values);

        return values;
    }

    @Cacheable(value = PublicationsCacheName, key = "'statsByType'", sync = true)
    public PublicationStatsPair publicationStatsByPublicationType() {
        PublicationStatsPair stats = new PublicationStatsPair();
        stats.setNumbers(publicationService.readColorsPerYearAndPublicationType());
        stats.setNumbersPerInstitution(publicationService.readColorsPerYearAndInstitutionAndPublicationType());
        return stats;
    }

    @Cacheable(value = PublicationsCacheName, key = "'statsByLicence'", sync = true)
    public PublicationStatsPair publicationStatsByLicence() {
        PublicationStatsPair stats = new PublicationStatsPair();
        stats.setNumbers(publicationService.readColorsPerYearAndLicence());
        stats.setNumbersPerInstitution(publicationService.readColorsPerYearAndInstitutionAndLicence());
        return stats;
    }

    public void clearPublicationStatsCache() {
        Cache cache = cacheManager.getCache(PublicationsCacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public void clearAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }

}
