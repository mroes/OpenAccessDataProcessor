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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringSupport {

    private final static List<String> stopwords;

    // https://raw.githubusercontent.com/stopwords-iso/stopwords-de/master/stopwords-de.txt
    // https://gist.github.com/sebleier/554280
    static {
        try {
            URL resource = StringSupport.class.getClassLoader().getResource("stopwords.txt");
            stopwords = Files.readAllLines(Paths.get(resource.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }

    public static <T> boolean hasValue(Collection<T> coll) {
        return coll != null && !coll.isEmpty();
    }

    public static String removeStopWords(String text) {
        List<String> allWords =
                Stream.of(text.toLowerCase().split(" "))
                        .collect(Collectors.toCollection(ArrayList<String>::new));
        allWords.removeAll(stopwords);

        String result = String.join(" ", allWords);
        return result;
    }

    public static String stripSpecialCharacters(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder sb = new StringBuilder();
        boolean inToken = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '&') {
                i++;
                if (i < text.length()) {
                    char ch2 = text.charAt(i);
                    if (ch2 == '#') {
                        inToken = true;
                        continue;
                    }
                }
                continue;
            }
            if (inToken) {
                if (ch == ';') {
                    inToken = false;
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static String[] str2array(String value, String separator) {
        if (value == null || value.isEmpty()) {
            return new String[]{};
        }
        String[] values = value.split(Pattern.quote(separator));
        return values;
    }

}