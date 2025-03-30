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

package at.roesel.oadataprocessor.services.impexp.export;

import at.roesel.oadataprocessor.components.controller.PublicationSearchFilter;
import at.roesel.oadataprocessor.model.Institution;
import at.roesel.oadataprocessor.model.PublicationFlat;
import at.roesel.oadataprocessor.model.SourceReferenceFlat;
import at.roesel.oadataprocessor.model.coat.ClassificationOutput;
import at.roesel.oadataprocessor.services.PublicationAccessService;
import at.roesel.oadataprocessor.persistance.elastic.ElasticResultHandler;
import at.roesel.oadataprocessor.services.InstitutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static at.roesel.common.CollectionSupport.collectionToString;
import static at.roesel.oadataprocessor.components.openapi.PublicationConverter.addBaseCoarUrl;

@Component
public class DownloadService {

    private final Logger logger = LoggerFactory.getLogger(DownloadService.class);

    @Autowired
    private PublicationAccessService publicationAccessService;

    @Autowired
    private InstitutionService institutionService;


    public BuildResult createDownloadRecords(PublicationSearchFilter filter) {
        BuildResult result = new BuildResult();
        try {
            Map<String, Institution> institutionMap = institutionService.institutionMap();

            PublicationResultHandler handler = new PublicationResultHandler(institutionMap, filter.options);
            publicationAccessService.readPublicationsFromElastic(filter, handler);


/*
            // export in CSV format
            try (CharArrayWriter writer = new CharArrayWriter()) {
                CsvSupport.exportCsv(writer, new PublicationOutputDescriptor(), rows);
                result.resource = new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
 */

            // export in xlsx format
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ExcelStreamingExporter.export(outputStream, new PublicationOutputDescriptor(filter.options), handler.getRows());
                result.resource = new ByteArrayResource(outputStream.toByteArray());
            }


        } catch (Exception e) {
            if (result.resource == null) {
                result.resource = new ByteArrayResource(new byte[0]);
            }
            result.messages.add(e.getMessage());
        }

        return result;
    }

    private ClassificationOutput buildPublicationOutput(PublicationFlat publication) {
        ClassificationOutput output = new ClassificationOutput();
        output.id = publication.getId();
        output.doi = publication.getDoi();
        output.title = publication.getTitle();
        output.year = publication.getYear();

        output.publicationDate = "";
        output.licence = publication.getLicence().getLicence();
        output.licenceUrl = publication.getLicence().getUrl();
        output.licenceSource = publication.getLicence().getSource();

        output.embargoTime = publication.embargoTime;
        output.embargoSource = publication.embargoSource;
        output.embargoDate = "";
        output.coat = publication.getCoat();
        output.color = publication.getColor();
        output.upwColor = publication.getColorUpw();

        output.publisherName = publication.getPublisher().getName();
        output.publisherId = publication.getPublisher().getWikidataId();

        output.mainPublisherName = publication.getMainPublisher().getName();
        output.mainPublisherId = publication.getMainPublisher().getWikidataId();

        output.journalTitle = publication.getJournal().getTitle();
        output.journalId = publication.getJournal().getWikidataId();
        output.issn = collectionToString(publication.getJournal().getIssn(), ",");

        output.version = publication.oaVersionLink;
        output.versionSource = publication.versionSource;
        output.costs = publication.getCosts().getAmount();
        output.costsSource = publication.getCosts().getSource();
        output.costsCurrency = publication.getCosts().getCurrency();

        output.oaversionLink = publication.oaVersionLink;
        output.oaPlace = publication.oaPlace;
        output.oaPlaceSource = publication.oaPlaceSource;

        PublicationFlat.PublicationType publicationType = publication.getType();
        if (publicationType != null) {
            output.pubTypeName = publicationType.getName();
            output.pubType = addBaseCoarUrl(publicationType.getCoarId());
        }

        output.author = collectionToString(publication.authors, ";", author -> author.getLastName() + ", " + author.getFirstName() + (author.corr ? ", corr" : ""));

        return output;
    }

    private String institutionName(Map<String, Institution> institutionMap, String institutionId) {
        Institution institution = institutionMap.get(institutionId);
        String name = institution != null ? institution.getSname() : "?";
        return name;
    }

    private List<String> buildInstitutionNames(Map<String, Institution> institutionMap, List<String> institutions) {
        List<String> result = new ArrayList<>();
        for (String id : institutions) {
            result.add(institutionName(institutionMap, id));
        }
        return result;
    }

    public static class BuildResult {
        public ByteArrayResource resource;
        public List<String> messages = new ArrayList<>();
    }

    private class PublicationResultHandler implements ElasticResultHandler<PublicationFlat> {
        private final List<ClassificationOutput> rows;
        private final AtomicInteger count;
        private final Map<String, Institution> institutionMap;
        private final PublicationSearchFilter.ExcelDownloadOptions options;

        public PublicationResultHandler(Map<String, Institution> institutionMap, PublicationSearchFilter.ExcelDownloadOptions options) {
            this.institutionMap = institutionMap;
            rows = new ArrayList<>();
            count = new AtomicInteger();
            this.options = options;
        }

        @Override
        public boolean handle(PublicationFlat publication) {
            if (options.multipleRows) {
                for (SourceReferenceFlat ref : publication.getSources()) {
                    count.getAndIncrement();
                    ClassificationOutput output = buildPublicationOutput(publication);
                    output.institution = ref.institutionId;
                    output.institutionName = institutionName(institutionMap, ref.institutionId);
                    output.institutionCorresponding = ref.corr? "1" : "0";
                    output.nativeId = ref.nativeId;
                    rows.add(output);
                }
            } else {
                count.getAndIncrement();
                ClassificationOutput output = buildPublicationOutput(publication);
                output.institution = collectionToString(publication.institutions(), ";");
                output.institutionName = collectionToString(buildInstitutionNames(institutionMap, publication.institutions()), ";");
                output.nativeId = collectionToString(publication.nativeIds(), ";");
                output.institutionCorresponding = collectionToString(publication.corresponding(), ";");
                rows.add(output);
            }
            return false;
        }

        public List<ClassificationOutput> getRows() {
            return rows;
        }
    }
}

