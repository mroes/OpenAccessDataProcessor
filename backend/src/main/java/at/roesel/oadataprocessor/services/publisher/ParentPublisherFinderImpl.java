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

package at.roesel.oadataprocessor.services.publisher;

import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.PublisherVar;

import java.time.LocalDate;
import java.util.Set;

public class ParentPublisherFinderImpl implements ParentPublisherFinder {

    /*
     * looks up the parent publisher of the given publisher
     *
     * @param publisher the publisher for which the parent is looked up
     * @param year      the year for which the parent is looked up
     * @return          the ID of the parent publisher or null if there is no parent
     */
    @Override
    public String findParent(Publisher publisher, int year) {
        if (publisher.isTopPublisher()) {
            return null;
        }
        Set<PublisherVar> variables = publisher.getVariable();
        // if there is no variable data available then use the directly set parent publisher
        if (variables == null || variables.isEmpty()) {
            String parentId = publisher.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                return null;
            }
            return parentId;
        }
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        for (PublisherVar var : variables) {
            String parentId = var.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                continue;
            }
            if (!var.getStartDate().isAfter(endDate) && !var.getEndDate().isBefore(startDate)) {
                return parentId;
            }

        }
        return null;
    }
}
