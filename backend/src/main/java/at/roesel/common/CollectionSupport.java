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

import java.util.*;
import java.util.function.Function;

public class CollectionSupport {

    public static <T> boolean isEqualCollection(final Collection<T> a, final Collection<T> b, Comparator<T> comparator) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            return false;
        }

        for (T element : a) {
            if (find(b, element, comparator) == null) {
                return false;
            }
        }

        return true;
    }

    public static <T> T find(Collection<T> coll, T findElement, Comparator<T> comparator) {
        for (T element : coll) {
            if (comparator.compare(element, findElement) == 0) {
                return element;
            }
        }
        return null;
    }

    public static <T> String collectionToString(Collection<T> coll, String separator) {
        return collectionToString(coll, separator, Object::toString);
    }

    public static <T> String collectionToString(Collection<T> coll, String separator, Function<T,String> formatter) {
        if (coll == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T element : coll) {
            if (!first) {
                sb.append(separator);
            } else {
                first = false;
            }
            if (element != null) {
                sb.append(formatter.apply(element));
            }
        }
        return sb.toString();
    }

    public static <T> T firstElementOf(Set<T> set) {
        List<T> list = new ArrayList<T>(set);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}