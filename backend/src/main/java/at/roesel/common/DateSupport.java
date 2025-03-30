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

package at.roesel.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateSupport {

    public static final LocalDate MIN_DATE = LocalDate.of(-1000, 1, 1);
    public static final LocalDate MAX_DATE = LocalDate.of(2999, 12, 31);

    // convert with default ZoneId
    public static LocalDate localDateFrom(Date date) {
        LocalDate result = null;
        if (date != null) {
            result = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return result;
    }

    // convert date into an int date YYYYMMDD
    private static String fromLocalDate(LocalDate date) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%04d", date.getYear()));
        sb.append(String.format("%02d", date.getMonthValue()));
        sb.append(String.format("%02d", date.getDayOfMonth()));
        return sb.toString();
    }

    public static int toIntDate(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return Integer.parseInt(fromLocalDate(date));
    }

    private static LocalDate fromIntDate(String iDate) {
        if (iDate == null || iDate.length() != 8) {
            return null;
        }
        return LocalDate.of(Integer.parseInt(iDate.substring(0,4)), Integer.parseInt(iDate.substring(4,6)), Integer.parseInt(iDate.substring(6,8)));
    }

    public static LocalDate fromIntDate(int iDate) {
        if (iDate == 0) {
            return null;
        }
        return fromIntDate(String.valueOf(iDate));
    }

    public static Instant fromISO9601(String timestampStr) {
        Instant timestamp;
        try {
            timestamp = Instant.parse(timestampStr);
        } catch (Exception e) {
            timestamp = Instant.now();
        }
        return timestamp;
    }
}
