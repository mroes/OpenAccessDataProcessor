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

package at.roesel.oadataprocessor.persistance.elastic;

import at.roesel.oadataprocessor.components.controller.PublicationSearchFilter;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import java.util.ArrayList;
import java.util.List;

import static at.roesel.common.StringSupport.hasValue;
import static at.roesel.oadataprocessor.model.ui.PublicationColor.UNKNOWN;

public class QuerySupport {

    public static final String ALL_INSTITUTIONS = "all";
    public static final String ALL_PUBLISHERS = "all";

    public static Query buildQueryForFilter(PublicationSearchFilter filter) {

        List<Query> queries = new ArrayList<>();
        List<Query> mustNotQueries = new ArrayList<>();

        // When searching for a DOI, it is the only filter
        if (hasValue(filter.doi) ) {
            Query q = new Query.Builder().term(t ->
                    t.field("doi").value(filter.doi).caseInsensitive(true)).build();
            queries.add(q);
        } else {

            if (hasValue(filter.institution) && !ALL_INSTITUTIONS.equals(filter.institution)) {
                Query institutionQuery = new Query.Builder().term(t ->t.field("sources.institutionId").value(filter.institution)).build();
                NestedQuery nestedQuery = new NestedQuery.Builder()
                        .path("sources")
                        .query(b -> b.bool(builder -> builder.filter(institutionQuery))).build();
                queries.add(new Query(nestedQuery));
          }
            if (filter.year > 0) {
                Query q = new Query.Builder().term(t ->
                        t.field("year").value(filter.year)).build();
                queries.add(q);
            }
            if (hasValue(filter.color)) {
                Query q = new Query.Builder().term(t ->
                        t.field("color").value(filter.color).caseInsensitive(true)).build();
                queries.add(q);
            }
            if (!filter.includeUnknown) {
                Query q = new Query.Builder().term(t ->
                        t.field("color").value(UNKNOWN).caseInsensitive(true)).build();
                mustNotQueries.add(q);
            }
            if (hasValue(filter.upwColor)) {
                Query q = new Query.Builder().term(t ->
                        t.field("upwColor").value(filter.upwColor).caseInsensitive(true)).build();
                queries.add(q);
            }
            if (hasValue(filter.publisherId)  && !ALL_PUBLISHERS.equals(filter.publisherId)) {
                String publisherField;
                if (filter.filterMainPublisher) {
                    publisherField = "mainPublisher.id";
                } else {
                    publisherField = "publisher.id";
                }
                Query q = new Query.Builder().term(t -> t.field(publisherField).value(filter.publisherId)).build();
                queries.add(q);
            }
            if (hasValue(filter.journal)) {
                Query q = new Query.Builder().match(t ->
                        t.field("journal.title").query(filter.journal)).build();
                queries.add(q);
            }
            if (hasValue(filter.title)) {
                Query q = new Query.Builder().match(t ->
                        t.field("title").query(filter.title)
                                //                                                .fuzziness("0")
                                // use And to search for all words in title
                                .operator(Operator.And)).build();

                queries.add(q);
            }
            if (hasValue(filter.author)) {
                // We expect either just the last name or the last name followed by a blank space and the first name.
                String[] parts = filter.author.split(" ");

                List<Query> subQueries = new ArrayList<>();
                String lastName = parts[0];
                if (!lastName.isEmpty()) {
                    subQueries.add(new Query.Builder().term(t -> t.field("authors.lastName").value(lastName).caseInsensitive(true)).build());
                }
                if (parts.length > 1) {
                    String foreName = parts[1];
                    if (!foreName.isEmpty()) {
                        subQueries.add(new Query.Builder().term(t -> t.field("authors.firstName").value(foreName).caseInsensitive(true)).build());
                    }
                }
                if (!subQueries.isEmpty()) {
                    NestedQuery nestedQuery = new NestedQuery.Builder()
                            .path("authors")
                            .query(b -> b.bool(builder -> builder.filter(subQueries))).build();
                    queries.add(new Query(nestedQuery));
                }
            }
        }

        Query mainQuery = new Query.Builder()
                .bool(b -> b.filter(queries)
                             .mustNot(mustNotQueries)
                )
                .build();

        return mainQuery;
    }
}
