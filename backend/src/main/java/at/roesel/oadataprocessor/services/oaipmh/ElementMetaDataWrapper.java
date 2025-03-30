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

package at.roesel.oadataprocessor.services.oaipmh;

import at.roesel.oadataprocessor.support.MetaDataWrapper;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ElementMetaDataWrapper implements MetaDataWrapper {
    private final ElementNode rootNode;

    public ElementMetaDataWrapper(Element element) {
        this.rootNode = parseElement(element);
    }

    @Override
    public String getFieldValue(String name) {
        List<String> values = getFieldValues(name);
        return values.isEmpty() ? null : values.get(0);
    }

    @Override
    public List<String> getFieldValues(String name) {
        List<ElementNode> nodes = getNodesByPath(name);
        List<String> values = new ArrayList<>();
        for (ElementNode node : nodes) {
            if (node.getTextContent() != null) {
                values.add(node.getTextContent());
            }
        }
        return values;
    }

    public List<String> getAttributeValues(String name, String attrName) {
        List<ElementNode> nodes = getNodesByPath(name);
        List<String> values = new ArrayList<>();
        for (ElementNode node : nodes) {
            values.add(node.getAttribute(attrName));
        }
        return values;
    }

    @Override
    public List<String> getFieldNames() {
        return new ArrayList<>(rootNode.getChildren().keySet());
    }

    private ElementNode parseElement(Element element) {
        ElementNode node = new ElementNode();
        parseAttributes(element, node);
        parseChildElements(element, node);
        return node;
    }

    private void parseAttributes(Element element, ElementNode node) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            node.addAttribute(attribute.getNodeName(), attribute.getNodeValue());
        }
    }

    private void parseChildElements(Element element, ElementNode node) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                ElementNode childElementNode = parseElement(childElement);
                node.addChild(childElement.getTagName(), childElementNode);
            } else if (childNode.getNodeType() == Node.TEXT_NODE) {
                String textContent = childNode.getTextContent().trim();
                if (!textContent.isEmpty()) {
                    node.setTextContent(textContent);
                }
            }
        }
    }

    public List<ElementNode> getNodesByPath(String path) {
        String[] parts = path.split("\\.");
        List<ElementNode> currentNodes = Arrays.asList(rootNode);

        for (String part : parts) {
            if (part.contains("[") && part.contains("]")) {
                String tagName = part.substring(0, part.indexOf('['));
                String condition = part.substring(part.indexOf('[') + 1, part.indexOf(']'));
                String[] conditionParts = condition.split("=");
                String attrName = conditionParts[0];
                String attrValue = conditionParts[1].replace("\"", "");

                currentNodes = currentNodes.stream()
                        .flatMap(node -> node.getChildren().getOrDefault(tagName, new ArrayList<>()).stream())
                        .filter(node -> attrValue.equals(node.getAttributes().get(attrName)))
                        .collect(Collectors.toList());
            } else {
                currentNodes = currentNodes.stream()
                        .flatMap(node -> node.getChildren().getOrDefault(part, new ArrayList<>()).stream())
                        .collect(Collectors.toList());
            }
        }
        return currentNodes;
    }

    @Override
    public OaipmhRecord getData() {
        OaipmhRecord data = new OaipmhRecord();
//        data.setData(fields);
        return data;
    }
}
