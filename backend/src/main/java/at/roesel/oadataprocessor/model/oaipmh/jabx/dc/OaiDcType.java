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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für oai_dcType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="oai_dcType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}title"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}creator"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}subject"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}description"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}publisher"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}contributor"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}date"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}type"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}format"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}identifier"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}source"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}language"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}relation"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}coverage"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}rights"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "oai_dcType", namespace = "http://www.openarchives.org/OAI/2.0/oai_dc/", propOrder = {
    "titleOrCreatorOrSubject"
})
public class OaiDcType {

    @XmlElementRefs({
        @XmlElementRef(name = "coverage", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "type", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "language", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "publisher", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "creator", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "contributor", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "relation", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "subject", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "title", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "source", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "date", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "format", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "description", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "rights", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<ElementType>> titleOrCreatorOrSubject;

    /**
     * Gets the value of the titleOrCreatorOrSubject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the titleOrCreatorOrSubject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTitleOrCreatorOrSubject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<ElementType>> getTitleOrCreatorOrSubject() {
        if (titleOrCreatorOrSubject == null) {
            titleOrCreatorOrSubject = new ArrayList<JAXBElement<ElementType>>();
        }
        return this.titleOrCreatorOrSubject;
    }

}
