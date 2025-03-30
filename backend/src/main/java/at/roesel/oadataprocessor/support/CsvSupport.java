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

package at.roesel.oadataprocessor.support;

import at.roesel.oadataprocessor.services.impexp.OutputDescriptor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CsvSupport {

    private final static Logger logger = LoggerFactory.getLogger(CsvSupport.class);

    public static void printFile(String filename, List<String> headers, List<List<String>> rows) throws IOException {

        CSVFormat.Builder builder = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                .setIgnoreSurroundingSpaces(true)
                .setHeader(headers.toArray(new String[0]));
        CSVFormat csvFormat = builder.build();

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename));

                CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
        ) {
            writer.write('\ufeff');
            for (List<String> row : rows) {
                csvPrinter.printRecord(row);
            }

            csvPrinter.flush();
        }
    }

    public static <T> void exportCsv(String filename, OutputDescriptor<T> outputDescriptor, List<T> rows) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename));
        writer.write('\ufeff');
        exportCsv(writer, outputDescriptor, rows);
    }

    public static <T> void exportCsv(Writer writer, OutputDescriptor<T> outputDescriptor, List<T> rows) throws IOException {

        CSVFormat.Builder builder = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                .setIgnoreSurroundingSpaces(true)
                .setDelimiter("\t")
                .setHeader(outputDescriptor.getHeaders().toArray(new String[0]));
        CSVFormat csvFormat = builder.build();

        try (
                CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
        ) {
            for (T row : rows) {
                csvPrinter.printRecord(outputDescriptor.cells(row));
            }
            csvPrinter.flush();
        }
    }

    public static int yearFromString(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        String[] parts =str.split("[^0-9]+");
        if (parts.length == 0) {
            return 0;
        }
        int year = Integer.parseInt(parts[0]);
        return year;
    }

    public static boolean field2boolean(String value) {
        if (value != null) {
            value = value.toLowerCase();
            if (value.equals("true")) {
                return true;
            }
        }
        return false;
    }

    public static int field2int(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                int result = Integer.parseInt(value);
                return result;
            } catch (NumberFormatException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return 0;
    }
}
