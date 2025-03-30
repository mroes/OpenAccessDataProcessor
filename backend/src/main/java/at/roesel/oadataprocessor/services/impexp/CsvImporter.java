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

import at.roesel.oadataprocessor.model.CsvRow;
import at.roesel.oadataprocessor.services.common.FieldAccessor;
import at.roesel.oadataprocessor.services.common.RowImportHandler;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvImporter<T extends CsvRow> {

    private final Logger logger = LoggerFactory.getLogger(CsvImporter.class);

    private final Class<T> rowClass;
    private final Constructor<T> rowConstructor;
    private final CSVFormat csvFormat;
    private List<String> fieldNames;
    private int maxCount;   // Maximum number of records to be read

    // The rowClass is required to ensure that the correct class can be created.
    public CsvImporter(Class<T> rowClass) {
        this(rowClass,
                CSVFormat.Builder.create(CSVFormat.DEFAULT)
                        .setIgnoreSurroundingSpaces(true)
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setAllowMissingColumnNames(true));
    }

    public CsvImporter(Class<T> rowClass, CSVFormat.Builder builder) {
        this.rowClass = rowClass;
        try {
            this.rowConstructor = rowClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        csvFormat = builder.build();
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public List<T> readFromStream(InputStream inputStream) throws IOException {

        List<T> result = new ArrayList<>();
        readFromStream(inputStream, (CsvImportHandler<T>) result::add);
        return result;
    }

    public <T extends CsvRow> void readFromStream(InputStream inputStream, CsvImportHandler<T> handler) throws IOException {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            Iterable<CSVRecord> records = csvFormat.parse(in);
            boolean firstRecord = true;
            int count = 0;
            for (CSVRecord record : records) {
                count++;
                if (count > maxCount && maxCount != 0) {
                    break;
                }
                if (firstRecord) {
                    // extract column names
                    fieldNames = record.getParser().getHeaderNames();
                    firstRecord = false;
                }
                T row = parseRecord(record);
                handler.handle(row);
            }
        }
    }

    private <T extends CsvRow> T parseRecord(CSVRecord record) {
        T row = null;
        try {
            row = (T) rowConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        int index = 0;
        for (String value : record) {
            try {
                row.setField(fieldNames.get(index), value);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            index++;
        }

        return row;
    }


    public void readFromStream(InputStream inputStream, RowImportHandler handler) throws IOException {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            Iterable<CSVRecord> records = csvFormat.parse(in);
            boolean firstRecord = true;
            int count = 0;
            for (CSVRecord record : records) {
                count++;
                if (count > maxCount && maxCount != 0) {
                    break;
                }
                if (firstRecord) {
                    // extract column names
                    fieldNames = record.getParser().getHeaderNames();
                    firstRecord = false;
                }
                CSVFieldAccessor accessor = new CSVFieldAccessor(record);
                handler.handle(accessor);
            }
        }
    }

    class CSVFieldAccessor implements FieldAccessor {

        private final CSVRecord record;

        public CSVFieldAccessor(CSVRecord record) {
            this.record = record;
        }

        @Override
        public String getString(String columnName) {
            try {
                return record.get(columnName);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }

}

