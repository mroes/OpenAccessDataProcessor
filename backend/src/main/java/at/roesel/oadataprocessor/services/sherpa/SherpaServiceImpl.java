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

package at.roesel.oadataprocessor.services.sherpa;

import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.SourceStatus;
import at.roesel.oadataprocessor.model.sherpa.Romeo;
import at.roesel.oadataprocessor.model.sherpa.RomeoPublisher;
import at.roesel.oadataprocessor.model.sherpa.RomeoSource;
import at.roesel.oadataprocessor.model.sherpa.SherpaObjectResponse;
import at.roesel.oadataprocessor.persistance.RomeoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class SherpaServiceImpl implements InitializingBean, SherpaService {

    private final Logger logger = LoggerFactory.getLogger(SherpaServiceImpl.class);

    private final AppSettings settings;
    private final RomeoRepository romeoRepository;

    private SherpaClient client;

    public SherpaServiceImpl(AppSettings settings, RomeoRepository romeoRepository) {
        super();
        this.settings = settings;
        this.romeoRepository = romeoRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = new SherpaClient(settings.getApiKeySherpa());
    }

    @Override
    public SherpaObjectResponse objectByID(String issn) {
        return client.objectByID(issn);
    }

    public Romeo romeoForIssn(String issn) {
        Optional<RomeoSource> result = romeoRepository.findById(issn);
        if (result.isPresent()) {
            RomeoSource romeoSource = result.get();
            if (romeoSource.isOk()) {
                return romeoSource.getRecord();
            }
        } else {
            SherpaObjectResponse response = objectByID(issn);
            if (response.hasItems()) {
                logger.debug("new romeo data for {}", issn);
                Romeo romeo = response.items.get(0);
                RomeoSource romeoSource = new RomeoSource(issn, romeo);
                romeoRepository.save(romeoSource);
                return romeo;
            } else {
                logger.debug("no romeo data for {}", issn);
                RomeoSource romeoSource = new RomeoSource(issn, null);
                if (response.hasError()) {
                    Romeo romeo = new Romeo();
                    romeo.setAdditionalProperty("error", response.getError());
                    romeoSource.setStatus(SourceStatus.OTHER_ERROR.getCode());
                } else {
                    romeoSource.setStatus(SourceStatus.NOT_FOUND.getCode());
                }
                romeoRepository.save(romeoSource);
            }
        }
        return null;
    }

    public Romeo romeoForIssns(List<String> issns) {
        for (String issn : issns) {
            Romeo romeo = romeoForIssn(issn);
            if (romeo != null) {
                return romeo;
            }
        }

        return null;
    }

    public void fetchPublishers(Consumer<RomeoPublisher> visitor) {
        SherpaClient client = new SherpaClient(settings.getApiKeySherpa());
        client.fetchPublishers(visitor);
    }

}
