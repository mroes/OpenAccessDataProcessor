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

package at.roesel.oadataprocessor.services.openalex;

import at.roesel.common.SleepSupport;
import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.OpenAlexSource;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.persistance.OpenAlexRepository;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import jakarta.persistence.NonUniqueResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OpenAlexServiceImpl implements OpenAlexService, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(OpenAlexServiceImpl.class);

    private OpenAlexClient restClient;

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private OpenAlexRepository openAlexRepository;

    public OpenAlexServiceImpl() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String contactMail = appSettings.getContactEMail();
        restClient = new OpenAlexClient(contactMail);
    }

    public OpenAlexWork work(String doi) {
        SleepSupport.sleep(150);
        return restClient.work(doi);
    }

    @Override
    public List<OpenAlexWork> works(String parameters) {
        return restClient.works(parameters);
    }

    public void fetchWorks(String parameters, ResultResponseHandler<OpenAlexWork> resultResponseHandler) {
        restClient.fetchWorks(parameters, resultResponseHandler);
    }

    @Override
    public OpenAlexWork getWork(String doi) {
        OpenAlexWork result = null;
        Optional<OpenAlexSource> openAlexSourceOpt = null;
        String saveId = null;
        try {
            openAlexSourceOpt = openAlexRepository.findByDoi(doi);
        } catch (NonUniqueResultException e) {
            logger.error(String.format("OpenAlexService.getWork: doi = %s, too many results: %s", doi, e.getMessage()));
            return null;
        } catch (Exception e) {
            logger.error(String.format("OpenAlexService.getWork: doi = %s, %s", doi, e.getMessage()), e);
            return null;
        }
        boolean fetch = false;
        if (openAlexSourceOpt.isPresent()) {
            logger.debug("openAlex data for {} from database", doi);
            OpenAlexSource openAlexSource = openAlexSourceOpt.get();
            saveId = openAlexSource.getId(); // we need this id to overwrite an existing record and avoid duplicates
            Long updated = openAlexSource.getUpdated();
            if (updated == null || updated == 0) {
                updated = openAlexSource.getCreated();
            }
            if (openAlexSource.getStatus().ok()) {
                result = openAlexSource.getRecord();
                // If the query is older than 2 months, try again
                if (updated < SystemTime.currentTimeMillis() - 2 * 30 * 86400 * 1000L) {
                    fetch = true;
                }
            } else {
                // If the query is older than one month, try again
                if (updated < SystemTime.currentTimeMillis() - 30 * 86400 * 1000L) {
                    fetch = true;
                }
            }
        } else {
            fetch = true;
        }
        if (fetch) {
            logger.debug("fetching openAlex data for {}", doi);
            try {
                OpenAlexWork work = work(doi);
                OpenAlexSource source = new OpenAlexSource();
                source.setDoi(doi);
                source.setStatus(work.status());
                source.setRecord(work);
                if (saveId != null) {
                    source.setId(saveId);
                } else {
                    source.setId(work.id);
                }
                if (source.getStatus().ok()) {
                    result = work;
                }
                openAlexRepository.save(source);
            } catch (Exception e) {
                logger.error("error fetching openAlex for doi: {} {}", doi, e.getMessage(), e);
            }
        }
        return result;
    }

}
