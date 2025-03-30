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

import at.roesel.oadataprocessor.model.Publisher;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PublisherRepository extends CrudRepository<Publisher, String> {

    Publisher findByWikiId(String wikiId);
    Publisher findByNameLike(String name);
    List<Publisher> findAllByNameLike(String name);
    List<Publisher> findAllByFlag(int flag);
    List<Publisher> findAllByNameLikeAndFlag(String name, int flag);

    List<Publisher> findAllByWikiInstanceOfLikeAndFlag(String instanceOf, int flag);

    @Query(value="SELECT * from publisher WHERE flag = 1 and name = wikiid", nativeQuery=true)
    List<Publisher> findAllWithoutLabel();

    // find all publishers that are main publishers in publications
    @Query(value="SELECT DISTINCT p.* FROM publication pub JOIN publisher p ON p.id = pub.mainpublisherid WHERE YEAR > 2000", nativeQuery=true)
    List<Publisher> findAllMainPublishersWithPublications();

    // find all publishers that are only direct publishers in publications
    @Query(value="SELECT DISTINCT p.* FROM publication pub JOIN publisher p ON p.id = pub.publisherid" +
            " WHERE YEAR > 2000 and pub.publisherid NOT IN (SELECT DISTINCT mainpublisherid FROM publication)", nativeQuery=true)
    List<Publisher> findAllDirectPublishersWithPublications();

    @Modifying
    @Transactional
    @Query("update Publisher pub set pub.wikiParentId = ?2, pub.parentId =?3 where pub.wikiParentId = ?1")
    int replaceWikidataPublisher(String wikidataId, String replacementWikidataId, String replacementPublisherId);

    @Modifying
    @Transactional
    @Query("update PublisherVar pub set pub.wikiParentId = ?2, pub.parentId =?3 where pub.wikiParentId = ?1")
    int replaceWikidataPublisherVar(String wikidataId, String replacementWikidataId, String replacementPublisherId);

}
