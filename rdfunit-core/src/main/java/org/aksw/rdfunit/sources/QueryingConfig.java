package org.aksw.rdfunit.sources;

import com.google.common.base.Objects;

/**
 * Querying configuration for a TestSource
 *
 * @author Dimitris Kontokostas
 * @since 8/19/15 7:57 PM
 */
final class QueryingConfig {

    /**
     * cache time to live (in ms), set to 1 week by default for endpoints
     */
    private static final long CACHE_TTL = 7L * 24L * 60L * 60L * 1000L;

    /**
     * Pagination for big results, set to 800 records by default for endpoints
     */
    private static final long PAGINATION = 800;

    /**
     * Delay between queries in a SPARQL Endpoint, set to 5 seconds by default for endpoints
     */
    private static final long QUERY_DELAY = 5L * 1000L;

    /**
     * Pose a limit on the returned results. Limit to pagination by default for endpoints
     */
    private static final long QUERY_LIMIT = PAGINATION - 1;


    private final long cacheTTL;
    private final long queryDelay;
    private final long queryLimit;
    private final long pagination;

    private QueryingConfig(long cacheTTL, long queryDelay, long queryLimit, long pagination) {
        this.cacheTTL = cacheTTL;
        this.queryDelay = queryDelay;
        this.queryLimit = queryLimit;
        this.pagination = pagination;
    }

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    private static QueryingConfig create() {
        return new QueryingConfig(CACHE_TTL, QUERY_DELAY, QUERY_LIMIT, PAGINATION);
    }

    /**
     * <p>create.</p>
     *
     * @param cacheTTL a long.
     * @param queryDelay a long.
     * @param queryLimit a long.
     * @param pagination a long.
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    public static QueryingConfig create(long cacheTTL, long queryDelay, long queryLimit, long pagination) {
        return new QueryingConfig(cacheTTL, queryDelay, queryLimit, pagination);
    }

    /**
     * <p>createEndpoint.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    public static QueryingConfig createEndpoint() {
        return create();
    }

    /**
     * <p>createInMemory.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    public static QueryingConfig createInMemory() {
        return new QueryingConfig(0,0,0,0);
    }

    /**
     * <p>copyWithNewCacheTTL.</p>
     *
     * @param newCacheTTL a long.
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    public QueryingConfig copyWithNewCacheTTL(long newCacheTTL) {return create(newCacheTTL, getQueryDelay(), getQueryLimit(), getPagination());}
    /**
     * <p>copyWithNewQueryDelay.</p>
     *
     * @param newQueryDelay a long.
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    public QueryingConfig copyWithNewQueryDelay(long newQueryDelay) {return create(getCacheTTL(), newQueryDelay, getQueryLimit(), getPagination());}
    /**
     * <p>copyWithNewQueryLimit.</p>
     *
     * @param newQueryLimit a long.
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    public QueryingConfig copyWithNewQueryLimit(long newQueryLimit) {return create(getCacheTTL(), getQueryDelay(), newQueryLimit, getPagination());}
    /**
     * <p>copyWithNewPagination.</p>
     *
     * @param newPagination a long.
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    public QueryingConfig copyWithNewPagination(long newPagination) {return create(getCacheTTL(), getQueryDelay(), getQueryLimit(), newPagination);}



    /**
     * The Cache tTL.
     *
     * @return a long.
     */
    public long getCacheTTL() {
        return cacheTTL;
    }

    /**
     * The Query delay.
     *
     * @return a long.
     */
    public long getQueryDelay() {
        return queryDelay;
    }


    /**
     * The Query limit.
     *
     * @return a long.
     */
    public long getQueryLimit() {
        return queryLimit;
    }

    /**
     * The Pagination.
     *
     * @return a long.
     */
    public long getPagination() {
        return pagination;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hashCode(cacheTTL, queryDelay, queryLimit, pagination);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final QueryingConfig other = (QueryingConfig) obj;
        return Objects.equal(this.cacheTTL, other.cacheTTL)
                && Objects.equal(this.queryDelay, other.queryDelay)
                && Objects.equal(this.queryLimit, other.queryLimit)
                && Objects.equal(this.pagination, other.pagination);
    }
}
