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
import at.roesel.oadataprocessor.model.crossref.CrossrefWork;
import at.roesel.oadataprocessor.model.doaj.DoajJournal;
import at.roesel.oadataprocessor.model.openalex.OpenAlexWork;
import at.roesel.oadataprocessor.model.ui.PublicationColor;
import at.roesel.oadataprocessor.model.unpaywall.UnpaywallResource;
import at.roesel.oadataprocessor.services.crossref.CrossrefService;
import at.roesel.oadataprocessor.services.doaj.DoajService;
import at.roesel.oadataprocessor.services.openalex.OpenAlexService;
import at.roesel.oadataprocessor.services.openapc.OpenApcService;
import at.roesel.oadataprocessor.services.sherpa.SherpaService;
import at.roesel.oadataprocessor.services.unpaywall.UnpaywallService;
import at.roesel.oadataprocessor.support.DateUtil;
import at.roesel.oadataprocessor.support.PublicationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.support.PublicationSupport.jsonFrom;

@Component
public class ClassifyService {

    private final String sourceUnpaywall = "unpaywall";
    private final String sourceOpenApc = "openapc";
    private final String sourceDoaj = "doaj";
    private final String sourceRoemo = "romeo";

    private final Logger logger = LoggerFactory.getLogger(ClassifyService.class);

    @Autowired
    private CrossrefService crossrefService;

    @Autowired
    private UnpaywallService unpaywallService;

    @Autowired
    private DoajService doajService;

    @Autowired
    private OpenApcService openApcService;

    @Autowired
    private SherpaService sherpaService;

    @Autowired
    private OpenAlexService openAlexService;

    @Autowired
    private PublicationService publicationService;

    private final CoatClassifier coatClassifier = new CoatClassifierDefault();
    private final CoatColorMapper coatColorMapper = new CoatColorMapperImpl();

    public Coat classifyCoat(ClassificationData classificationData) {
        Coat coat = coatClassifier.classify(classificationData);
        coat.setColorUpw(classificationData.colorUpw);
        String color;
        if (!coat.equals(Coat.errorCoat)) {
            // map the COAT result to an OA colour
            color = coatColorMapper.mapColor(coat);
        } else {
            color = PublicationColor.UNKNOWN;
        }
        coat.setName(color);
        return coat;
    }

    public Coat classifyTuple(String doi) {

        ClassificationData classificationData = buildClassificationData(doi, false);

        Coat coat = coatClassifier.classify(classificationData);

        // OA Color determination
        String color = coatColorMapper.mapColor(coat);
        coat.setName(color);

        return coat;
    }

    /*
     * collect data for the classification, interpretation of COAT-Funtions.R
     *
     * @param explain if true then the steps of the classification are recorded
     */
    public ClassificationData buildClassificationData(String doi, boolean explain) {
        ClassificationData classificationData = new ClassificationData(doi);
        classificationData.setExplain(explain);
        if (doi == null || doi.isEmpty()) {
            return classificationData;
        }

        // Crossref
        CrossrefWork crossrefWork = crossrefService.getCrossrefWork(doi);
        classificationData.setValuesFromCrossref(crossrefWork);

        // Doaj
        if (crossrefWork != null) {
            List<String> issns = crossrefWork.getIssns();
            if (explain) {
                classificationData.addExplanation("Suche Doaj record mit issn(s): " + issns);
            }
            DoajJournal doajRecord = doajService.searchForDoaj(issns);
            if (doajRecord != null) {
                // check if the journal was included in DOAJ at the time of publication
                if (doajRecord.getEndDate() > 0 && doajRecord.getEndDate() < DoajJournal.MAX_DATE) {
                    int publishedDate = classificationData.getPublishedDate();
                    if (publishedDate > 0
                            && publishedDate <= doajRecord.getEndDate()) {
                        // yes, the journal was in DOAJ
                        classificationData.setValuesFromDoaj(doajRecord);
                    } else {
                        logger.debug(String.format("doi = %s, issn = %s found in Doaj but publicationdate %d after %d",
                                doi, issns, publishedDate, doajRecord.getEndDate()));
                        if (classificationData.isExplain()) {
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            classificationData.addExplanation(String.format("issn(s): %s gefunden in doaj, aber nach der OA-Gültigkeit %s",
                                    issns,
                                    dateFormat.format(DateUtil.intTolocalDate(doajRecord.getEndDate()))
                            ));
                            classificationData.addExplanation("");
                        }
                    }
                } else {
                    classificationData.setValuesFromDoaj(doajRecord);
                }
            }
        }

        // Unpaywall
        UnpaywallResource unpaywallResource = unpaywallService.getResource(doi);
        classificationData.setValuesFromUnpaywall(unpaywallResource);

        // OpenAPC
        classificationData.setValuesFromApc(openApcService.searchByDoi(doi));

        if (crossrefWork != null) {
            if (explain) {
                classificationData.addExplanation("Search Sherpa/Romeao record with issn(s): " + crossrefWork.getIssns());
            }
            classificationData.setValuesFromRomeo(sherpaService.romeoForIssns(crossrefWork.getIssns()));
        }

        if (classificationData.crossrefLicence == null && classificationData.doajLicence == null ) {
            // if we found no licence yet, we try to get one from OpenAlex
            OpenAlexWork openAlexWork = openAlexService.getWork(doi);
            if (openAlexWork != null) {
                if (explain) {
                    classificationData.getExplanation().openalex = openAlexWork;
                    classificationData.addExplanation(String.format("** Found record for '%s' in OpenAlex", doi));
                }
                classificationData.openAlexLicence = openAlexWork.licence();
            } else {
                if (explain) {
                    classificationData.addExplanation(String.format("** No record for '%s' in OpenAlex", doi));
                }
            }
            if (explain) {
                classificationData.addExplanation("");
            }
        }

        classificationData.prepare();

        return classificationData;
    }

    public void classifyAllPublications() {

        logger.info("Classify OA status of publications");
        final int[] count = {0};
        publicationService.visitAll(0,
                new PublicationProvider() {
                    @Override
                    public Page<Publication> provide(PageRequest pageRequest) {
                        return publicationService.findAllByClassificationStatus(pageRequest, Publication.CLASSIFICATION_TODO);
                    }

                    @Override
                    public boolean isIncrementPage() {
                        return false;
                    }
                },
                publication -> {
                    count[0]++;

                    boolean dataChanged = false;

                    Coat previousCoat = Coat.fromString(publication.getCoat());
                    previousCoat.setName(publication.getColor());

                    logger.debug("{}\t{}\t{}", count[0], publication.getId(), publication.getTitle());

                    ClassificationData classificationData = buildClassificationData(publication.getDoi(), false);
                    Coat coat = classifyCoat(classificationData);

                    publication.setCoat(coat.buildString());
                    publication.setColor(coat.getName());
                    publication.setColorUpw(coat.getColorUpw());
                    int classificationStatus = Publication.CLASSIFICATION_OK;
                    if (coat.equals(Coat.errorCoat)) {
                        classificationStatus = Publication.CLASSIFICATION_NOT_POSSIBLE;
                    }
                    LicenceInfo bestLicence = classificationData.getBestLicence();
                    if (bestLicence != null) {
                        String licence = bestLicence.licence;
                        if (licence != null) {
                            String normalizedLicence = Licences.buildNormalizedLicence(licence).getNormalizedLicence();
                            if (normalizedLicence.length() > 32) {
                                normalizedLicence = normalizedLicence.substring(0, 32);
                            }
                            if (!normalizedLicence.equals(publication.getLicence())) {
                                dataChanged = true;
                                publication.setLicence(normalizedLicence);
                            }
                        }
                    }

                    boolean coatChanged = !coat.equals(previousCoat);

                    // save enriched publication data
                    PublicationProps props = PublicationSupport.propsFrom(publication);
                    if (bestLicence != null) {
                        props.licenceUrl = bestLicence.licence;
                        props.licenceSource = bestLicence.licenceSource;
                    }
                    props.version = classificationData.getOaversion();
                    if (hasValue(props.version)) {
                        props.versionSource = sourceUnpaywall;
                    } else {
                        props.versionSource = "";
                    }
                    props.oaVersionLink = classificationData.getOaurl();
                    props.oaPlace = classificationData.getHosttype();
                    if (hasValue(props.oaPlace)) {
                        props.oaPlaceSource = sourceUnpaywall;
                    } else {
                        props.oaPlaceSource = "";
                    }
                    if (classificationData.embargoRomeo != ClassificationData.EMBARGO_UNKNOWN) {
                        props.embargoTime = Integer.toString(classificationData.embargoRomeo);
                        props.embargoSource = sourceRoemo;
                    } else {
                        props.embargoSource = "";
                    }
                    Amount amount = classificationData.apc();
                    if (amount != null) {
                        props.costs = formatAmount(amount.value);
                        props.costsCurrency = amount.currency;
                        if (classificationData.inOpenApc) {
                            props.costsSource = sourceOpenApc;
                        } else if (classificationData.inDoaj) {
                            props.costsSource = sourceDoaj;
                        }
                    }


                    String newProps = jsonFrom(props);
                    if (!newProps.equals(publication.getProps())) {
                        publication.setProps(newProps);
                        dataChanged = true;
                    }

                    if (coatChanged || dataChanged || classificationStatus != publication.getClassificationStatus()) {
                        publication.setClassificationStatus(classificationStatus);
                        if (coatChanged) {
                            if (previousCoat.isUnknown()) {
                                logger.info("COAT set\t{}\t{}\t{}\t{}\t{}", count[0], publication.getId(), publication.getCoat(), coat.getName(), coat.getColorUpw());
                            } else {
                                logger.warn("COAT changed\t{}\t{}\t{}\t{}\t{}\tprev: {}", count[0], publication.getId(), publication.getCoat(), coat.getName(), coat.getColorUpw(), previousCoat);
                            }
                        }
                        publicationService.save(publication);
                    }
                });
        logger.info(String.format("Classified %d publications", count[0]));

    }

    private String formatAmount(long value) {
        return String.format("%.2f", value / 100f);
    }

}
