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

import at.roesel.oadataprocessor.model.PublicationType;
import at.roesel.oadataprocessor.model.PublicationTypeMapping;
import at.roesel.oadataprocessor.persistance.PublicationTypeMapppingRepository;
import at.roesel.oadataprocessor.persistance.PublicationTypeRepository;
import at.roesel.oadataprocessor.services.common.PublicationTypeMapper;
import at.roesel.oadataprocessor.services.common.PublicationTypeMapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicationTypeServiceImpl implements PublicationTypeService {

    private final Logger logger = LoggerFactory.getLogger(PublicationTypeServiceImpl.class);

    @Autowired
    private PublicationTypeRepository publicationTypeRepository;

    @Autowired
    private PublicationTypeMapppingRepository publicationTypeMapppingRepository;

    public Map<Integer, PublicationType> buildTable() {
        Map<Integer, PublicationType> publicationTypeMap = new HashMap<>();
        Iterable<PublicationType> types = publicationTypeRepository.findAll();
        for (PublicationType type : types) {
            publicationTypeMap.put(type.getId(), type);
        }
        return publicationTypeMap;
    }

    public PublicationTypeMapper buildMapper(String institutionId) {
        Iterable<PublicationTypeMapping> mappings;
        if (institutionId != null) {
            mappings = publicationTypeMapppingRepository.findAllByInstitutionId(institutionId);
        } else {
            mappings = publicationTypeMapppingRepository.findAll();
        }
        PublicationTypeMapperImpl mapper = new PublicationTypeMapperImpl(buildTable(), mappings);
        return mapper;
    }

    public List<PublicationType> findAllByEnabled() {
        return publicationTypeRepository.findAllByEnabledAndMappedToId(1, 0);
    }
}
