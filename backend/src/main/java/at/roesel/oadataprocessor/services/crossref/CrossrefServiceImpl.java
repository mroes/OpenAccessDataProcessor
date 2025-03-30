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

package at.roesel.oadataprocessor.services.crossref;

import at.roesel.common.StringSupport;
import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.crossref.CrossrefPrefix;
import at.roesel.oadataprocessor.model.crossref.CrossrefSource;
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;
import at.roesel.oadataprocessor.persistance.CrossrefRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CrossrefServiceImpl implements CrossrefService, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(CrossrefServiceImpl.class);

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private CrossrefRepository crossrefRepository;

    private CrossrefClient restClient;


    public CrossrefServiceImpl() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        restClient = new CrossrefClient(appSettings.getContactEMail());
    }

    @Override
    public CrossrefWork crossrefWork(String doi) {
        CrossrefWork crossrefWork = restClient.crossrefWork(doi);
        return crossrefWork;
    }

    @Override
    public CrossrefPrefix crossrefPrefix(String prefix) {
        return restClient.crossrefPrefix(prefix);
    }

    @Override
    public CrossrefDoiSearchResult searchWork(String author, String title) {
        String result = StringSupport.removeStopWords(title);
        return restClient.searchWork(author, result);
    }

    public void deleteCrossrefSourcefromDB(String doi) {
        crossrefRepository.deleteById(doi);
    }

    public CrossrefSource readCrossrefSourcefromDB(String doi) {
        Optional<CrossrefSource> crossrefSourceOpt = crossrefRepository.findById(doi);
        return crossrefSourceOpt.orElse(null);
    }

    public CrossrefWork getCrossrefWork(String doi) {
        CrossrefWork result = null;
        CrossrefSource crossrefSource = readCrossrefSourcefromDB(doi);
        boolean fetch = false;
        if (crossrefSource != null) {
            logger.debug("crossref data for {} from database", doi);
            // If no Crossref data is available and a certain period has passed since the last query, then query again.
            if (crossrefSource.getStatus().ok()) {
                result = crossrefSource.getRecord();
            } else {
                Long updated = crossrefSource.getUpdated();
                if (updated == null || updated == 0) {
                    updated = crossrefSource.getCreated();
                }
                // If the query is older than one month, try again.
                if (updated < SystemTime.currentTimeMillis() - 30 * 86500 * 1000L) {
                    fetch = true;
                }
            }
        } else {
            fetch = true;
        }
        if (fetch) {
            logger.debug("fetching crossref data for {}", doi);
            try {
                result = crossrefWork(doi);
                CrossrefSource source = new CrossrefSource(doi, result);
                crossrefRepository.save(source);
            } catch (Exception e) {
                logger.error("error while handling doi: {} {}", doi, e.getMessage(), e);
                if (result == null) {
                    result = new CrossrefWork();
                    result.setError(e.getMessage());
                }
            }
        }
        return result;
    }

}
