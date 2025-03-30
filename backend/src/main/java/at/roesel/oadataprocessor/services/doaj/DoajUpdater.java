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
import at.roesel.oadataprocessor.model.doaj.DoajJournalEntity;
import at.roesel.oadataprocessor.model.doaj.DoajPayload;
import at.roesel.oadataprocessor.persistance.DoajRepository;
import at.roesel.oadataprocessor.persistance.conversion.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DoajUpdater {

    private final Logger logger = LoggerFactory.getLogger(DoajUpdater.class);

    private final DoajRepository doajRepository;
    private final Map<String, DoajJournalEntity> journalMap;

    private final ObjectMapper mapper;
    private final CollectionType javaType;

    private final ObjectDiffer entityDiffer;

    public DoajUpdater(DoajRepository doajRepository, Map<String, DoajJournalEntity> journalMap) {
        this.doajRepository = doajRepository;
        this.journalMap = journalMap;

        mapper = ObjectMapperFactory.create();
        javaType = mapper.getTypeFactory()
                .constructCollectionType(List.class, DoajPayload.class);

        ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
        builder.inclusion().exclude().propertyName("created")
                .and()
                .inclusion().exclude().propertyName("updated")
                .and()
                .inclusion().exclude().propertyName("deleted");

        entityDiffer = builder.build();
    }

    // https://stackoverflow.com/questions/54817985/how-to-parse-a-huge-json-file-without-loading-it-in-memory
    public void importDoajRecordsFromJson(String jsonFile) {
        try {
            List<DoajPayload> results = mapper.readValue(new File(jsonFile), javaType);
            for (DoajPayload payload : results) {
                DoajJournalEntity journal = payload.buildJournal();
                try {
                    DoajJournalEntity existingJournal = journalMap.get(journal.getId());
                    if (existingJournal == null) {
                        doajRepository.save(journal);
                    } else {
                        boolean hasDifference = compareRecords(existingJournal, journal);
                        if (hasDifference) {
                            // Journal aktualisieren
                            journal.setCreated(existingJournal.getCreated());
                            doajRepository.save(journal);
                        }
                        // aus der Map entfernen als Kennzeichen dafür, dass es noch vorhanden ist
                        journalMap.remove(journal.getId());
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            // übrig gebliebene Journale als gelöscht markieren
            for (DoajJournalEntity entity : journalMap.values()) {
                // wenn das Journal bereits gelöscht wurde, dann uber
                if (entity.getDeleted() > 0) {
                    continue;
                }
                entity.setDeleted(SystemTime.currentTimeMillis());
                doajRepository.save(entity);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean compareRecords(DoajJournalEntity existingRecord, DoajJournalEntity newRecord) {
        boolean changes = false;
        DiffNode diff = entityDiffer.compare(newRecord, existingRecord);
        if (diff.hasChanges()) {
            logger.debug(String.format("journal id=%s has changes", existingRecord.getId()));
            changes = true;
            diff.visit((node, visit) -> {
                final Object baseValue = node.canonicalGet(existingRecord);
                final Object workingValue = node.canonicalGet(newRecord);
                final String message = node.getPath() + " changed from " +
                        baseValue + " to " + workingValue;
                logger.debug(message);
            });
        }
        return changes;
    }

}
