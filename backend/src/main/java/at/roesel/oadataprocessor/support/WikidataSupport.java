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

import at.roesel.oadataprocessor.model.wikidata.WikidataValue;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static at.roesel.common.CollectionSupport.collectionToString;

public class WikidataSupport {
    public static String extractWikiId(String wikiDataEntity) {
        if (wikiDataEntity == null) {
            return "";
        }
        int pos = wikiDataEntity.indexOf("/Q");
        if (pos > -1) {
            return wikiDataEntity.substring(pos+1);
        } else {
            // No wikidataId, but something like, for example http://www.wikidata.org/.well-known/genid/3534da1549d68d4a3cab62f2e05e6552
            return "";
        }
    }

    public static boolean isWikiId(String name) {
        if (name == null || name.length() < 2) {
            return false;
        }
        return name.matches("^[Qq][0-9]+$");
    }

    /*
     * @return name if it is a valid WikidataId, otherwise an empty string
     */
    public static String wikiIdOrEmpty(String name) {
        if (isWikiId(name)) {
            return name;
        }
        return "";
    }

    public static LocalDate localDateFrom(String wikTime) {

        int[] values = {0, 0, 0};
        StringBuilder part = new StringBuilder();
        int idx = 0;
        for (int i = 0; i < wikTime.length(); i++) {
            char ch = wikTime.charAt(i);
            if (ch == '+') {
                continue;
            } else if (ch == '-' || ch == 'T') {
                int value = Integer.parseInt(part.toString());
                values[idx] = value;
                idx++;
                part = new StringBuilder();
                if (ch == 'T') {
                    break;
                }
                continue;
            }
            part.append(ch);
        }
        if (values[1] == 0) {
            values[1] = 1;
        }
        if (values[2] == 0) {
            values[2] = 1;
        }
        LocalDate localDate = LocalDate.of(values[0], values[1], values[2]);
        return localDate;
    }

    public static String firstValueOrEmpty(List<WikidataValue> entries) {
        if (entries == null || entries.isEmpty()) {
            return "";
        }

        return entries.get(0).value;
    }

    public static String stringFromValues(List<WikidataValue> entries) {
        if (entries == null || entries.isEmpty()) {
            return "";
        }

        return collectionToString(entries.stream().map(value -> value.value).toList(), ",");
    }

    public static String stringFromValues(List<WikidataValue> entries, Function<String, String> transformer) {
        if (entries == null || entries.isEmpty()) {
            return "";
        }

        return collectionToString(entries.stream().map(value -> transformer.apply(value.value)).toList(), ",");
    }

    public static String normalizeIsni(String isni) {
        if (isni == null || isni.isEmpty()) {
            return "";
        }

        return isni.replaceAll(" ", "");
    }

}
