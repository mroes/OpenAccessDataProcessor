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

import at.roesel.oadataprocessor.model.Institution;

public class JsonInstitution implements Comparable<JsonInstitution> {
    private String id;  // ROR

    private String name;    // german name
    private String nameEn;  // english name

    private String sname; // short name

    private boolean fileDelivery; // institution delivers the publications per file

    public JsonInstitution(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public boolean isFileDelivery() {
        return fileDelivery;
    }

    public void setFileDelivery(boolean fileDelivery) {
        this.fileDelivery = fileDelivery;
    }

    public static JsonInstitution from(Institution institution) {
        JsonInstitution jsonInstitution = new JsonInstitution((institution.getId()), institution.getName());
        jsonInstitution.setNameEn(institution.getName2());
        jsonInstitution.setSname(institution.getSname());
        jsonInstitution.setFileDelivery(institution.getRepositoryUrl() == null);
        return jsonInstitution;
    }

    @Override
    public int compareTo(JsonInstitution o) {
        return name.compareTo(o.getName());
    }
}
