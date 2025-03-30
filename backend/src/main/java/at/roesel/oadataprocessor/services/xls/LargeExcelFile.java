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

package at.roesel.oadataprocessor.services.xls;

import at.roesel.oadataprocessor.services.common.FieldAccessor;
import at.roesel.oadataprocessor.services.common.RowImportHandler;
import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static at.roesel.common.StringSupport.hasValue;

/*
 * Importing from a large Excel file
 * https://stackoverflow.com/questions/11891851/how-to-load-a-large-xlsx-file-with-apache-poi
 * https://github.com/monitorjbl/excel-streaming-reader
 */
public class LargeExcelFile {

    private final Logger logger = LoggerFactory.getLogger(LargeExcelFile.class);

    private final HashMap<String, Integer> headerMap = new HashMap<>();
    private final List<String> headers = new ArrayList<>();

    protected boolean ignoreFirstLine;
    private boolean exceptionOnMissingColumn = true;

    private final RowImportHandler rowHandler;
    protected Sheet currentSheet;

    public LargeExcelFile(RowImportHandler rowHandler, boolean ignoreFirstLine) {
        this.ignoreFirstLine = ignoreFirstLine;
        this.rowHandler = rowHandler;
    }

    public void setExceptionOnMissingColumn(boolean exceptionOnMissingColumn) {
        this.exceptionOnMissingColumn = exceptionOnMissingColumn;
    }

    public Sheet getCurrentSheet() {
        return currentSheet;
    }

    public void readFromStream(InputStream inputStream) throws IOException {
        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(inputStream)) {

            int numSheets = workbook.getNumberOfSheets();
            logger.debug(String.format("Number of sheets: %d", numSheets));
            if (numSheets > 0) {
                for (int sheetNo = 0; sheetNo < numSheets; sheetNo++) {
                    Sheet sheet = workbook.getSheetAt(sheetNo);
                    logger.debug(String.format("Reading sheet %d [%s]", sheetNo, sheet.getSheetName()));
                    parseSheet(sheet);
                }
            }
        }

    }

    public void readFromStream(InputStream inputStream, int sheetNo) throws IOException {

        try (Workbook workbook = StreamingReader.builder()
            .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
            .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
            .open(inputStream)) {

            int numSheets = workbook.getNumberOfSheets();
            logger.debug(String.format("Number of sheets: %d", numSheets));
            if (numSheets > 0 && sheetNo < numSheets) {
                Sheet sheet = workbook.getSheetAt(sheetNo);
                logger.debug(String.format("Reading sheet %d [%s]", sheetNo, sheet.getSheetName()));
                parseSheet(sheet);
            }
        }

    }

    private void parseSheet(Sheet sheet) {
        currentSheet = sheet;
        int line = 0;
        for (Row row : sheet) {
            if (line == 0 && ignoreFirstLine) {
                createHeaderMap(row);
            } else {
                parseRow(row);
            }
            line++;
        }
        logger.debug(String.format("Parsed %d lines from %s", line, sheet.getSheetName()));
    }

    private void createHeaderMap(Row row) {
        headers.clear();
        int lastCellNum = row.getLastCellNum();
        for (int i = 0; i <= lastCellNum; i++) {
            Cell cell = row.getCell(i);
            String name;
            if (cell != null) {
                name = cell.getStringCellValue();
                if (hasValue(name)) {
                    name = normalizeHeader(name);
                }
            } else {
                name = "unknown" + i;
            }
            headerMap.put(name, i);
            headers.add(name);
        }
    }

    private String normalizeHeader(String header) {
        StringBuilder sb = new StringBuilder();
        String[] parts = header.split("\n");
        for (String part : parts) {
            if (hasValue(part)) {
                sb.append(part);
            }
        }
        return sb.toString().trim();
    }

    public String columnName(int index) {
        return headers.get(index);
    }

    public int columnIndexForName(String name) {
        Integer index = headerMap.get(name);
        if (index != null) {
            return index;
        }
        if (exceptionOnMissingColumn) {
            throw new UnknownColumnException("Unknown column '" + name + "'");
        } else {
            return -2;
        }
    }

    protected void parseRow(Row row) {
        FieldAccessor fieldAccessor = new ExcelFieldAccessor(this, row);
        rowHandler.handle(fieldAccessor);
    }

    public class ExcelFieldAccessor implements FieldAccessor {

        private final Row row;
        private final LargeExcelFile excelFile;

        public ExcelFieldAccessor(LargeExcelFile excelFile, Row row) {
            this.excelFile = excelFile;
            this.row = row;
        }

        @Override
        public String getString(String columnName) {
            try {
                int number = columnIndexForName(columnName);
                if (number >= 0) {
                    Cell cell = row.getCell(number);
                    if (cell != null) {
                        return getString(cell);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return "";
        }

        private String getString(Cell cell) {
            String value = cell.getStringCellValue();
            return value.trim();
        }

        public Sheet getCurrentSheet() {
            Sheet sheet = excelFile.getCurrentSheet();
            return sheet;
        }

    }

}

