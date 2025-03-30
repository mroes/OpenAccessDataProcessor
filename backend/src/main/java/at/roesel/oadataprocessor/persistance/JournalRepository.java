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

import at.roesel.oadataprocessor.model.Journal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JournalRepository extends CrudRepository<Journal, String> {
    Journal findByWikiId(String wikiId);
    Journal findByNameLike(String name);
    List<Journal> findAllByNameLike(String name);
    List<Journal> findAllByNameLikeAndFlag(String name, int flag);
    List<Journal> findAllByFlag(int flag);
    @Query(
            value = "SELECT * FROM journal WHERE issnl = ?1 or issn like %?1%",
            nativeQuery = true)
    List<Journal> findAllByIssn(String issn);

    @Query(
            value = "SELECT distinct j.* FROM journal j INNER join publication p ON j.id = p.journalid",
            nativeQuery = true)
    List<Journal> findAllUsedJournals();

    @Modifying
    @Transactional
    @Query("update Journal jou set jou.wikiPublisherId = ?2, jou.publisherId =?3 where jou.wikiPublisherId = ?1")
    int replaceWikidataJournal(String wikidataId, String replacementWikidataId, String replacementPublisherId);

    @Modifying
    @Transactional
    @Query("update JournalVar jou set jou.wikiPublisherId = ?2, jou.publisherId =?3 where jou.wikiPublisherId = ?1")
    int replaceWikidataJournalVar(String wikidataId, String replacementWikidataId, String replacementPublisherId);

}
