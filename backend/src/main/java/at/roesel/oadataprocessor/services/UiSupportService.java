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

import at.roesel.oadataprocessor.model.ClassificationData;
import at.roesel.oadataprocessor.model.Coat;
import at.roesel.oadataprocessor.model.Faq;
import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.search.SearchEntity;
import at.roesel.oadataprocessor.model.ui.ClassificationResult;
import at.roesel.oadataprocessor.persistance.FaqRepository;
import at.roesel.oadataprocessor.persistance.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static at.roesel.oadataprocessor.support.DoiSupport.checkDoi;

@Component
public class UiSupportService {

    private final Logger logger = LoggerFactory.getLogger(UiSupportService.class);

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private ClassifyService classifyService;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private FaqRepository faqRepository;

    public Iterable<Faq> readFaqs(String language) {
        language = sanitizeLanguage(language);
        return faqRepository.findAllByLanguage(language);
    }

    private static String sanitizeLanguage(String language) {
        language = language.toLowerCase();
        if (language.length() > 2) {
            language = language.substring(0,2);
        }
        if (!language.equals("de") && !language.equals("en")) {
            language = "en";
        }
        return language;
    }

    public ClassificationResult classifyPublication(String doi, String ip) {
        ClassificationResult result = new ClassificationResult();
        String comment = "";
        Publication publication = publicationService.readByDoi(doi);
        if (publication == null) {
            if (checkDoi(doi)) {
                publication = new Publication();
                publication.setDoi(doi);
            } else {
                comment = "invalid doi";
            }
        } else {
            comment = "existing";
        }
        if (publication != null) {
            ClassificationData classificationData = classifyService.buildClassificationData(doi, true);
            Coat coat = classifyService.classifyCoat(classificationData);
            result.publication = publication;
            result.coat = coat;
            result.explanation = classificationData.getExplanation();
        }

        logSearch(doi, ip, comment);

        return result;
    }

    private void logSearch(String doi, String ip, String comment) {
        SearchEntity search = new SearchEntity();
        search.setDoi(doi);
        search.setIp(ip);
        search.setComment(comment);
        searchRepository.save(search);
    }

}
