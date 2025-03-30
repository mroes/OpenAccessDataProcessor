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

package at.roesel.oadataprocessor.services.impexp;

import at.roesel.oadataprocessor.config.AppSettings;
import at.roesel.oadataprocessor.model.LastImport;
import at.roesel.oadataprocessor.persistance.LastImportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

import static at.roesel.common.DateSupport.fromIntDate;
import static at.roesel.common.DateSupport.toIntDate;

@Component
public class LastImportService {

    private final Logger logger = LoggerFactory.getLogger(LastImportService.class);

    private AppSettings appSettings;
    private LastImportRepository lastImportRepository;

    public LastImportService(AppSettings appSettings, LastImportRepository lastImportRepository) {
        this.appSettings = appSettings;
        this.lastImportRepository = lastImportRepository;
    }

    private LastImport loadLastImport(String serviceId) {
        Optional<LastImport> lastImport = lastImportRepository.findById(serviceId);
        return lastImport.orElse(null);
    }

    public LocalDate loadLastImportDate(String serviceId) {
        LastImport lastImport = loadLastImport(serviceId);
        LocalDate date = null;
        if (lastImport != null) {
            date = fromIntDate(lastImport.getLastImport());
        }
        if (date == null) {
            return LocalDate.of(2000, 1, 1);
        }
        return date;
    }

    public LocalDate loadLastImportDate(ImportType importType) {
        return loadLastImportDate(importType.getTypeName());
    }

    public void saveLastPublicationImportDate(String serviceId, LocalDate lastUpdate) {
        saveLastImportDate(serviceId, ImportType.PUBLICATION, lastUpdate);
    }

    public void saveLastServiceImportDate(ImportType importType, LocalDate lastUpdate) {
        saveLastImportDate(importType.getTypeName(), importType, lastUpdate);
    }

    public void saveLastImportDate(String serviceId, ImportType type, LocalDate lastUpdate) {
        LastImport lastImport = loadLastImport(serviceId);
        if (lastImport == null) {
            lastImport = new LastImport();
            lastImport.setType(type.getTypeId());
            lastImport.setId(serviceId);
        }
        lastImport.setLastImport(toIntDate(lastUpdate));
        try {
            lastImportRepository.save(lastImport);
        } catch (Exception e) {
            // just log the error
            logger.error("saveLastImportDate {} for {}, {}", lastUpdate, serviceId, type.getTypeName(), e);
        }
    }

}