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

//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2021.11.18 um 11:17:28 AM CET 
//


package at.roesel.oadataprocessor.model.oaipmh.jabx.dc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the at.roesel.oadataprocessor.model.oaipmh.jabx.dc package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Contributor_QNAME = new QName("http://purl.org/dc/elements/1.1/", "contributor");
    private final static QName _Rights_QNAME = new QName("http://purl.org/dc/elements/1.1/", "rights");
    private final static QName _Publisher_QNAME = new QName("http://purl.org/dc/elements/1.1/", "publisher");
    private final static QName _Date_QNAME = new QName("http://purl.org/dc/elements/1.1/", "date");
    private final static QName _Coverage_QNAME = new QName("http://purl.org/dc/elements/1.1/", "coverage");
    private final static QName _Identifier_QNAME = new QName("http://purl.org/dc/elements/1.1/", "identifier");
    private final static QName _Creator_QNAME = new QName("http://purl.org/dc/elements/1.1/", "creator");
    private final static QName _Subject_QNAME = new QName("http://purl.org/dc/elements/1.1/", "subject");
    private final static QName _Dc_QNAME = new QName("http://www.openarchives.org/OAI/2.0/oai_dc/", "dc");
    private final static QName _Format_QNAME = new QName("http://purl.org/dc/elements/1.1/", "format");
    private final static QName _Description_QNAME = new QName("http://purl.org/dc/elements/1.1/", "description");
    private final static QName _Language_QNAME = new QName("http://purl.org/dc/elements/1.1/", "language");
    private final static QName _Source_QNAME = new QName("http://purl.org/dc/elements/1.1/", "source");
    private final static QName _Title_QNAME = new QName("http://purl.org/dc/elements/1.1/", "title");
    private final static QName _Type_QNAME = new QName("http://purl.org/dc/elements/1.1/", "type");
    private final static QName _Relation_QNAME = new QName("http://purl.org/dc/elements/1.1/", "relation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: at.roesel.oadataprocessor.model.oaipmh.jabx.dc
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OaiDcType }
     * 
     */
    public OaiDcType createOaiDcType() {
        return new OaiDcType();
    }

    /**
     * Create an instance of {@link ElementType }
     * 
     */
    public ElementType createElementType() {
        return new ElementType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "contributor")
    public JAXBElement<ElementType> createContributor(ElementType value) {
        return new JAXBElement<ElementType>(_Contributor_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "rights")
    public JAXBElement<ElementType> createRights(ElementType value) {
        return new JAXBElement<ElementType>(_Rights_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "publisher")
    public JAXBElement<ElementType> createPublisher(ElementType value) {
        return new JAXBElement<ElementType>(_Publisher_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "date")
    public JAXBElement<ElementType> createDate(ElementType value) {
        return new JAXBElement<ElementType>(_Date_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "coverage")
    public JAXBElement<ElementType> createCoverage(ElementType value) {
        return new JAXBElement<ElementType>(_Coverage_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "identifier")
    public JAXBElement<ElementType> createIdentifier(ElementType value) {
        return new JAXBElement<ElementType>(_Identifier_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "creator")
    public JAXBElement<ElementType> createCreator(ElementType value) {
        return new JAXBElement<ElementType>(_Creator_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "subject")
    public JAXBElement<ElementType> createSubject(ElementType value) {
        return new JAXBElement<ElementType>(_Subject_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OaiDcType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openarchives.org/OAI/2.0/oai_dc/", name = "dc")
    public JAXBElement<OaiDcType> createDc(OaiDcType value) {
        return new JAXBElement<OaiDcType>(_Dc_QNAME, OaiDcType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "format")
    public JAXBElement<ElementType> createFormat(ElementType value) {
        return new JAXBElement<ElementType>(_Format_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "description")
    public JAXBElement<ElementType> createDescription(ElementType value) {
        return new JAXBElement<ElementType>(_Description_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "language")
    public JAXBElement<ElementType> createLanguage(ElementType value) {
        return new JAXBElement<ElementType>(_Language_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "source")
    public JAXBElement<ElementType> createSource(ElementType value) {
        return new JAXBElement<ElementType>(_Source_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "title")
    public JAXBElement<ElementType> createTitle(ElementType value) {
        return new JAXBElement<ElementType>(_Title_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "type")
    public JAXBElement<ElementType> createType(ElementType value) {
        return new JAXBElement<ElementType>(_Type_QNAME, ElementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://purl.org/dc/elements/1.1/", name = "relation")
    public JAXBElement<ElementType> createRelation(ElementType value) {
        return new JAXBElement<ElementType>(_Relation_QNAME, ElementType.class, null, value);
    }

}
