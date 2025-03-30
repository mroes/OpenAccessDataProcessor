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

package at.roesel.oadataprocessor.services.common;

import at.roesel.oadataprocessor.model.PublicationType;
import at.roesel.oadataprocessor.model.PublicationTypeMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.support.IdentifierSupport.coarIdFromUrl;
import static at.roesel.oadataprocessor.support.IdentifierSupport.coarPrefix;

public class PublicationTypeMapperImpl implements PublicationTypeMapper {

    private final Logger logger = LoggerFactory.getLogger(PublicationTypeMapperImpl.class);

    Map<Integer, PublicationType> typesMap; // publication types by ID
    private final Map<String, PublicationType> mapping; // Mapping of general publication types to the standard types.
    private final Map<String, Map<String, PublicationType>> mappingPerInstitution; // Mapping of individual publication types to the standard types.

    public PublicationTypeMapperImpl(Map<Integer, PublicationType> typesMap, Iterable<PublicationTypeMapping> mappings) {
        this.typesMap = typesMap;
        mapping = new HashMap<>();
        mappingPerInstitution = new HashMap<>();

        // take coar mappings directly from the publication types
        typesMap.values().forEach(publicationType -> {
            mapping.put(coarPrefix + publicationType.getCoarId(), publicationType);
            mapping.put(publicationType.getName(), publicationType);
        } );

        mappings.forEach( map -> {
            PublicationType publicationType = publicationTypeForId(map.getPubtypeId());
            if (publicationType != null) {
                if (hasValue(map.getInstitutionId())) {
                    Map<String, PublicationType> instMapping = mappingPerInstitution.computeIfAbsent(map.getInstitutionId(), k -> new HashMap<>());
                    instMapping.put(map.getValue(), publicationType);
                } else {
                    mapping.put(map.getValue(), publicationType);
                }
            }
        });
    }

    @Override
    public PublicationType mapType(String institutionId, String rawPublicationType) {
        if (rawPublicationType == null || rawPublicationType.isEmpty()) {
            return PublicationType.UNKNOWN;
        }

        // is an explicit mapping for the institutionId existing?
        PublicationType result = publicationTypeForInstitution(institutionId, rawPublicationType);
        if (result == null) {
            // No, then examine the general mappings.
            result = mapping.get(rawPublicationType);
            if (result == null) {
                // try with coar: prefix
                result = mapping.get(coarPrefix + rawPublicationType);
            }
            // is it a Coar url?
            String coarId = coarIdFromUrl(rawPublicationType);
            if (coarId != null) {
                result = mapping.get(coarId);
            }
            if (result == null) {
                // try in lower case
                result = mapping.get(rawPublicationType.toLowerCase());
            }
        }
        if (result == null) {
            result = PublicationType.UNKNOWN;
        }
        // do we have to replace the type with another type ?
        if (result.getMappedToId() != 0) {
            PublicationType mappedTo = publicationTypeForId(result.getMappedToId());
            if (mappedTo != null) {
                result = mappedTo;
            } else {
                // this should only happen if the data for the publication types is invalid
                logger.warn(String.format("PublicationType %s for input %s has undefined mapped typeId %d ", result.getName(), rawPublicationType, result.getMappedToId()));
            }
        }
        return result;

    }

    private PublicationType publicationTypeForInstitution(String institutionId, String rawPublicationType) {
        Map<String, PublicationType> instMapping = mappingPerInstitution.get(institutionId);
        if (instMapping == null) {
            return null;
        }
        PublicationType result = instMapping.get(rawPublicationType);
        return result;

    }

    private PublicationType publicationTypeForId(int typeId) {
        PublicationType result = typesMap.get(typeId);
        return result;
    }

}
