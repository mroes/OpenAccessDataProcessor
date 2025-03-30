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

package at.roesel.oadataprocessor.services.impexp.export;

import at.roesel.oadataprocessor.services.impexp.OutputDescriptor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelStreamingExporter {

    private final static int rowSize = 100;

    public static <T> void export(OutputStream outputStream, OutputDescriptor<T> outputDescriptor, List<T> rows) throws IOException {

        List<String> headers = outputDescriptor.getHeaders();
        try (SXSSFWorkbook workbook = new SXSSFWorkbook (rowSize)) {
            Sheet sheet = workbook.createSheet("publications");
            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            // Data
            int rowNum = 1;
            for (T row : rows) {
                Row excelRow = sheet.createRow(rowNum++);
                List<String> cells = outputDescriptor.cells(row);
                for (int i = 0; i < cells.size(); i++) {
                    excelRow.createCell(i).setCellValue(cells.get(i));
                }
            }

            // Write the output to a file
            workbook.write(outputStream);

            // Close the workbook
            try {
                workbook.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }
}
