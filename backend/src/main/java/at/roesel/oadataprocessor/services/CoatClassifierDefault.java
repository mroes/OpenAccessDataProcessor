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

import at.roesel.oadataprocessor.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    COAT classification of a publication.
    https://doi.org/10.31263/voebm.v72i1.2276

    Danowski, P. (2019) „Ein österreichischer Vorschlag zur Klassifizierung von Open Access-Tupeln (COAT) -
    Unterscheiden verschiedener Open Access-Typen jenseits von Farben“,
    Mitteilungen der Vereinigung Österreichischer Bibliothekarinnen und Bibliothekare, 72(1), S. 59–65.
    doi: 10.31263/voebm.v72i1.2276.

    Original prototype for open access classification
    https://github.com/patrickda/COAT/blob/master/Version%200.5/COAT%20Classification.R
 */

public class CoatClassifierDefault implements CoatClassifier {

    private final Logger logger = LoggerFactory.getLogger(CoatClassifierDefault.class);

    public Coat classify(ClassificationData data) {
        logger.debug(String.format("classify doi='%s'", data.doi));
        if (data.getSources() == 0) {
            // No data available for classification
            logger.debug("classify: no sources available");
            return Coat.errorCoat;
        }

        Coat coat = new Coat();
        coat.setSources(data.getSources());

        // version
        // Must be called before `classifyPlace()`, as `classifyPlace` may use the version.
        classifyVersion(data, coat);

        // place
        classifyPlace(data, coat);

        // licence
        classifyLicence(data, coat);

        // embargo
        classifyEmbargo(data, coat);

        // conditions
        classifyConditions(data, coat);

        return coat;
    }

    public int classifyEmbargo(ClassificationData data, Coat coat) {
        int embargo_time = ClassificationData.EMBARGO_UNKNOWN;
        LicenceInfo bestLicence = data.getBestLicence();
        String source = "<default>";
        if (bestLicence != null && bestLicence.licenceType != null && bestLicence.licenceType.isBetter(LicenceType.ProprietaryLicence)) {
            embargo_time = 0;
            source = "free/open licence " + bestLicence.licenceSource;
        } else if (data.inOpenApc) {
            embargo_time = 0;
            source = "inOpenApc";
        } else if (data.inDoaj) {
            embargo_time = 0;
            source = "inDoaj";
        } else {
            // Embargo von Sherpa ?
            if (data.embargoRomeo != ClassificationData.EMBARGO_UNKNOWN) {
                embargo_time = data.embargoRomeo;
                source = "SherpaRomeo";
            }
        }

        if (embargo_time == ClassificationData.EMBARGO_UNKNOWN) {
            coat.setEmbargo(4, "Embargo unbekannt");
        } else if (embargo_time == 0) {
            coat.setEmbargo(1, source);
        } else if (embargo_time < 6) {
            coat.setEmbargo(2, source);
        } else if (embargo_time < 12) {
            coat.setEmbargo(3, source);
        } else {
            coat.setEmbargo(4, source);
        }

        return embargo_time;
    }


    private void classifyPlace(ClassificationData data, Coat coat) {

        switch (data.getHosttype()) {
            case "publisher": {
                coat.setPlace(1, "upw:hosttype = publisher");
                break;
            }
            case "repository": {
                coat.setPlace(2, "upw:hosttype = repository");
                break;
            }
            default: {
                coat.setPlace(4, "<default>");
            }
        }
        if (coat.getPlace() > 1) {
            if (data.inOpenApc) {
                coat.setPlace(1, "inOpenApc");
            } else if (data.inDoaj) {
                coat.setPlace(1, "inDoaj");
            }
        }
    }

    private void classifyVersion(ClassificationData data, Coat coat) {
        switch (data.getHosttype()) {
            case "publisher": {
                coat.setVersion(1, "upw:hosttype = publisher");
                break;
            }
        }
        switch (data.getOaversion()) {
            case "publishedVersion": {
                coat.setVersion(1, "upw:oaversion = publishedVersion");
                break;
            }
            case "acceptedVersion": {
                coat.setVersion(2, "upw:oaversion = acceptedVersion");
                break;
            }
            case "submittedVersion": {
                coat.setVersion(3, "upw:oaversion = submittedVersion");
                break;
            }
        }

        if (data.inDoaj) {
            coat.setVersion(1, "inDoaj");
        } else if (data.inOpenApc) {
            coat.setVersion(1, "inOpenApc");
        }
    }

    public void classifyLicence(ClassificationData data, Coat coat) {
        LicenceInfo licenceInfo = data.getBestLicence();
        if (licenceInfo != null) {
            LicenceType lt = licenceInfo.licenceType;
            if (lt != null) {
                coat.setLicence(lt.code, licenceInfo.licenceSource);
            }
        }
    }

    private void classifyConditions(ClassificationData data, Coat coat) {
        data.addExplanation("*** Klassifizierung der Konditionen");
        boolean hybrid = false;
        if (data.getOpenapcHybrid() != null) {
            hybrid = data.getOpenapcHybrid();
            data.addExplanation(String.format("hybrid = %b (Quelle OpenApc)", hybrid));
        } else {
            // Hybrid, even if not in DOAJ and there is an open or free license.
            if (!data.inDoaj
                    && (coat.getLicence() == 1 || coat.getLicence() == 2)) {
                hybrid = true;
                data.addExplanation(String.format("hybrid = %b (nicht in DOAJ und offene/freie Lizenz)", hybrid));
            } else {
                data.addExplanation(String.format("hybrid = %b (in DOAJ oder keine offene/freie Lizenz)", hybrid));
            }
        }

        Amount apc = data.apc();

        if (apc != null && apc.value == 0) {
            coat.setConditions(1, "apc = 0");
        } else if (apc != null && apc.value > 0 && !hybrid) {
            coat.setConditions(2, "apc > 0 && !hybrid");
        } else if (apc != null && apc.value > 0 && hybrid) {
            coat.setConditions(3, "apc > 0 && hybrid");
        } else if (coat.getPlace() == 2) {
            coat.setConditions(1, "coat.place = 2");
        } else if (coat.getVersion() != 1 && coat.getVersion() != 4) {
            coat.setConditions(1, "coat.version != 1 && coat.version != 4");
        } else if (coat.getVersion() < 4) {
            coat.setConditions(3, "coat.version < 4");
        } else {
            coat.setConditions(4, "<default>");
        }
    }

}

