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

import at.roesel.oadataprocessor.model.crossref.CrossrefInfo;
import at.roesel.oadataprocessor.model.pure.PureResearchInfo;
import at.roesel.oadataprocessor.model.unpaywall.UnpaywallInfo;
import org.apache.commons.beanutils.NestedNullException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.beanutils.PropertyUtils.getNestedProperty;

public class PublicationInfo {
    public PureResearchInfo pure;
    public CrossrefInfo cr;
    public UnpaywallInfo upw;

    public static List<String> fields() {
        List<String> result = new ArrayList<>();
        result.addAll(fieldNamesForClass("pure",  PureResearchInfo.class));
        result.addAll(fieldNamesForClass("cr",  CrossrefInfo.class));
        result.addAll(fieldNamesForClass("upw",  UnpaywallInfo.class));
        return result;
    }

    public static List<String> fieldNamesForClass(String name, Class<?> clazz) {
        Field[] allFields = clazz.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field field : allFields) {
            result.add(name + "." + field.getName());
        }
        return result;
    }

    public List<String> rows(List<String> fieldNames) {
        List<String> rows = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String value;
            try {
                Object fieldValue = getNestedProperty(this, fieldName);
                if (fieldValue != null) {
                    value = fieldValue.toString();
                } else {
                    value = "";
                }
            } catch (NestedNullException e) {
                value = "";
            } catch (Exception e) {
                value = "!error!";
            }
            rows.add(value);
        }
        return rows;
    }

    public PureResearchInfo getPure() {
        return pure;
    }

    public CrossrefInfo getCr() {
        return cr;
    }

    public UnpaywallInfo getUpw() {
        return upw;
    }
}
