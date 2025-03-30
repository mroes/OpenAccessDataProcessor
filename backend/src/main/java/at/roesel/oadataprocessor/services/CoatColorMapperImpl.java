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

package at.roesel.oadataprocessor.services;

import at.roesel.oadataprocessor.model.Coat;

import java.util.Arrays;
import java.util.List;

public class CoatColorMapperImpl implements CoatColorMapper {

    private final static List<Coat> oaColors = Arrays.asList(
            // Color mapping provided by Patrick Danowski, 17.8.2022
            new Coat("Diamant", 1, 1, 1, 1, 1),
            new Coat("Gold-pur", 1, 2, 1, 1, 2),
            new Coat("Hybrid", 1, 2, 1, 1, 3),
            new Coat("Bronze", 1, 4, 1, 4, 3),
            new Coat("Green-Post", 2, 4, 2, 4, 1),
            new Coat("Green-Pre", 2, 4, 4, 4, 1),
            new Coat("Other OA", 3, 4, 4, 4, 3),
            new Coat("Closed", 4, 4, 4, 4, 4)
    );

    @Override
    public String mapColor(Coat coat) {
        String result = "unknown";

        if (coat.equals(Coat.errorCoat)) {
            return result;
        }

        for (Coat coatColor : oaColors) {
            if (coat.isLower(coatColor)) {
                return coatColor.getName();
            }
        }

        return result;
    }

}
