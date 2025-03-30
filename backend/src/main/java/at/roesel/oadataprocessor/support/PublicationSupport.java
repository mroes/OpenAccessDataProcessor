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

package at.roesel.oadataprocessor.support;

import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.PublicationProps;
import at.roesel.oadataprocessor.persistance.conversion.ObjectConverter;

// set/get for json properties in Publication
public class PublicationSupport {

    private static ObjectConverter<PublicationProps> propsConverter = new ObjectConverter<>(PublicationProps.class);

    public static PublicationProps propsFrom(Publication publication, boolean createProps) {
        PublicationProps props = propsConverter.convertToEntityAttribute(publication.getProps());
        if (props == null && createProps) {
            props =  new PublicationProps();
        }
        return props;
    }

    public static PublicationProps propsFrom(Publication publication) {
        return propsFrom(publication, true);
    }

    public static String jsonFrom(PublicationProps props) {
        String result = propsConverter.convertToDatabaseColumn(props);
        return result;
    }
}
