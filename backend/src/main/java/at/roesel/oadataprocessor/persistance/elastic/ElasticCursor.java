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

package at.roesel.oadataprocessor.persistance.elastic;

import co.elastic.clients.elasticsearch._types.FieldValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ElasticCursor {

    // preference für ElasticSearch, damit alle Queries für Konsistenz am gleichen Shard erfolgen
    // https://www.elastic.co/guide/en/elasticsearch/reference/current/consistent-scoring.html
    final private String preference;
    private List<FieldValue> search_afterValues;

    public ElasticCursor() {
        preference = UUID.randomUUID().toString();
    }

    public ElasticCursor(String preference) {
        this.preference = preference;
    }

    public String getPreference() {
        return preference;
    }

    public List<FieldValue> getSearch_afterValues() {
        return search_afterValues;
    }

    public void setSearch_afterValues(List<FieldValue> search_afterValues) {
        this.search_afterValues = search_afterValues;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(preference);
        sb.append(":");
        if (search_afterValues != null && !search_afterValues.isEmpty()) {
            Long index = search_afterValues.get(0).longValue();
            sb.append(index);
        }
        return sb.toString();
    }

    public static ElasticCursor fromString(String strCursor) {
        String[] parts = strCursor.split(":");
        if (parts.length != 2) {
            throw new InvalidCursorException();
        }
        try {
            ElasticCursor cursor = new ElasticCursor(parts[0]);
            FieldValue fieldValue = FieldValue.of(Long.parseLong(parts[1]));
            List<FieldValue> search_afterValues = new ArrayList<>();
            search_afterValues.add(fieldValue);
            cursor.setSearch_afterValues(search_afterValues);
            return cursor;
        } catch (NumberFormatException e) {
            throw new InvalidCursorException();
        }
    }
}
