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

package at.roesel.oadataprocessor.services.sherpa;

import at.roesel.oadataprocessor.model.sherpa.Romeo;
import at.roesel.oadataprocessor.model.sherpa.RomeoPublisher;
import at.roesel.oadataprocessor.model.sherpa.SherpaObjectResponse;

import java.util.List;
import java.util.function.Consumer;

/*
    Sherpa APIs
    https://v2.sherpa.ac.uk/api/
 */
public interface SherpaService {
    // Fetches the data from SherpaRomeo
    SherpaObjectResponse objectByID(String issn);

    // Checks if an entry exists in the database
    // If yes, the entry is returned
    // If no, the entry is fetched from Sherpa and saved
    Romeo romeoForIssn(String issn);

    /* romeoForIssn for multiple ISSNs */
    Romeo romeoForIssns(List<String> issns);

    void fetchPublishers(Consumer<RomeoPublisher> visitor);

}
