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

package at.roesel.oadataprocessor.services.publicationsource;

import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.PublicationType;
import at.roesel.oadataprocessor.services.common.PublicationTypeMapper;
import at.roesel.oadataprocessor.services.oaipmh.PublicationCreatorOaiPmh;
import at.roesel.oadataprocessor.services.pure.PurePublicationCreator;

import static at.roesel.oadataprocessor.support.TitleSupport.normalizeTitle;

/*
 * Creates a Publication from a PublicationSource
 */
public class PublicationCreator {

    private final PurePublicationCreator purePublicationCreator = new PurePublicationCreator();
    private final PublicationCreatorOaiPmh oaiPmhPublicationCreator = new PublicationCreatorOaiPmh();
    private final PublicationFromJsonCreator publicationFromJsonCreator = new PublicationFromJsonCreator();

    private final PublicationTypeMapper publicationTypeMapper;

    public PublicationCreator(PublicationTypeMapper publicationTypeMapper) {
        this.publicationTypeMapper = publicationTypeMapper;
    }

    public Publication from(PublicationSource source) {
        Publication publication;
        switch (source.getDataType()) {
            case SourceTypes.pure :
                publication = purePublicationCreator.from(source);
                break;
            case SourceTypes.oai_dc:
                publication = oaiPmhPublicationCreator.from(source);
                break;
            case SourceTypes.json:
                publication = publicationFromJsonCreator.from(source);
                break;
            default:
                return null;
        }

        // normalize title and calculate hash for title
        publication.setTitleAndHash(normalizeTitle(publication.getTitle()));

        // normalize publication type
        String pubType = source.getPubtype();
        PublicationType publicationType = publicationTypeMapper.mapType(source.getInstitution(), pubType);
        publication.setPublicationType(publicationType);

        return publication;
    }

}
