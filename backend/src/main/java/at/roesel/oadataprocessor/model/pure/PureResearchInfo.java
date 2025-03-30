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

package at.roesel.oadataprocessor.model.pure;

import at.roesel.oadataprocessor.services.pure.model.PureJournalAssociation;
import at.roesel.oadataprocessor.services.pure.model.PureManagingOrganisationalUnit;
import at.roesel.oadataprocessor.services.pure.model.PureOrganisation;
import at.roesel.oadataprocessor.services.pure.model.PureResearchOutput;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static at.roesel.common.BeanSupport.buildString;

public class PureResearchInfo {

    public long pureId;
    public String title;
    public String peerReview;
    public String managingOrganisationalUnit;
    public String journalAssociation;
    public String category;
    public String type;
    public int totalNumberOfAuthors;
    public String openAccessPermission;
    public String publicationStatuses;
    public String personAssociations;
    public String electronicVersions;
    public String keywordGroups;

    public static PureResearchInfo build(PureResearchOutput output) {
        PureResearchInfo info = new PureResearchInfo();
        info.pureId = output.pureId;
        info.title = output.resolvedTitle();
        info.peerReview = output.peerReview == null ? "" : output.peerReview.toString();
        info.managingOrganisationalUnit = buildmanagingOrganisationalUnit(output.managingOrganisationalUnit);
        info.journalAssociation = buildJournalAssociation(output.journalAssociation);
        info.totalNumberOfAuthors = output.totalNumberOfAuthors;
        info.category = output.category == null ? "" : output.category.texts().toString();
        info.type = output.type == null ? "" : output.type.texts().toString();
        info.openAccessPermission = output.openAccessPermission.texts().toString();
        info.publicationStatuses = buildString(output.publicationStatuses, ps -> {
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(ps.publicationDate.toString());
            builder.append("; ");
            builder.append(ps.publicationStatus.term.texts());
            builder.append(" }");
            return builder.toString();
        });

        info.electronicVersions = buildString(output.electronicVersions, ev -> {
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(ev.doi);
            builder.append(" }");
            return builder.toString();
        });

        /*
        info.keywordGroups = buildString(output.keywordGroups, kg -> {
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(kg.type.term.texts());
            builder.append("; ");
            builder.append(buildString(kg.keywordContainers, kc -> {
                if (kc.structuredKeyword == null || kc.structuredKeyword.term == null) {
                    return "";
                }
                return kc.structuredKeyword.term.texts().toString();
            }));
            builder.append(" }");
            return builder.toString();
        });
         */

        info.personAssociations = buildString(output.personAssociations, person -> {
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(person.name.firstName);
            builder.append("; ");
            builder.append(person.name.lastName);
            builder.append("; ");
            builder.append(person.personRole.term.texts());
            builder.append("; ");
            if (person.organisationalUnits != null) {
                builder.append("orgs=");
                builder.append(buildString(person.organisationalUnits, PureResearchInfo::buildOrganisation));
            }
            builder.append("; ");
            if (person.externalOrganisations != null) {
                builder.append("ext=");
                builder.append(buildString(person.externalOrganisations, PureResearchInfo::buildOrganisation));
            }
            builder.append(" }");
            return builder.toString();
        });

        return info;
    }

    private static String buildOrganisation(PureOrganisation organisation) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        builder.append(organisation.name.texts());
        builder.append("; ");
        builder.append(organisation.type.term.texts());
        return builder.toString();
    }

    private static String buildmanagingOrganisationalUnit(PureManagingOrganisationalUnit unit) {
        StringBuilder builder = new StringBuilder();
        if (unit != null) {
            builder.append("{ ");
            builder.append(unit.name.texts());
            builder.append("; ");
            builder.append(unit.type.texts());
            builder.append(" }");
        }
        return builder.toString();
    }

    private static String buildJournalAssociation(PureJournalAssociation journalAssociation) {
        StringBuilder builder = new StringBuilder();
        if (journalAssociation != null) {
            builder.append("{ ");
            builder.append(journalAssociation.resolvedTitle());
            builder.append("; ");
            builder.append(journalAssociation.resolvedIssn());
            builder.append("; ");
            if (journalAssociation.journal != null) {
                builder.append(journalAssociation.journal.type.texts());
            }
            builder.append(" }");
        }
        return builder.toString();
    }

    public static List<String> headers() {
        Field[] allFields = PureResearchInfo.class.getDeclaredFields();
        List<String> headers = new ArrayList<>();
        for (Field field : allFields) {
            headers.add(field.getName());
        }
        return headers;
    }

    public List<String> rows() {
        Field[] allFields = PureResearchInfo.class.getDeclaredFields();
        List<String> rows = new ArrayList<>();
        for (Field field : allFields) {
            String value;
            try {
                value = field.get(this).toString();
            } catch (IllegalAccessException e) {
                value = "!error!";
            }
            rows.add(value);
        }
        return rows;
    }

    public long getPureId() {
        return pureId;
    }

    public String getTitle() {
        return title;
    }

    public String getPeerReview() {
        return peerReview;
    }

    public String getManagingOrganisationalUnit() {
        return managingOrganisationalUnit;
    }

    public String getJournalAssociation() {
        return journalAssociation;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public int getTotalNumberOfAuthors() {
        return totalNumberOfAuthors;
    }

    public String getOpenAccessPermission() {
        return openAccessPermission;
    }

    public String getPublicationStatuses() {
        return publicationStatuses;
    }

    public String getPersonAssociations() {
        return personAssociations;
    }

    public String getElectronicVersions() {
        return electronicVersions;
    }

    public String getKeywordGroups() {
        return keywordGroups;
    }
}
