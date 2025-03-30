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

import at.roesel.oadataprocessor.model.ClassificationData;
import at.roesel.oadataprocessor.model.sherpa.PermittedOa;
import at.roesel.oadataprocessor.model.sherpa.PublisherPolicy;
import at.roesel.oadataprocessor.model.sherpa.Romeo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SherpaRomeoSupport {

    private static final String ACCEPTED = "accepted";
    private static final String PUBLISHED = "published";
    private static final int MAX_TIME = 99999;

    private final static Logger logger = LoggerFactory.getLogger(SherpaRomeoSupport.class);

    /*
       Algorithm by Patrick Danowsky
       Paid options ("additional_oa_fee": "yes") and all options with prerequisites are not considered.
       - If a case with the published version remains, save the embargo.
       - If a case with the accepted version remains, save the embargo.
       - The shorter embargo is returned.

       @return: Embargo time in months
     */
    public static int findEmbargoTime(Romeo romeo) {
        int minEmbargoAccepted = MAX_TIME;
        int minEmbargoPublished = MAX_TIME;
        if (romeo.publisherPolicy != null) {
            for (PublisherPolicy policy : romeo.publisherPolicy) {
                if (policy.permittedOa != null) {
                    for (PermittedOa permittedOa : policy.permittedOa) {
                        if (permittedOa.hasAdditionalOaFee() ||
                                permittedOa.hasPrerequisites()) {
                            continue;
                        }

                        if (permittedOa.hasArticleVersion(ACCEPTED)) {
                            int result = findMinEmbargo(romeo, permittedOa, minEmbargoAccepted);
                            if (result != ClassificationData.EMBARGO_UNKNOWN) {
                                minEmbargoAccepted = result;
                            }
                        } else if (permittedOa.hasArticleVersion(PUBLISHED)) {
                            int result = findMinEmbargo(romeo, permittedOa, minEmbargoPublished);
                            if (result != ClassificationData.EMBARGO_UNKNOWN) {
                                minEmbargoPublished = result;
                            }
                        }
                    }
                }
            }
        }

        int embargo = Math.min(minEmbargoAccepted, minEmbargoPublished);
        if (embargo == MAX_TIME) {
            embargo =  ClassificationData.EMBARGO_UNKNOWN;
        }

        return embargo;
    }

    private static int findMinEmbargo(Romeo romeo, PermittedOa permittedOa, int minEmbargo) {
        if (permittedOa.embargo == null) {
            return ClassificationData.EMBARGO_UNKNOWN;
        }

        if (permittedOa.embargo.units == null) {
            if (permittedOa.embargo.amount != 0) {
                logger.error("SherpaRomeo data for {} has no unit, but embargo time {}", romeo.issns, permittedOa.embargo.amount);
            }
            return 0;
        }

        int months = permittedOa.embargo.amount;
        String units = permittedOa.embargo.units;
        if (!units.equals("months")) {
            if (units.equals("years")) {
                // logger.debug(String.format("SherpaRomeo data for %s has an embargo time %d with units %s", romeo.issns, months, units));
                months *= 12;
            } else if (units.equals("days")) {
                if (months != 0) {
                    logger.debug(String.format("SherpaRomeo data for %s has an embargo time %d with units %s", romeo.issns, months, units));
                }
                months = months / 30;
            } else {
                logger.error("SherpaRomeo data for {} has an embargo time with unhandled units {}", romeo.issns, units);
            }
        }

        int embargoTime = months;
        return Math.min(embargoTime, minEmbargo);
    }
}
