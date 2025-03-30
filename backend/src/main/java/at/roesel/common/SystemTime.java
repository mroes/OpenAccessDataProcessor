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

import java.time.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * Configurable source for times
 */
public class SystemTime {

    private static final TimeSource defaultTimeSource =
            new TimeSource() {
                public long currentTimeMillis() {
                    return System.currentTimeMillis();
                }

                public Calendar getToday() {
                    return new GregorianCalendar();
                }
            };

    private static TimeSource timeSource = defaultTimeSource;

    public static void setTimeSource(TimeSource timeSource) {
        if (timeSource == null)
            SystemTime.timeSource = defaultTimeSource;
        else
            SystemTime.timeSource = timeSource;
    }

    public static void reset() {
        setTimeSource(null);
    }

    public static long currentTimeMillis() {
        return (timeSource.currentTimeMillis());
    }

    public static Instant now() {
        return Instant.ofEpochMilli(timeSource.currentTimeMillis());
    }

    public static Calendar getToday() {
        return timeSource.getToday();
    }

    public static ZonedDateTime millsToZonedDateTime(ZoneId zoneId, long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime;
    }

    public static LocalTime currentLocalTime(ZoneId zoneId) {
        return millsToZonedDateTime(zoneId, timeSource.currentTimeMillis()).toLocalTime();
    }

    public static LocalDate currentLocalDate(ZoneId zoneId) {
        return millsToZonedDateTime(zoneId, timeSource.currentTimeMillis()).toLocalDate();
    }

    public static LocalDateTime currentLocalDateTime(ZoneId zoneId) {
        return millsToZonedDateTime(zoneId, timeSource.currentTimeMillis()).toLocalDateTime();
    }

    public static ZonedDateTime currentZonedDateTime(ZoneId zoneId) {
        return millsToZonedDateTime(zoneId, timeSource.currentTimeMillis());
    }

}
