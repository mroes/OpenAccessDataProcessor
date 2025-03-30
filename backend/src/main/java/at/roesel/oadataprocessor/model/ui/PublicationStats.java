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

package at.roesel.oadataprocessor.model.ui;

import java.util.*;

public class PublicationStats {

        // Publikationen pro Farbe und Institut
    private final List<PublicationColor> numbersPerYearAndInstitution = new ArrayList<>();

    // Publikationen pro Farbe und Jahr
    private final List<PublicationColor> numbersPerYear = new ArrayList<>();

    public List<PublicationColor> getNumbersPerYearAndInstitution() {
        return numbersPerYearAndInstitution;
    }

    public List<PublicationColor> getNumbersPerYear() {
        return numbersPerYear;
    }


    /*
    // Berechnung am Client
    public int getTotalCount() {
        int count = 0;
        for (PublicationColor color : numbersPerYear) {
            count += color.value;
        }
        return count;
    }

    // Berechnung am Client
    public Collection<PublicationColor> getTotalNumbers() {
        Map<String, PublicationColor> map = new TreeMap<>((c1, c2) ->
                Integer.compare(index(c1), index(c2))
        );
        for (PublicationColor color : numbersPerYear) {
            PublicationColor pc = map.computeIfAbsent(color.name(), k -> new PublicationColor(color.name(), 0) );
            pc.value += color.value;
        }
        return map.values();
    }

     */

    public void setFrom(PublicationStats newStats) {
        setNumbersPerYear(newStats.getNumbersPerYear());
        setNumbersPerYearAndInstitution(newStats.getNumbersPerYearAndInstitution());
    }

    public void setNumbersPerYearAndInstitution(List<PublicationColor> newNumbers) {
        numbersPerYearAndInstitution.clear();
        numbersPerYearAndInstitution.addAll(newNumbers);
    }

    public void setNumbersPerYear(List<PublicationColor> newNumbers) {
        numbersPerYear.clear();
        numbersPerYear.addAll(newNumbers);
    }


    // Farben error und Unknown werden kombiniert
    public void combineUnknownAndAdd(List<PublicationColor> numbers, List<PublicationColor> newNumbers) {
        Map<String, PublicationColor> result = new HashMap<>();
        for (PublicationColor color : newNumbers) {
            String name = color.name();
            color.setName(name);
            PublicationColor existingColor = result.computeIfAbsent(name, (k) -> new PublicationColor());
            existingColor.value += color.value;
        }

        numbers.addAll(result.values());
    }

//    private static final List<String> sortColor = Arrays.asList("Diamant", "Gold-pur", "Hybrid", "Bronze", "Green-Post", "Green-Pre", "Closed", "error");

    private int index(String name) {
        int index = 999;
        /*
        for (int i = 0; i < oaColors.size(); i++) {
            if (oaColors.get(i).getName().equals(name)) {
                return i;
            }
        }

         */
        return index;
    }

    public void sort() {
        /*
        Comparator<PublicationColor> comparator = (c1, c2) -> {
            // quickfix für null-Datensätze
            if (c1 == null || c2 == null) {
                return 0;
            }
            int result = Integer.compare(index(c1.name), index(c2.name));
            if (result == 0) {
                result = Integer.compare(c1.year, c2.year);
            }
            return result;
        };

        // Sortieren nach Farbe und Jahr, damit der Chart Publikation/Jahr im UI sofort aufgebaut werden kann
        numbersPerYearAndInstitution.sort(comparator);
        numbersPerYear.sort(comparator);

         */
    }

    /*
    // summiert alle Zahlen pro Farbe auf
    public void calcTotalNumbers() {
        Map<String, Integer> colorSum = new HashMap<>();
        if (oaColors != null) {
            // wir wollen auch einen Datensatz, wenn eine Farbe nicht vorhanden ist
            oaColors.forEach( oaColor -> {
                colorSum.put(oaColor.getName(), 0);
            });
        }
        for (PublicationColor color : numbers) {
            String name = color.name();
            color.setName(name);
            int count = colorSum.getOrDefault(name, 0);
            count += color.value;
            colorSum.put(name, count);
        }
        numbersPerYear.clear();
        for (Map.Entry<String, Integer> entry : colorSum.entrySet()) {
            PublicationColor color = new PublicationColor(entry.getKey(), entry.getValue());
            numbersPerYear.add(color);
        }
    }

     */
}
