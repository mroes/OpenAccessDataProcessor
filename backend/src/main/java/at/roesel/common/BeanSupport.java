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

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;

public class BeanSupport {

    /*
     * builds a String of the values, mapper maps the value to a string representation
     */
    public static <T> String buildString(List<T> values, Function<T,String> mapper) {
        StringBuilder builder = new StringBuilder();
        if (values != null) {
            int count = 0;
            for (T value : values) {
                if (count > 0) {
                    builder.append(", ");
                }
                count++;
                String representation = mapper.apply(value);
                if (representation != null && !representation.isEmpty()) {
                    builder.append(representation);
                }
            }
        }
        return builder.toString();
    }

    /*
     * @return true if value was changed
     */
    public static boolean addNewValue(Object bean, String fieldName, String newValue, String delimiter) {
        if (newValue == null || newValue.isEmpty()) {
            return false;
        }
        String value = getProperty(bean, fieldName);
        if (value == null || value.isEmpty()) {
            setProperty(bean, fieldName, newValue);
            return true;
        } else if (value.contains(newValue)) {
            return false;
        }
        else {
            setProperty(bean, fieldName, value + delimiter + newValue);
            return true;
        }
    }

    private static String getProperty(Object bean, String fieldName) {
        String value;
        try {
            value = (String)PropertyUtils.getProperty(bean, fieldName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    private static void setProperty(Object bean, String fieldName, String value) {
        try {
            PropertyUtils.setProperty(bean, fieldName, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
