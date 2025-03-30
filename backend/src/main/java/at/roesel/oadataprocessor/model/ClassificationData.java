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

package at.roesel.oadataprocessor.model;

import at.roesel.oadataprocessor.model.crossref.CrossrefLicence;
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;
import at.roesel.oadataprocessor.model.doaj.DoajJournal;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.model.openapc.OpenApcRecord;
import at.roesel.oadataprocessor.model.sherpa.Romeo;
import at.roesel.oadataprocessor.model.unpaywall.UnpaywallResource;

import java.util.ArrayList;
import java.util.List;

import static at.roesel.oadataprocessor.model.Licences.getLicenceType;
import static at.roesel.oadataprocessor.services.sherpa.SherpaRomeoSupport.findEmbargoTime;

/*
 * Gathering information for a publication needed for classification.
 */
public class ClassificationData {

    private boolean explain;
    private Explanation explanation;

    public String doi;

    // publishing date from Crossref
    public int publishedDate;

    public boolean free = false;
    public Boolean openapcHybrid = null;

    public CrossrefLicence crossrefLicence;
    public String crossrefLicenceUrl;
    public LicenceType crossrefLicenceType;
    public int publicationYear;

    // licence of journal from DOAJ
    public String doajLicence;

    // licence of publication from OpenAlex
    public String openAlexLicence;

    public LicenceInfo bestLicence; // The best-found license

    public String hosttype; // from Unpaywall
    public String oaurl;    // from Unpaywall
    public String oaversion; // from Unpaywall

    public String colorUpw; // colour from Unpaywall

    public boolean inDoaj;   // journal is listed in Doaj
    public boolean inOpenApc;   // publication has an OpenApc record

    // APC in EUR Cent
    public Amount doajApc;
    // APC in EUR from OpenApc
    public long openApcAmount;

    // Embargo from Sherpa Romeo
    public static int EMBARGO_UNKNOWN = -1;  // no Embargo found
    public int embargoRomeo = EMBARGO_UNKNOWN;

    // IDs for sources
    public static final int SOURCE_CR = 1;
    public static final int SOURCE_UNPAYWALL = 2;
    public static final int SOURCE_DOAJ = 4;
    public static final int SOURCE_APC = 8;
    public static final int SOURCE_ROMEO = 16;
    public int sources;

    public ClassificationData(String doi) {
        this.doi = doi;
        sources = 0;
    }


    // Must be called after all data has been set.
    public void prepare() {
        bestLicence = buildLicenceInfo();
    }

    private void addSource(int sourceId) {
        sources |= sourceId;
    }

    public int getSources() {
        return sources;
    }

    public int getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(int publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getHosttype() {
        if (hosttype == null) {
            return "";
        }
        return hosttype;
    }

    public String getOaurl() {
        if (oaurl == null) {
            return "";
        }
        return oaurl;
    }

    public String getOaversion() {
        if (oaversion == null) {
            return "";
        }
        return oaversion;
    }

    public Boolean getOpenapcHybrid() {
        return openapcHybrid;
    }

    public boolean isHybrid() {
        boolean hybrid = !inDoaj;
        if (openapcHybrid != null) {
            hybrid = openapcHybrid;
        }
        return hybrid;
    }

    public Amount apc() {
        if (inOpenApc && openApcAmount >= 0) {
            return new Amount(openApcAmount, "EUR");
        }

        if (doajApc != null) {
            return doajApc;
        }

        return null;
    }

    public LicenceInfo getBestLicence() {
        return bestLicence;
    }

    public void setValuesFromCrossref(CrossrefWork crossrefWork) {
        if (crossrefWork != null && crossrefWork.isValid()) {
            if (explain) {
                explanation.add("** DOI in Crossref gefunden.");
                explanation.crossref = crossrefWork;
            }
            addSource(SOURCE_CR);
            List<CrossrefLicence> licences = crossrefWork.license;
            if (licences != null) {
                LicenceType bestLicenceType = LicenceType.UnknownLicence;
                CrossrefLicence bestlicence = null;
                for (CrossrefLicence lic : licences) {
                    LicenceType lt = getLicenceType(lic.url);
                    if (lt.isBetter(bestLicenceType)) {
                        bestLicenceType = lt;
                        bestlicence = lic;
                    }
                }
                crossrefLicence = bestlicence;
                crossrefLicenceUrl = bestlicence.url;
                crossrefLicenceType = bestLicenceType;
                if (explain) {
                    explanation.add("crossrefLicenceUrl: " + bestlicence.url);
                    explanation.add("crossrefLicenceType: " + bestLicenceType);
                }
            }

            if (crossrefWork.publishedOnline != null) {
                publicationYear = crossrefWork.publishedOnline.year();
            } else if (crossrefWork.publishedPrint != null) {
                publicationYear = crossrefWork.publishedPrint.year();
            }
            publishedDate = crossrefWork.publishedDate();
            if (explain) {
                explanation.add("Publikationsjahr: " + publicationYear);
            }
        } else {
            if (explain) {
                explanation.add("** DOI nicht in Crossref gefunden.");
            }
        }
        explanation.add("");
    }

    public void setValuesFromUnpaywall(UnpaywallResource unpaywallResource) {
        if (unpaywallResource != null && unpaywallResource.isValid()) {
            if (explain) {
                explanation.add("** DOI in Unpawywall gefunden.");
                explanation.unpaywall = unpaywallResource;
            }
            addSource(SOURCE_UNPAYWALL);
            if (unpaywallResource.is_oa && unpaywallResource.best_oa_location != null) {
                // reads data from best OA location
                hosttype = unpaywallResource.best_oa_location.host_type;
                oaurl = unpaywallResource.best_oa_location.url;
                oaversion = unpaywallResource.best_oa_location.version;
            }
            colorUpw = unpaywallResource.oa_status;
            if (explain) {
                explanation.add("hosttype: " + hosttype);
                explanation.add("oaurl: " + oaurl);
                explanation.add("oaversion: " + oaversion);
            }
        } else {
            if (explain) {
                explanation.add("** DOI nicht in Unpawywall gefunden.");
            }
        }
        explanation.add("");
    }

    public void setValuesFromDoaj(DoajJournal doajRecord) {
        if (doajRecord != null) {
            int oaStart = doajRecord.getOaStart();
            if (oaStart == 0 || oaStart <= publicationYear) {
                addSource(SOURCE_DOAJ);
                inDoaj = true;
                doajLicence = doajRecord.getLicence();
                Amount apc = doajRecord.getApcAmount();
                if (apc != null) {
                    free = (apc.value == 0);
                }
                this.doajApc = apc;
                if (explain) {
                    explanation.add("** Issn(s) in Doaj gefunden.");
                    if (oaStart > 0) {
                        explanation.add("OA ab " + oaStart);
                    }
                    explanation.add("doajLicence: " + doajLicence);
                    if (apc != null) {
                        explanation.add("apc: " + apc.value + " " + apc.currency);
                    } else {
                        explanation.add("keine apc in doaj angegeben");
                    }
                    explanation.doaj = doajRecord;
                }
            } else {
                if (explain) {
                    explanation.doaj = doajRecord;
                    explanation.add(String.format("** Issn(s) in Doaj gefunden, aber Publikationsjahr %d liegt vor OA-Start %d",
                            publicationYear, oaStart));
                }
            }
        } else {
            if (explain) {
                explanation.add("** Issn(s) nicht in Doaj gefunden.");
            }
        }
        explanation.add("");
    }

    public void setValuesFromApc(OpenApcRecord record) {
        if (record != null) {
            addSource(SOURCE_APC);
            inOpenApc = true;
            free = false;
            openapcHybrid = record.isHybrid();
            openApcAmount = record.getApcAmount();
            if (explain) {
                explanation.add("** DOI in OpenAPC gefunden.");
                explanation.add("hybrid: " + openapcHybrid);
                explanation.add("openApcAmount: " + openApcAmount);
                if (record.isTa()) {
                    explanation.add("transformative agreement");
                }
                explanation.openapc = record;
            }
        } else {
            if (explain) {
                explanation.add("** DOI nicht in OpenAPC gefunden.");
            }
        }
        explanation.add("");
    }

    public void setValuesFromRomeo(Romeo record) {
        if (record == null) {
            explanation.add("** Issn(s) nicht in Sherpa/Romeo gefunden.");
            return;
        }

        addSource(SOURCE_ROMEO);
        explanation.add("** Issn(s) in Sherpa/Romeo gefunden.");
        explanation.romeo = record;
        embargoRomeo = findEmbargoTime(record);
        explanation.add("embargoRomeo: " + embargoRomeo);
        explanation.add("");
    }

    public LicenceInfo buildLicenceInfo() {
        addExplanation("** Klassifizierung der Lizenz");
        LicenceType lt = crossrefLicenceType;
        String licence = "";
        String licenceSource = "";
        if (lt != null) {
            licenceSource = "crossref";
            licence = crossrefLicence.url;
            addExplanation(String.format("Crossref licence type: %s", lt.name()));
        }
        if (doajLicence != null) {
            LicenceType doajLicenceType = getLicenceType(doajLicence);
            addExplanation(String.format("DOAJ licence type: %s", doajLicenceType.name()));
            if (doajLicenceType.isBetter(lt)) {
                lt = doajLicenceType;
                licenceSource = "doaj";
                licence = doajLicence;
            }
        }
        if (lt == null && openAlexLicence != null) {
            LicenceType openAlexLicenceType = getLicenceType(openAlexLicence);
            addExplanation(String.format("OpenAlex licence type: %s", openAlexLicenceType.name()));
            lt = openAlexLicenceType;
            licenceSource = "OpenAlex";
            licence = openAlexLicence;
        }

        if (lt != null) {
            addExplanation(String.format("Using licence from source %s", licenceSource));
        }

        LicenceInfo licenceInfo = new LicenceInfo(lt, licenceSource);
        licenceInfo.licence = licence;
        return licenceInfo;
    }


    public void setExplain(boolean explain) {
        this.explain = explain;
        explanation = new Explanation();
    }

    public boolean isExplain() {
        return explain;
    }

    public void addExplanation(String text) {
        if (explanation != null) {
            explanation.add(text);
        }
    }

    public Explanation getExplanation() {
        return explanation;
    }

    /*
     * collects explanations for the OA classification process that will be displayed if the publication is examined on the view COAT classification
     */
    public static class Explanation {
        public List<String> steps = new ArrayList<>();
        public CrossrefWork crossref;
        public UnpaywallResource unpaywall;
        public DoajJournal doaj;
        public OpenApcRecord openapc;
        public Romeo romeo;
        public OpenAlexWork openalex;

        public void add(String text) {
            steps.add(text);
        }
    }
}
