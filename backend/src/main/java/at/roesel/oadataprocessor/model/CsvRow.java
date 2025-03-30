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

package at.roesel.oadataprocessor.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class CsvRow extends HashMap<String, String> {

    private final Logger logger = LoggerFactory.getLogger(CsvRow.class);

    public CsvRow() {
        super();
    }

    public void setField(String field, String value) {
        put(field, value);
    }

    public String getField(String field) {
        return get(field);
    }

    public Integer getInteger(String field) {
        String value = get(field);
        if (value != null && !value.isEmpty() ) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return 0;
    }

    public Long getLong(String field) {
        String value = get(field);
        if (value != null && !value.isEmpty() ) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return 0L;
    }

    // Abfrage für boolean Felder
    public boolean getBooleanField(String field) {
        String fieldValue = getField(field);
        if (fieldValue != null) {
            switch (fieldValue) {
                case "Yes" :    // DOAJ
                case "TRUE" :   // APC
                    return true;
                case "No" :     // APC
                    return false;
            }
        }
        return false;
    }

    public boolean fieldContains(String field, String value) {
        String fieldValue = getField(field);
        if (fieldValue != null) {
            if (fieldValue.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
