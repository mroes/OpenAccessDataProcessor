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

package at.roesel.oadataprocessor.services.pure;

import at.roesel.oadataprocessor.model.Author;
import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.SourceReference;
import at.roesel.oadataprocessor.persistance.conversion.ObjectConverter;
import at.roesel.oadataprocessor.services.publicationsource.BasicPublicationCreator;
import at.roesel.oadataprocessor.services.pure.model.PureResearchOutput;

import java.util.List;
import java.util.Set;

/*
 * Creates a publication from a PureResearchOutput dataset
 */
public class PurePublicationCreator extends BasicPublicationCreator {

    private final ObjectConverter<PureResearchOutput> converter = new ObjectConverter<>(PureResearchOutput.class);

    public Publication from(PublicationSource source) {

        Publication publication = super.from(source);

        PureResearchOutput researchOutput = converter.convertToEntityAttribute(source.getRecord());

        publication.setTitle(researchOutput.resolvedTitle());

        // year
        publication.setYear(researchOutput.resolvedPublicationYear());
        // authors
        publication.setAuthors(researchOutput.resolvedAuthors());
        publication.setNormalizedIssn(researchOutput.issn());
        String organisation = null;
        List<Author> authors = publication.getAuthors();
        if (authors != null) {
            for (Author author : authors) {
                if (author.organisation != null && !author.organisation.isEmpty()) {
                    organisation = author.organisation;
                    break;
                }
            }
        }
        if (organisation != null) {
            Set<SourceReference> sources = publication.getSources();
            SourceReference sourceReference = sources.iterator().next();
            sourceReference.setOrganisation(organisation);
        }
        return publication;
    }
}
