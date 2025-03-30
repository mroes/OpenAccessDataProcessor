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

package at.roesel.oadataprocessor.services.common;

import at.roesel.oadataprocessor.model.PublicationSource;

import java.util.ArrayList;
import java.util.List;

public class FetchResult {

    private boolean ok;
    private long importedRecords;
    private long createdRecords;
    private long modifiedRecords;
    private long unchangedRecords;

    private long ignoredRecords; // e.g. records without a native ID

    private List<PublicationSource> publicationSources;

    public FetchResult() {
        this(0, 0, 0, 0, 0);
    }

    public FetchResult(long importedRecords, long createdRecords, long modifiedRecords, long unchangedRecords, long ignoredRecords) {
        this.importedRecords = importedRecords;
        this.createdRecords = createdRecords;
        this.modifiedRecords = modifiedRecords;
        this.unchangedRecords = unchangedRecords;
        this.ignoredRecords = ignoredRecords;
        publicationSources = new ArrayList<>();
    }

    public void incImportedRecords() {
        importedRecords++;
    }

    public void incCreatedRecords() {
        createdRecords++;
    }

    public void incModifiedRecords() {
        modifiedRecords++;
    }

    public void incUnchangedRecords() {
        unchangedRecords++;
    }

    public void incIgnoredRecords() {
        ignoredRecords++;
    }

    public String asText() {
        return String.format("imported publication sources: %d, created: %d, modified: %d, unchanged: %d, ignored: %d",
                importedRecords, createdRecords, modifiedRecords, unchangedRecords, ignoredRecords);
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public long getImportedRecords() {
        return importedRecords;
    }

    public long getCreatedRecords() {
        return createdRecords;
    }

    public long getModifiedRecords() {
        return modifiedRecords;
    }

    public long getUnchangedRecords() {
        return unchangedRecords;
    }

    public long getIgnoredRecords() {
        return ignoredRecords;
    }

    public List<PublicationSource> getPublicationSources() {
        return publicationSources;
    }

    public void addPublicationsSources(PublicationSource publicationsSource) {
        this.publicationSources.add(publicationsSource);
    }

    public void limitPreviewPublications(int limit) {
        if (publicationSources.size() > limit) {
            publicationSources = publicationSources.subList(0, limit);
        }
    }
}
