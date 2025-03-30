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

package at.roesel.oadataprocessor.services.publicationsource;

import at.roesel.oadataprocessor.model.json.JsonAuthor;

import java.util.ArrayList;
import java.util.List;

public class BokuNameParser {

    public static List<JsonAuthor> authorsFromString(String text) {
        List<JsonAuthor> authors = new ArrayList<>();
        String[] names = null;
        text = text.trim();
        if (text.contains(";")) {
            names = text.split(";");
        } else if (text.contains("\n")) {
            names = text.split("\n");
        } else {
            // Schwen A, Hernandez-Ramirez G, Buchan G, Carrick S
            // Hauer, C., Rudolf-Miklau, F., Suda, J., Brandl, H., Blovsky, S., Hübl, J., Holub, M., Habersack, H.
            /*
            for (int i = 0; i < text.length(); i++) {

            }
             */
            String[] parts = text.split(",");
            List<String> nameParts = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                String name = parts[i].trim();
                if (!name.contains(" ")) {
                    // nächsten Teil dazunehmen
                    if (i+1 < parts.length) {
                        String given = parts[i + 1];
                        // Rametsteiner, E., Bauer, Weiss, G.
                        if (given.length() < 3 || given.contains(".")) {
                            name += "," + given;
                            i++;
                        }
                    }
                }
                nameParts.add(name);
            }
            names = new String[nameParts.size()];
            nameParts.toArray(names);
        }
        if (names != null) {
            for (String str : names) {
                JsonAuthor author = authorFromString(str.trim());
                if (author != null) {
                    authors.add(author);
                }
            }
        }
        return authors;
    }

    public static JsonAuthor authorFromString(String name) {
        if (name.isEmpty()) {
            return null;
        }
        JsonAuthor author = new JsonAuthor();
        String[] nameParts = name.split(",");
        if (nameParts.length > 1) {
            author.setFamily(nameParts[0].trim());
            author.setGiven(nameParts[1].trim());
        } else {
            nameParts = name.split(" ");
            if (nameParts.length > 1) {
                String first = nameParts[0].trim();
                String second = nameParts[1].trim();
                if (second.length() < 3 || second.contains(".")) {
                    author.setFamily(first);
                    author.setGiven(second);
                } else {
                    author.setFamily(second);
                    author.setGiven(first);
                }
            } else {
                author.setFamily(nameParts[0].trim());
            }
        }
        return author;
    }

}
