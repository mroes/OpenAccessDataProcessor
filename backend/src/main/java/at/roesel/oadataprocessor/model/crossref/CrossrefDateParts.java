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

package at.roesel.oadataprocessor.model.crossref;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CrossrefDateParts {
    @JsonProperty("date-parts")
    public List<List<Integer>> dateParts;

    @JsonIgnore
    public int year() {
        int result = 0;
        if (dateParts != null) {
            return dateParts.get(0).get(0);
        }
        return result;
    }

    @JsonIgnore
    public String date() {
        StringBuilder sb = new StringBuilder();
        if (dateParts != null && !dateParts.isEmpty()) {
            List<Integer> firstPart = dateParts.get(0);
            int size = firstPart.size();
            if (size > 0) {
                sb.append(firstPart.get(0));
            }
            if (size > 1) {
                sb.append("-");
                int month = firstPart.get(1);
                if (month < 10) {
                    sb.append(0);
                }
                sb.append(month);
            }
            if (size > 2) {
                sb.append("-");
                int day = firstPart.get(2);
                if (day < 10) {
                    sb.append(0);
                }
                sb.append(day);
            }
        }
        return sb.toString();
    }

}
