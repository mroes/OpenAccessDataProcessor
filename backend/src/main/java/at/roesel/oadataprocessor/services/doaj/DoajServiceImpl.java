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

package at.roesel.oadataprocessor.services.doaj;

import at.roesel.common.SystemTime;
import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.doaj.DoajJournal;
import at.roesel.oadataprocessor.model.doaj.DoajJournalEntity;
import at.roesel.oadataprocessor.model.doaj.DoajPayload;
import at.roesel.oadataprocessor.persistance.DoajRepository;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/*
 *  Services for Doaj
 */
@Component
public class DoajServiceImpl implements DoajService, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(DoajServiceImpl.class);

    // cache for temporarily storing issns that are not found in Doaj
    private com.github.benmanes.caffeine.cache.Cache<String, Boolean> issnCache;

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private DoajRepository doajRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        issnCache = Caffeine.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS)
                .maximumSize(10000)
                .build();
    }

    public DoajJournal searchForDoaj(List<String> issns) {
        DoajJournalEntity doajJournalDatabase = searchDatabaseForDoaj(issns);
        if (doajJournalDatabase != null) {
            // check age of entry
            long updated = doajJournalDatabase.getUpdated();
            if (updated == 0) {
                updated = doajJournalDatabase.getCreated();
            }
            // return this entity if it is not older than 7 days
            if (updated > SystemTime.currentTimeMillis() - 7 * 86400 * 1000) {
                if (doajJournalDatabase.isDeleted()) {
                    return null;
                }
                return doajJournalDatabase;
            }
        }
        // search doaj.org
        try {
            DoajJournalEntity doajJournal = searchOnlineForDoaj(issns);
            if (doajJournal != null) {
                DoajJournalEntity existingDoajJournal = doajRepository.findById(doajJournal.getId()).orElse(null);
                if (existingDoajJournal == null) {
                    // add Journal
                    doajRepository.save(doajJournal);
                } else {
                    if (doajJournalDatabase != null) {
                        if (!doajJournalDatabase.getId().equals(existingDoajJournal.getId())) {
                            logger.error("Journal found by issn={} in DOAJ has another id={} " +
                                    "than the one found in the database with id={}", issns,
                                    existingDoajJournal.getId(), doajJournalDatabase.getId());
                        }
                    }
                    // update Journal
                    doajJournal.setCreated(existingDoajJournal.getCreated());
                    doajRepository.save(doajJournal);
                }
                return doajJournal;
            } else {
                // if we found the journal in the database but could not find it online than
                // mark it as deleted
                if (doajJournalDatabase != null && !doajJournalDatabase.isDeleted()) {
                    doajJournalDatabase.setDeleted(SystemTime.currentTimeMillis());
                    doajRepository.save(doajJournalDatabase);
                }
            }
        } catch (Exception e) {
            // if we have an older DOAJ entry from the database we return this one in case of an error
            return doajJournalDatabase;
        }

        return null;
    }

    public DoajJournalEntity searchDatabaseForDoaj(List<String> issns) {
        for (String issn : issns) {
            Iterable<DoajJournalEntity> journals = doajRepository.searchByIssn(issn);
            // we return the first journal of the list
            for (DoajJournalEntity doajJournal : journals) {
                return doajJournal;
            }
        }
        return null;
    }

    public DoajJournalEntity searchOnlineForDoaj(List<String> issns) {
        DoajClient doajClient = new DoajClient(appSettings.getContactEMail());
        for (String issn : issns) {
            if (issnCache.getIfPresent(issn) != null) {
                continue;
            }
            List<DoajPayload> result = doajClient.searchJournal(issn);
            if (!result.isEmpty()) {
                DoajJournalEntity doajJournalEntity = result.get(0).buildJournal();
                return doajJournalEntity;
            }
        }

        // We did not find the issns in doaj. Add the issns to the cache
        for (String issn : issns) {
            if (issnCache.getIfPresent(issn) == null) {
                issnCache.put(issn, true);
            }
        }

        return null;
    }

}
