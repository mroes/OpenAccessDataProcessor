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

package at.roesel.oadataprocessor.persistance;

import at.roesel.oadataprocessor.model.Publication;
import at.roesel.oadataprocessor.model.Publisher;
import at.roesel.oadataprocessor.model.ui.PublicationColor;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/*
There are separate queries for the aggregated publications for all institutions and per institution.
The query for all institutions is needed because publications can belong to multiple institutions,
and therefore the summed numbers per institution would count these publications multiple times.
 */

@Component
public class PublicationRepositoryCustomImpl implements PublicationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    static final String statsQuery = "SELECT '' as institutionId, COUNT(distinct id) AS value , color as name, '' as publisherid, '' as mainpublisherid, true as corr FROM publication " +
            "where status = " + Publication.STATUS_ACTIVE + "GROUP BY color";

    public List<PublicationColor> readStats() {
        List<PublicationColor> result = entityManager.createNativeQuery(statsQuery, PublicationColor.class).getResultList();
        return result;
    }

    static final String colorsPerYearPublisherQuery = "SELECT '' as institutionId, color as name, year, COUNT(distinct id) AS value, publisherid, mainpublisherid, true as corr FROM publication " +
            "where status = " + Publication.STATUS_ACTIVE + " GROUP BY color,year,publisherId";

    public List<PublicationColor> readColorsPerYearAndPublisher() {
        List<PublicationColor> result = entityManager.createNativeQuery(colorsPerYearPublisherQuery, PublicationColor.class).getResultList();
        return result;
    }

    static final String colorsPerInstitutionQuery = "SELECT color as name, pi.institutionid as institutionId, COUNT(distinct p.id) as value, 0 as year, '' as publisherid, mainpublisherid, pi.corr as corr" +
            " FROM publication p left JOIN publication_institution pi ON p.id = pi.publicationid" +
            " where status = " + Publication.STATUS_ACTIVE + "GROUP BY pi.institutionid, color";

    public List<PublicationColor> readColorsPerInstitution() {
        List<PublicationColor> result = entityManager.createNativeQuery(colorsPerInstitutionQuery, PublicationColor.class).getResultList();
        return result;
    }

    static final String colorsPerInstitutionYearQuery = "SELECT color as name, pi.institutionid as institutionId, COUNT(distinct p.id) as value, year, '' as publisherid, mainpublisherid, pi.corr as corr" +
            " FROM publication p left JOIN publication_institution pi ON p.id = pi.publicationid" +
            " where status = " + Publication.STATUS_ACTIVE + " GROUP BY pi.institutionid, year, color";

    public List<PublicationColor> readColorsPerYearAndInstitution() {
        List<PublicationColor> result = entityManager.createNativeQuery(colorsPerInstitutionYearQuery, PublicationColor.class).getResultList();
        return result;
    }

    static final String colorsPerInstitutionYearPublisherQuery = "SELECT color as name, pi.institutionid as institutionId, COUNT(distinct p.id) as value, year, publisherid, mainpublisherid, pi.corr as corr" +
            " FROM publication p left JOIN publication_institution pi ON p.id = pi.publicationid" +
            " where status = " + Publication.STATUS_ACTIVE +
            " GROUP BY pi.institutionid, year, color, publisherid";

    public List<PublicationColor> readColorsPerYearAndInstitutionAndPublisher() {
        List<PublicationColor> result = entityManager.createNativeQuery(colorsPerInstitutionYearPublisherQuery, PublicationColor.class).getResultList();
        return result;
    }

    // the field publisherId is used for the pubtypeId too
    static final String colorsPerYearPublicationTypeQuery = "SELECT color as name, '' as institutionId, COUNT(distinct id) as value, year, pubtypeid as publisherid, mainpublisherid, true as corr" +
            " FROM publication p" +
            " where status = " + Publication.STATUS_ACTIVE +
            " GROUP BY year, color, pubtypeid";

    public List<PublicationColor> readColorsPerYearAndPublicationType() {
        List<PublicationColor> result = entityManager.createNativeQuery(colorsPerYearPublicationTypeQuery, PublicationColor.class).getResultList();
        return result;
    }

    // the field publisherId is used for the pubtypeId too
    static final String colorsPerInstitutionYearPublicationTypeQuery = "SELECT color as name, pi.institutionid as institutionId, COUNT(distinct p.id) as value, year, pubtypeid as publisherid, mainpublisherid, pi.corr as corr" +
            " FROM publication p left JOIN publication_institution pi ON p.id = pi.publicationid" +
            " where status = " + Publication.STATUS_ACTIVE +
            " GROUP BY pi.institutionid, year, color, pubtypeid";

    public List<PublicationColor> readColorsPerYearAndInstitutionAndPublicationType() {
        List<PublicationColor> result = entityManager.createNativeQuery(colorsPerInstitutionYearPublicationTypeQuery, PublicationColor.class).getResultList();
        return result;
    }


    // the field publisherId is used for the licence too
    static final String colorsPerYearLicenceQuery = "SELECT color as name, '' as institutionId, COUNT(distinct id) as value, year, licence as publisherid, mainpublisherid, true as corr" +
            " FROM publication p" +
            " where status = " + Publication.STATUS_ACTIVE +
            " GROUP BY year, color, licence";

    public List<PublicationColor> readColorsPerYearAndLicence() {
        List<PublicationColor> result = entityManager.createNativeQuery(colorsPerYearLicenceQuery, PublicationColor.class).getResultList();
        return result;
    }

    // the field publisherId is used for the licence too
    static final String colorsPerInstitutionYearLicenceQuery = "SELECT color as name, pi.institutionid as institutionId, COUNT(distinct p.id) as value, year, licence as publisherid, mainpublisherid, pi.corr as corr" +
            " FROM publication p left JOIN publication_institution pi ON p.id = pi.publicationid" +
            " where status = " + Publication.STATUS_ACTIVE +
            " GROUP BY pi.institutionid, year, color, licence";

    public List<PublicationColor> readColorsPerYearAndInstitutionAndLicence() {
        List<PublicationColor> result = entityManager.createNativeQuery(colorsPerInstitutionYearLicenceQuery, PublicationColor.class).getResultList();
        return result;
    }

    static final String colorsPerInstitutionYearForPublisherQuery = "SELECT color as name, pi.institutionid as institutionId, COUNT(distinct p.id) as value, year, publisherid as publisherid, mainpublisherid" +
            " FROM publication p left JOIN publication_institution pi ON p.id = pi.publicationid" +
            " where status = " + Publication.STATUS_ACTIVE + " AND publisherid = '%s'" +
            " GROUP BY pi.institutionid, year, color";

    public List<PublicationColor> readColorsPerYearAndInstitutionForPublisher(String publisherId) {
        String sql = String.format(colorsPerInstitutionYearForPublisherQuery, publisherId);
        List<PublicationColor> result = entityManager.createNativeQuery(sql, PublicationColor.class).getResultList();
        return result;
    }

    static final String publishersFromPublicationsQuery = "SELECT publisherid, pub.name, COUNT(publisherid)" +
            " FROM publication p INNER JOIN publisher pub ON p.publisherid = pub.id WHERE STATUS = 0" +
            " GROUP BY publisherid having COUNT(publisherid) > 10";

    public List<Publisher> readPublishersFromPublications() {
        Query query = entityManager.createNativeQuery(publishersFromPublicationsQuery);
        List<Publisher> result = ((List<Object[]>) query.getResultList()).stream().map(o -> new Publisher((String) o[0], (String) o[1], ((BigInteger) o[2]).intValue()))
                .filter(publisher -> publisher.getName() != null).collect(Collectors.toList());
        return result;
    }

}
