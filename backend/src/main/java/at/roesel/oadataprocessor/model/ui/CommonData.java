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

package at.roesel.oadataprocessor.model.ui;

import at.roesel.oadataprocessor.model.OAColor;

import java.util.List;

public class CommonData {

    private List<OAColor> oaColors;
    private List<OAColor> oaColorsReduced;

    private List<JsonInstitution> institutions;
    private List<JsonInstitution> inactiveInstitutions;

    private List<JsonPublicationType> publicationTypes;

    public List<OAColor> getOaColors() {
        return oaColors;
    }

    public void setOaColors(List<OAColor> oaColors) {
        this.oaColors = oaColors;
    }

    public List<JsonInstitution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<JsonInstitution> institutions) {
        this.institutions = institutions;
    }

    public List<JsonInstitution> getInactiveInstitutions() {
        return inactiveInstitutions;
    }

    public void setInactiveInstitutions(List<JsonInstitution> inactiveInstitutions) {
        this.inactiveInstitutions = inactiveInstitutions;
    }

    public List<JsonPublicationType> getPublicationTypes() {
        return publicationTypes;
    }

    public void setPublicationTypes(List<JsonPublicationType> publicationTypes) {
        this.publicationTypes = publicationTypes;
    }

    public List<OAColor> getOaColorsReduced() {
        return oaColorsReduced;
    }

    public void setOaColorsReduced(List<OAColor> oaColorsReduced) {
        this.oaColorsReduced = oaColorsReduced;
    }
}
