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

import at.roesel.oadataprocessor.config.ElasticsearchSettings;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ElasticClientImpl implements ElasticClient {

    private final static String schema = "https";
    private static final Integer MAX_ITEMS = 1000;

    private final Logger logger = LoggerFactory.getLogger(ElasticClientImpl.class);

    protected final ElasticsearchClient client;

    public ElasticClientImpl(ElasticsearchSettings settings) {

        SSLContext sslContext;
        if (settings.getPathToCaCert() != null && !settings.getPathToCaCert().isEmpty()) {
            File certFile = new File(settings.getPathToCaCert());
            try {
                sslContext =TransportUtils.sslContextFromHttpCaCrt(certFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            sslContext = TransportUtils.sslContextFromCaFingerprint(settings.getFingerprint());
        }

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(settings.getUser(), settings.getPassword())
        );


        RestClient restClient = RestClient
                .builder(new org.apache.http.HttpHost(settings.getHost(), settings.getPort(), schema))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .build();


        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        client = new ElasticsearchClient(transport);
    }

    public <T> T readById(Class<T> clazz, String index, String id) {
        try {
            GetResponse<T> response = client.get(g -> g
                            .index(index)
                            .id(id),
                    clazz
            );
            if (response.found()) {
                T item = response.source();
                return item;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public <T> List<T> search(Class<T> clazz, String index, Query query) {
        ElasticCollectResultHandler<T> handler = new ElasticCollectResultHandler<>();
        search(clazz, index, query, handler);
        return handler.getItems();
    }

    @Override
    public <T> void search(Class<T> clazz, String index, Query query, ElasticResultHandler<T> resultHandler) {

        // preference for ElasticSearch for consistency, so that all queries in a call will use the same shard
        // https://www.elastic.co/guide/en/elasticsearch/reference/current/consistent-scoring.html
        String preference = UUID.randomUUID().toString();

        SearchResponse<T> response;
        try {
            boolean repeat = true;
            List<FieldValue> search_afterValues = null;
            int count = 0;
            do {
                SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
                        .index(index)
                        .query(query)
                        .preference(preference)
                        // to sort by index order
                        .sort(sort -> sort.field(f -> f.field("_doc").order(SortOrder.Asc)))
                        .size(MAX_ITEMS);
                if (search_afterValues != null) {
                    searchRequestBuilder.searchAfter(search_afterValues);
                }

                response = client.search(searchRequestBuilder.build(), clazz);

                if (count == 0) {
                    TotalHits total = response.hits().total();
                    if (total != null) {
                        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;
                        if (isExactResult) {
                            logger.debug("There are {} results", total.value());
                        } else {
                            logger.debug("There are more than {} results", total.value());
                        }
                    }
                }

                List<Hit<T>> hits = response.hits().hits();
                for (Hit<T> hit : hits) {
                    T item = hit.source();
                    resultHandler.handle(item);
                }
                if (hits.isEmpty()) {
                    repeat = false;
                } else {
                    search_afterValues = hits.get(hits.size()-1).sort();
                }
                count++;
            } while (repeat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    public <T> List<T> pagedSearch(Class<T> clazz, String index, Query query, int limit, ElasticCursor cursor) {

        // enforce API limit
        if (limit == 0 || limit > 2000) {
            limit = 200;
        }

        if (cursor == null) {
            throw new RuntimeException("pagedSearch: cursor must not be null");
        }

        List<FieldValue> search_afterValues = cursor.getSearch_afterValues();

        try {
            List<T> items = new ArrayList<>();
            SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
                    .index(index)
                    .query(query)
                    .preference(cursor.getPreference())
                    // to sort by index order
                    .sort(sort -> sort.field(f -> f.field("_doc").order(SortOrder.Asc)))
                    .size(limit);
            if (search_afterValues != null) {
                searchRequestBuilder.searchAfter(search_afterValues);
            }

            SearchResponse<T> response = client.search(searchRequestBuilder.build(), clazz);

            List<Hit<T>> hits = response.hits().hits();
            for (Hit<T> hit : hits) {
                T item = hit.source();
                items.add(item);
            }
            if (hits.isEmpty()) {
                cursor.setSearch_afterValues(null);
            } else {
                cursor.setSearch_afterValues(hits.get(hits.size()-1).sort());
            }
            return items;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> search(Class<T> clazz, String index, String fieldName, String searchText) {

        Query query1 = new Query.Builder()
                .bool(b -> b.
                        must(f -> f.term(t ->
                                t.field(fieldName).value(searchText))
                        )
                )
                .build();

        return search(clazz, index, query1);
    }

    public CreateIndexResponse createIndexRequest(CreateIndexRequest createIndexRequest) {
        try {
            return client.indices().create(createIndexRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> IndexResponse store(T item, String id, String index) {
        try {
            IndexResponse response = client.index(i -> i
                    .index(index)
                    .id(id)
                    .document(item)
            );
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void store(List<T> items, Function<T, String> idProvider, String index) {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (T item : items) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(index)
                            .id(idProvider.apply(item))
                            .document(item)
                    )
            );
        }

        try {
            BulkResponse result = client.bulk(br.build());
            // Log errors, if any
            if (result.errors()) {
                logger.error("Elastic bulk storage had errors");
                for (BulkResponseItem item: result.items()) {
                    if (item.error() != null) {
                        logger.error(item.error().reason());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
