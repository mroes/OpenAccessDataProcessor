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

package at.roesel.oadataprocessor.components.controller;

import at.roesel.oadataprocessor.model.Faq;
import at.roesel.oadataprocessor.model.ui.JsonInstitution;
import at.roesel.oadataprocessor.model.ui.JsonPublicationType;
import at.roesel.oadataprocessor.services.InstitutionService;
import at.roesel.oadataprocessor.services.PublicationTypeService;
import at.roesel.oadataprocessor.services.UiSupportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DataController {

    private final static Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private PublicationTypeService publicationTypeService;

    @Autowired
    private UiSupportService uiSupportService;

    public DataController() {
    }

    @GetMapping(path = "/institution/list")
    public List<JsonInstitution> institutions() throws IOException {
        return institutionService.findAllActive().stream().map(JsonInstitution::from).sorted().collect(Collectors.toList());
    }

    @GetMapping(path = "/publicationtype/list")
    public List<JsonPublicationType> publicationtypes() {
        return publicationTypeService.findAllByEnabled().stream().map(JsonPublicationType::from).sorted().collect(Collectors.toList());
    }

    @GetMapping(path = "/faq/list")
    public Iterable<Faq> faqs(@RequestParam(name = "lang") String language) {
        return uiSupportService.readFaqs(language);
    }

}
