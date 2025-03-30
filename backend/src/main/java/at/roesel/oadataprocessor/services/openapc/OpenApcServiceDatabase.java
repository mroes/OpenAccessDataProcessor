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

package at.roesel.oadataprocessor.services.openapc;

import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.openapc.ApcRow;
import at.roesel.oadataprocessor.model.openapc.OpenApcEntity;
import at.roesel.oadataprocessor.model.openapc.OpenApcFields;
import at.roesel.oadataprocessor.model.openapc.OpenApcRecord;
import at.roesel.oadataprocessor.persistance.OpenApcRepository;
import at.roesel.oadataprocessor.services.impexp.CsvImportHandler;
import at.roesel.oadataprocessor.services.impexp.LastImportService;
import at.roesel.oadataprocessor.services.impexp.ImportType;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.model.openapc.OpenApcEntity.AGREEMENT;
import static at.roesel.oadataprocessor.model.openapc.OpenApcEntity.RECORD;
import static at.roesel.oadataprocessor.support.CsvSupport.field2boolean;
import static at.roesel.oadataprocessor.support.CsvSupport.field2int;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;
import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

/*
 * Service for apc costs from OpenAPC
 * https://github.com/OpenAPC/openapc-de
 */
@Component
public class OpenApcServiceDatabase implements OpenApcService {

    private final static Logger logger = LoggerFactory.getLogger(OpenApcServiceDatabase.class);

    @Value("${openapc.csv.url.apc}")
    private String openApcCsvUrl;

    @Value("${openapc.csv.url.ta}")
    private String transformativeAgreementsCsvUrl;

    @Autowired
    private AppSettings appSettings;

    @Autowired
    private LastImportService lastImportService;

    private final OpenApcRepository openApcRepository;

    private final ObjectDiffer openApcEntityDiffer;

    @Autowired
    public OpenApcServiceDatabase(OpenApcRepository openApcRepository) {
        this.openApcRepository = openApcRepository;

        ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
        builder.inclusion().exclude().propertyName("created")
                .and()
                .inclusion().exclude().propertyName("updated");
        openApcEntityDiffer = builder.build();
    }

    @Override
    public OpenApcRecord searchByDoi(String doi) {
        return openApcRepository.findByDoi(doi);
    }

    public void importRecordsFromCSV(Path path, int recordType) {
        OpenApcCsvImporter importer = new OpenApcCsvImporter();

        final AtomicInteger count = new AtomicInteger();
        try (InputStream is = Files.newInputStream(path)) {
            importer.readFromStream(is, (CsvImportHandler<ApcRow>) row -> {
                OpenApcEntity record = fromRow(row);
                // Without a DOI, we ignore the record as we can't do anything with it anyway.
                if (!hasValue(record.getDoi())) {
                    return;
                }
                record.setTa(recordType == AGREEMENT);
                try {
                    OpenApcEntity existingRecord = openApcRepository.findByDoi(record.getDoi());
                    if (existingRecord != null) {
                        // Only import TA if no individual record exists for the DOI
                        if (!record.isTa() ||
                                record.isTa() && existingRecord.isTa()) {
                            boolean hasDifference = compareRecords(existingRecord, record);
                            if (hasDifference) {
                                // save the new record if there are changes
                                record.setCreated(existingRecord.getCreated());
                                openApcRepository.save(record);
                            }
                        }
                    } else {
                        openApcRepository.save(record);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                count.incrementAndGet();
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug(String.format("importRecordsFromCSV: %d openAPC records imported", count.get()));
    }

    public static OpenApcEntity fromRow(ApcRow row) {
        OpenApcEntity record = new OpenApcEntity();

        for (Map.Entry<String, String> entry : row.entrySet()) {
            setField(record, entry.getKey(), entry.getValue());
        }

        String doi = row.getField(OpenApcFields.doi);
        record.setId(doi);
        record.setAmount(row.getApcAmount());
        return record;
    }

    private static void setField(Object object, String fieldName, String strValue) {
        try {
            // field content NA is ignored
            if ("NA".equals(strValue)) {
                return;
            }
            Field field = getField(object.getClass(), fieldName, true);
            if (field != null) {
                Object value;
                if (field.getType().equals(String.class)) {
                    value = strValue;
                } else if (field.getType().equals(boolean.class)) {
                    value = field2boolean(strValue);
                } else if (field.getType().equals(int.class)) {
                    value = field2int(strValue);
                } else {
                    throw new RuntimeException("unhandled field type " + field.getType().getName());
                }
                writeField(field, object, value);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean compareRecords(OpenApcEntity existingRecord, OpenApcEntity newRecord) {
        boolean changes = false;
        DiffNode diff = openApcEntityDiffer.compare(newRecord, existingRecord);
        if (diff.hasChanges()) {
            changes = true;
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("%s has changes", existingRecord.getDoi()));
                diff.visit((node, visit) -> {
                    final Object baseValue = node.canonicalGet(existingRecord);
                    final Object workingValue = node.canonicalGet(newRecord);
                    final String message = node.getPath() + " changed from " +
                            baseValue + " to " + workingValue;
                    logger.debug(message);
                });
            }
        }
        return changes;
    }

    private String currentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

    public void updateOpenAPCData() {
        OpenApcClient client = new OpenApcClient();
        try {
            logger.info("starting updateOpenAPCData()");
            // APC
            Path apcPath = Path.of(appSettings.getDataPath(), "openapc", "apc_de_" + currentDate() + ".csv");
            client.downloadCSV(openApcCsvUrl, apcPath);
            importRecordsFromCSV(apcPath, RECORD);

            // transformative agreements
            Path transformativeAgreementsPath = Path.of(appSettings.getDataPath(), "openapc", "transformative_agreements_" + currentDate() + ".csv");
            client.downloadCSV(transformativeAgreementsCsvUrl, transformativeAgreementsPath);
            importRecordsFromCSV(transformativeAgreementsPath, RECORD);

            lastImportService.saveLastServiceImportDate(ImportType.OPEN_APC, LocalDate.now());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
