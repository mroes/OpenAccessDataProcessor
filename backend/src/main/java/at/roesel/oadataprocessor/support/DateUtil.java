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

import at.roesel.common.DateSupport;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String formatDateIso8601(LocalDate date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssX");
        return date.atStartOfDay().atOffset(ZoneOffset.UTC).format(dtf);
    }

    public static String formatDateYYYYMMDD(LocalDate date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        return date.atStartOfDay().atOffset(ZoneOffset.UTC).format(dtf);
    }

    public static LocalDate localDateFromIso8601(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        return localDate;
    }

    public static int localDateToInt(LocalDate localDate) {
        int date = localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
        return date;
    }

    public static LocalDate intTolocalDate(int date) {
        if (date == 0) {
            return DateSupport.MIN_DATE;
        }
        int year = date / 10000;
        int day = date % 100;
        int month = (date / 100) % 100;
        return LocalDate.of(year, month, day);
    }

    public static int yearFromStr(String date) {
        if (date != null && !date.isEmpty()) {
            StringBuilder numericalChars = new StringBuilder();
            for (char ch : date.toCharArray()) {
                if (Character.isDigit(ch)) {
                    numericalChars.append(ch);
                } else {
                    break; // Stop at the first non-numerical character
                }
            }
            if (!numericalChars.isEmpty()) {
                try {
                    return Integer.parseInt(numericalChars.toString());
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return 0;
    }
}
