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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileSupport {

    private final static Logger logger = LoggerFactory.getLogger(FileSupport.class);

    public static String nameFromUrl(String url) {
        int posStart = url.indexOf("//");
        int posEnd = url.indexOf("?");
        if (posEnd == -1) {
            posEnd = url.length()-1;
        }
        String result = url.substring(posStart+2, posEnd);
        result = result.replace('.', '_');
        result = result.replace('/', '_');
        return result;
    }

    public static void writeStringToFile(String path, String contents) {
        try(FileOutputStream outputStream = new FileOutputStream(path)) {
            byte[] strToBytes = contents.getBytes(StandardCharsets.UTF_8);
            outputStream.write(strToBytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static String readStringFromFile(String path) {
        try(FileInputStream inputStream = new FileInputStream(path)) {
            return readFromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            logger.warn("{} not found!", path);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return "";
    }

    private static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static boolean isExcel(String fileName) {
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    public static boolean isCsv(String fileName) {
        return fileName.toLowerCase().endsWith(".csv");
    }
}
