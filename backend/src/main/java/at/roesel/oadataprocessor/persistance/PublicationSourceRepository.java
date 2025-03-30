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

import at.roesel.oadataprocessor.model.PublicationSource;
import at.roesel.oadataprocessor.model.PublicationSourceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PublicationSourceRepository extends CrudRepository<PublicationSource, String>,
        PagingAndSortingRepository<PublicationSource, String> {
    Iterable<PublicationSource> findAllByInstitution(String institution);
    PublicationSource findByInstitutionAndNativeId(String institution, String nativeId);
    List<PublicationSource> findAllByInstitutionAndNativeId(String institution, String nativeId);
    Iterable<PublicationSource> findAllByInstitutionAndStatus(String institution, PublicationSourceStatus status);
    PublicationSource findByDoi(String doi);

    Page<PublicationSource> findAllByInstitution(Pageable pageable, String institution);

    Page<PublicationSource> findAllByStatus(Pageable pageable, PublicationSourceStatus status);
    Page<PublicationSource> findAllByInstitutionAndStatus(Pageable pageable, String institution, PublicationSourceStatus status);

    Page<PublicationSource> findAllByDoiLike(Pageable pageable, String doiMask);

    @Query(value="SELECT * from source s join publication_institution PI ON s.id = PI.sourceid WHERE PI.publicationid = :publicationId", nativeQuery=true)
    List<PublicationSource> findSourceByPublicationId(String publicationId);

    @Modifying
    @Transactional
    @Query("update PublicationSource source set source.pubtype = ?2 where source.id = ?1")
    int updatePubtype(String id, String pubtype);

    @Modifying
    @Transactional
    @Query("update PublicationSource source set source.affiliated = ?2 where source.id = ?1")
    int updateAffiliated(String id, int affiliated);

    @Modifying
    @Transactional
    @Query("update PublicationSource source set source.year = ?2 where source.id = ?1")
    int updateYear(String id, int year);

    @Query(value="SELECT * FROM source GROUP BY institution, nativeid HAVING COUNT(nativeid) > 1", nativeQuery=true)
    List<PublicationSource> findDuplicatesForNativeId();

}
