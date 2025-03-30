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

package at.roesel.oadataprocessor.services.oaipmh;

import at.roesel.oadataprocessor.model.oaipmh.jabx.HeaderType;
import at.roesel.oadataprocessor.model.oaipmh.jabx.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractOaiPmhResultResponseHandler implements OaiPmhResultResponseHandler {

    private final Logger logger = LoggerFactory.getLogger(AbstractOaiPmhResultResponseHandler.class);

    private int count = 0;
    private int limit = 0;  // Maximum number of queried records

    public int getCount() {
        return count;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public final boolean handleResponse(List<RecordType> records) {
        boolean stop = false;
        for (RecordType recordType : records) {
            try {
                HeaderType header = recordType.getHeader();
                if (header == null) {
                    logger.warn("OaiPmhRecord without Header {}", recordType);
                    continue;
                }
                if (header.isDeleted()) {
                    // deleted record
                    // logger.debug("OaiPmhRecord deleted Record " + recordType);
                    continue;
                }

                // For safety, we check if there are metadata for this record
                // This should actually be redundant with the above check to see if the data has been deleted
                if (recordType.getMetadata() == null) {
                    logger.warn("OaiPmhRecord without Metadata {}", recordType);
                    continue;
                }

                stop = handleRecordType(recordType);
                if (stop) {
                    break;
                }
                count++;
                if (limit > 0 && count >= limit) {
                    stop = true;
                    break;
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return stop;
    }

    /*
     Derived classes implement their actual functionality here
     */
    protected abstract boolean handleRecordType(RecordType recordType);

}
