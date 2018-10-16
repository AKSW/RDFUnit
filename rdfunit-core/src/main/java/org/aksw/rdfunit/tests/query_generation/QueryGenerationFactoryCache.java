package org.aksw.rdfunit.tests.query_generation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.query.Query;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QueryGenerationFactoryCache implements QueryGenerationFactory {
    private final QueryGenerationFactory delegate;
    private final LoadingCache<TestCase, Query> queryCache;

    private static final int DEFAULT_CACHE_SIZE = 10000;
    private static final int DEFAULT_EXPIRATION_MINUTES = 30;

    public QueryGenerationFactoryCache(QueryGenerationFactory delegate) {
        this(delegate, DEFAULT_CACHE_SIZE, DEFAULT_EXPIRATION_MINUTES);
    }
    public QueryGenerationFactoryCache(QueryGenerationFactory delegate, int cache_size) {
        this(delegate, cache_size, DEFAULT_EXPIRATION_MINUTES);
    }

    public QueryGenerationFactoryCache(QueryGenerationFactory delegate, int cache_size, int expire_after_minutes) {
        this.delegate = delegate;
        this.queryCache = CacheBuilder.newBuilder()
                .maximumSize(cache_size)
                .expireAfterAccess(expire_after_minutes, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<TestCase, Query>() {
                            public Query load(TestCase testCase) {
                                return delegate.getSparqlQuery(testCase);
                            }
                        });
    }

    @Override
    public String getSparqlQueryAsString(TestCase testCase) {
        return delegate.getSparqlQueryAsString(testCase);
    }

    @Override
    public Query getSparqlQuery(TestCase testCase) {
        try {
            return queryCache.get(testCase);
        } catch (ExecutionException e) {
            log.error("Error retrieving query from cache", e);
            throw new IllegalStateException("Error retrieving query from cache", e);
        }
    }
}
