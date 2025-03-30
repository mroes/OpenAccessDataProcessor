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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementNode {
    private Map<String, String> attributes;
    private Map<String, List<ElementNode>> children;
    private String textContent;

    public ElementNode() {
        this.attributes = new HashMap<>();
        this.children = new HashMap<>();
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getAttribute(String attrName) {
        return attributes.get(attrName);
    }

    public Map<String, List<ElementNode>> getChildren() {
        return children;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void addChild(String name, ElementNode child) {
        this.children.computeIfAbsent(name, k -> new ArrayList<>()).add(child);
    }

    public String getFirstChildContent(String name) {
        List<ElementNode> nodes = this.children.get(name);
        if (nodes != null && !nodes.isEmpty()) {
            return nodes.get(0).getTextContent();
        }
        return null;
    }

    public void addAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    @Override
    public String toString() {
        return "ElementNode{" +
                "attributes=" + attributes +
                ", children=" + children +
                ", textContent='" + textContent + '\'' +
                '}';
    }
}
