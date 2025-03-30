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

package at.roesel.oadataprocessor.model;

import at.roesel.oadataprocessor.model.ui.PublicationColor;

import java.util.Objects;

/*
    CLASSIFICATION OF OPEN ACCESS TUPLES (COAT)
    https://journals.univie.ac.at/index.php/voebm/article/view/2788
 */
public class Coat {
    private String name;    // Farbe
    private int place;      // Place of OA
    private int licence;    // License
    private int version;    // Publication Version
    private int embargo;    // Embargo Period
    private int conditions; // Conditions of OA

    private int sources;    // Quellen, die für die Klassifizierung verwendet werden konnten

    // Quellen für die Parameter
    private String srcPlace;      // Place of OA
    private String srcLicence;    // License
    private String srcVersion;    // Publication Version
    private String srcEmbargo;    // Embargo Period
    private String srcConditions; // Conditions of OA

    private String colorUpw;    // Farbe von Unpaywall

    private final static int lowest = 4;


    public static final Coat errorCoat = new Coat(PublicationColor.UNKNOWN, -1, -1, -1, -1, -1);

    public Coat() {
        this(null, lowest , lowest, lowest, lowest, lowest);
    }

    public Coat(String name, int place, int licence, int version, int embargo, int conditions) {
        this.name = name;
        this.place = place;
        this.licence = licence;
        this.version = version;
        this.embargo = embargo;
        this.conditions = conditions;
    }

    public String getName() {
        if (name == null) {
            return "?name?";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public void setPlace(int place, String source) {
        this.setPlace(place);
        srcPlace = source;
    }

    public int getLicence() {
        return licence;
    }

    public void setLicence(int licence) {
        this.licence = licence;
    }

    public void setLicence(int licence, String source) {
        this.setLicence(licence);
        srcLicence = source;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setVersion(int version, String source) {
        this.setVersion(version);
        srcVersion = source;
    }

    public int getEmbargo() {
        return embargo;
    }

    public void setEmbargo(int embargo) {
        this.embargo = embargo;
    }

    public void setEmbargo(int embargo, String source) {
        this.setEmbargo(embargo);
        srcEmbargo = source;
    }

    public int getConditions() {
        return conditions;
    }

    public void setConditions(int conditions) {
        this.conditions = conditions;
    }

    public void setConditions(int conditions, String source) {
        this.setConditions(conditions);
        srcConditions = source;
    }

    public int getSources() {
        return sources;
    }

    public void setSources(int sources) {
        this.sources = sources;
    }

    public String getColorUpw() {
        if (colorUpw == null) {
            return "";
        }
        return colorUpw;
    }

    public void setColorUpw(String colorUpw) {
        this.colorUpw = colorUpw;
    }

    public boolean isLower(Coat coat) {
        boolean result = place <= coat.getPlace() &&
                licence <= coat.getLicence() &&
                version <= coat.getVersion() &&
                embargo <= coat.getEmbargo() &&
                conditions <= coat.getConditions();
        return result;
    }

    public boolean isUnknown() {
        return this.equals(errorCoat);
    }

    public String buildString() {
        StringBuilder builder = new StringBuilder();
        builder.append(place);
        builder.append(",");
        builder.append(licence);
        builder.append(",");
        builder.append(version);
        builder.append(",");
        builder.append(embargo);
        builder.append(",");
        builder.append(conditions);
        return builder.toString();
    }

    public String buildStringWithExplanation() {
        StringBuilder builder = new StringBuilder();
        builder.append(place);
        builder.append("\t");
        builder.append(srcPlace);
        builder.append("\r\n");
        builder.append(licence);
        builder.append("\t");
        builder.append(srcLicence);
        builder.append("\r\n");
        builder.append(version);
        builder.append("\t");
        builder.append(srcVersion);
        builder.append("\r\n");
        builder.append(embargo);
        builder.append("\t");
        builder.append(srcEmbargo);
        builder.append("\r\n");
        builder.append(conditions);
        builder.append("\t");
        builder.append(srcConditions);
        builder.append("\r\n");
        return builder.toString();
    }

    public static Coat fromString(String coatStr) {
        Coat coat = null;
        if (coatStr != null && !coatStr.isEmpty()) {
            String[] values = coatStr.split(",");
            if (values.length == 5) {
                coat = new Coat();
                coat.setPlace(Integer.parseInt(values[0]));
                coat.setLicence(Integer.parseInt(values[1]));
                coat.setVersion(Integer.parseInt(values[2]));
                coat.setEmbargo(Integer.parseInt(values[3]));
                coat.setConditions(Integer.parseInt(values[4]));
            }
        }
        if (coat == null) {
            coat = errorCoat;
        }
        return coat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coat coat = (Coat) o;
        return place == coat.place && licence == coat.licence && version == coat.version && embargo == coat.embargo && conditions == coat.conditions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(place, licence, version, embargo, conditions);
    }

    @Override
    public String toString() {
        return // "Coat " +
                name + " (" +
                place +
                ", " + licence +
                ", " + version +
                ", " + embargo +
                ", " + conditions +
                ")";
    }

    public String sources2Str() {
        StringBuilder sb = new StringBuilder();
        if ((sources & 1) == 1) {
            sb.append("C");
        } else {
            sb.append(" ");
        }
        if ((sources & 2) == 2) {
            sb.append("U");
        } else {
            sb.append(" ");
        }
        if ((sources & 4) == 4) {
            sb.append("D");
        } else {
            sb.append(" ");
        }
        if ((sources & 8) == 8) {
            sb.append("A");
        } else {
            sb.append(" ");
        }
        return sb.toString();
    }

    public String getSrcPlace() {
        return srcPlace;
    }

    public String getSrcLicence() {
        return srcLicence;
    }

    public String getSrcVersion() {
        return srcVersion;
    }

    public String getSrcEmbargo() {
        return srcEmbargo;
    }

    public String getSrcConditions() {
        return srcConditions;
    }
}
