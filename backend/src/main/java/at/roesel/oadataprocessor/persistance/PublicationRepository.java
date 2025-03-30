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
import at.roesel.oadataprocessor.model.SourceReference;
import at.roesel.oadataprocessor.model.ui.PublicationColor;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SqlResultSetMapping(
        name = "PublicationColorMapping",
        entities = @EntityResult(
                entityClass = PublicationColor.class,
                fields = {
                        @FieldResult(name = "institutionId", column = "institutionid"),
                        @FieldResult(name = "name", column = "name"),
                        @FieldResult(name = "value", column = "value")}))
public interface PublicationRepository extends CrudRepository<Publication, String>, PublicationRepositoryCustom {
    Iterable<Publication> findAllByDoi(String doi);
    @Query(
            value = "SELECT * FROM publication pub inner join publication_institution pi on pub.id = pi.publicationid WHERE pi.institutionid = ?1",
            nativeQuery = true)
    Iterable<Publication> findAllByInstitution(String institutionId);

    Page<Publication> findAll(Pageable pageable);
    Page<Publication> findByYear(Pageable pageable, int year);

    Page<Publication> findAllByClassificationStatus(Pageable pageable, int classification);

    Page<Publication> findAllByStatus(Pageable pageable, int status);

    @Query(
            value = "SELECT * FROM publication pub inner join publication_institution pi on pub.id = pi.publicationid WHERE pi.institutionid = ?1 and pub.classification =?2",
            nativeQuery = true)
    Page<Publication> findAllByInstitutionAndClassificationStatus(Pageable pageable, String institutionId, int classification);

    Page<Publication> findAllByStatusAndUpdatedAfterOrderByUpdatedAsc(Pageable pageable, int status, long time);

    Page<Publication> findAllByPublisherId(Pageable pageable, String publisherId);

    @Query(
            value = "SELECT * FROM publication WHERE not (publisherid = '' or publisherid = '-up')",
            nativeQuery = true)
    Page<Publication> findAllWithPublisherId(Pageable pageable);
    @Query(
            value = "SELECT * FROM publication WHERE mainpublisherid is null or mainpublisherid = ''",
            nativeQuery = true)
    Page<Publication> findAllEmptyMainPublisherId(Pageable pageable);

    Page<Publication> findAllByJournalId(Pageable pageable, String journalId);
    @Query(
            value = "SELECT * FROM publication WHERE journalId is null or journalId = ''",
            nativeQuery = true)
    Page<Publication> findAllEmptyJournalId(Pageable pageable);

    List<Publication> findAllByTitleLike(String title);

    List<Publication> findAllByTitleHash(int hash);


    @Query(
            value = "SELECT * FROM publication pub inner join publication_institution pi on pub.id = pi.publicationid WHERE pi.sourceid = ?1",
            nativeQuery = true)
    Publication findBySourceId(String sourceId);

    // count all publications
    long count();

    @Modifying
    @Transactional
    @Query("update Publication pub set pub.color = ?2, pub.updated =?3 where pub.id = ?1")
    int updateColor(String id, String color, long updateTime);

    @Modifying
    @Transactional
    @Query("update Publication pub set pub.comment = ?2 where pub.id = ?1")
    int updateComment(String id, String comment);

    @Modifying
    @Transactional
    @Query("update Publication pub set pub.publisherId = ?2, pub.updated =?3 where pub.id = ?1")
    int updatePublisherId(String id, String publisherId, long updateTime);

    @Modifying
    @Transactional
    @Query("update Publication pub set pub.mainPublisherId = ?2, pub.updated =?3 where pub.id = ?1")
    int updateMainPublisherId(String id, String publisherId, long updateTime);

    @Modifying
    @Transactional
    @Query("update Publication pub set pub.journalId = ?2, pub.updated =?3 where pub.id = ?1")
    int updateJournalId(String id, String journalId, long updateTime);

    // -uj = UNKNOWN_JOURNAL
    @Modifying
    @Transactional
    @Query("update Publication pub set pub.journalId = '' WHERE pub.journalId = '-uj'")
    int resetUnknownJournalId();

    // -up = UNKNOWN_PUBLISHER
    @Modifying
    @Transactional
    @Query("update Publication pub set pub.publisherId = '', pub.mainPublisherId = '' WHERE pub.publisherId = '-up'")
    int resetUnknownPublisherId();

    // set new publisherId and reset mainPublisherId
    @Modifying
    @Transactional
    @Query("update Publication pub set pub.publisherId = ?2, pub.mainPublisherId = '' WHERE pub.publisherId = ?1")
    int replacePublisher(String publisherId, String replacementPublisherId);

    @Modifying
    @Transactional
    @Query("update Publication pub set pub.journalId = ?2, pub.updated =?3 where pub.journalId = ?1")
    int replaceJournal(String journalId, String replacementJournalId, long updateTime);

    @Query(
            value = "SELECT * from publication_institution WHERE sourceid = ?1",
            nativeQuery = true)
    List<SourceReference> findAllBySourceId(String sourceId);

}
