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

package at.roesel.oadataprocessor.persistance.conversion;

import at.roesel.common.DateSupport;
import at.roesel.oadataprocessor.support.DateUtil;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

/**
 * Converts a LocalDate into an int for storage in the database.
 */
@Converter
public class DateConverter implements AttributeConverter<LocalDate, Integer> {

    private final static int maxDate = 99991231;

    public DateConverter() {

    }

    @Override
    public Integer convertToDatabaseColumn(LocalDate date) {
        int dbDate;
        if (date == null || date.equals(DateSupport.MIN_DATE) || date.isBefore(DateSupport.MIN_DATE)) {
            dbDate = 0;
        } else if (date.equals(DateSupport.MAX_DATE) || date.isAfter(DateSupport.MAX_DATE)) {
            dbDate = maxDate;
        } else {
            dbDate = DateUtil.localDateToInt(date);
        }
        return dbDate;
    }

    @Override
    public LocalDate convertToEntityAttribute(Integer dbDate) {
        if (dbDate == null || dbDate == 0 || dbDate == -1316124811) {
            return DateSupport.MIN_DATE;
        } else if (dbDate >= maxDate) {
            return DateSupport.MAX_DATE;
        }
        return DateUtil.intTolocalDate(dbDate);
    }

}
