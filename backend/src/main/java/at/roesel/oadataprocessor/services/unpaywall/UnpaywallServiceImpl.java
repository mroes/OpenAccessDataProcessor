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

package at.roesel.oadataprocessor.services.unpaywall;

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.unpaywall.UnpaywallResource;
import at.roesel.oadataprocessor.model.unpaywall.UnpaywallSource;
import at.roesel.oadataprocessor.persistance.UnpaywallRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

//@Profile("!test")
@Component
public class UnpaywallServiceImpl implements UnpaywallService, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(UnpaywallServiceImpl.class);

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private UnpaywallRepository unpaywallRepository;

    private UnpaywallClient restClient;


    public UnpaywallServiceImpl() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        restClient = new UnpaywallClient(appSettings.getContactEMail());
    }

    public UnpaywallResource resource(String doi) {
        UnpaywallResource resource =  restClient.resource(doi);
        /*
        if (resource != null) {
            resource.myCreated = SystemTime.currentTimeMillis();
        }
         */
        return resource;
    }

    /*
     * Data for the DOI is read from the file (in the same JSON format as with the REST query)
     */
    public UnpaywallResource readResource(File jsonFile, String doi) {

        UnpaywallResource resource;
        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            resource = jsonMapper.readValue(jsonFile, UnpaywallResource.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return resource;
    }

    public UnpaywallResource getResource(String doi) {
        UnpaywallResource result = null;
        Optional<UnpaywallSource> unpaywallSourceOpt = unpaywallRepository.findById(doi);
        boolean fetch = false;
        if (unpaywallSourceOpt.isPresent()) {
            logger.debug("unpaywall data for {} from database", doi);
            UnpaywallSource unpaywallSource = unpaywallSourceOpt.get();
            Long updated = unpaywallSource.getUpdated();
            if (updated == null || updated == 0) {
                updated = unpaywallSource.getCreated();
            }
            if (unpaywallSource.getStatus().ok()) {
                result = unpaywallSource.getRecord();
                // If the query is older than 2 months, try again.
                if (updated < SystemTime.currentTimeMillis() - 2 * 30 * 86400 * 1000L) {
                    fetch = true;
                }
            } else {
                // If the query is older than one month, try again.
                if (updated < SystemTime.currentTimeMillis() - 30 * 86400 * 1000L) {
                    fetch = true;
                }
            }
        } else {
            fetch = true;
        }
        if (fetch) {
            logger.debug("fetching unpaywall data for {}", doi);
            try {
                result = resource(doi);
                UnpaywallSource source = new UnpaywallSource(doi, result);
                unpaywallRepository.save(source);
            } catch (Exception e) {
                logger.error("doi: {} {}", doi, e.getMessage(), e);
                if (result == null) {
                    result = new UnpaywallResource();
                    result.setError(e.getMessage());
                }
            }
        }
        return result;
    }

}
