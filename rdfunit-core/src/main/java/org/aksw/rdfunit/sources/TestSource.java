package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.cache.h2.CacheUtilsH2;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.limit.QueryExecutionFactoryLimit;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12 /10/14 9:54 AM
 */
public abstract class TestSource extends Source {


    /**
     * cache time to live (in ms), set to 1 week by default for endpoints
     */
    public static final long CACHE_TTL = 7l * 24l * 60l * 60l * 1000l;

    /**
     * Pagination for big results, set to 800 records by default for endpoints
     */
    public static final long PAGINATION = 800;

    /**
     * Delay between queries in a SPARQL Endpoint, set to 5 seconds by default for endpoints
     */
    public static final long QUERY_DELAY = 5l * 1000l;

    /**
     * Pose a limit on the returned results. Limit to pagination by default for endpoints
     */
    public static final long QUERY_LIMIT = PAGINATION - 1;

    /**
     * The Cache tTL.
     */
    protected long cacheTTL = CACHE_TTL;
    /**
     * The Query delay.
     */
    protected long queryDelay = QUERY_DELAY;
    /**
     * The Query limit.
     */
    protected long queryLimit = QUERY_LIMIT;
    /**
     * The Pagination.
     */
    protected long pagination = PAGINATION;

    /**
     * Instantiates a new Test source.
     *
     * @param prefix the prefix
     * @param uri the uri
     */
    public TestSource(String prefix, String uri) {
        super(prefix, uri);
    }

    /**
     * Instantiates a new Test source.
     *
     * @param source the source
     */
    public TestSource(Source source) {
        super(source);
    }

    /**
     * Masquerade QEF.
     *
     * @param originalQEF the original qef
     * @return the masqueraded qef
     */
    protected QueryExecutionFactory masqueradeQEF(QueryExecutionFactory originalQEF) {

        QueryExecutionFactory qef = originalQEF;

        // Add delay in order to be nice to the remote server (delay in milli seconds)
        if (this.queryDelay > 0) {
            qef = new QueryExecutionFactoryDelay(qef, this.queryDelay);
        }

        if (this.cacheTTL > 0) {

            try {
                qef = CacheUtilsH2.createQueryExecutionFactory(qef, "./cache/sparql/" + getPrefix(), false, cacheTTL);
                log.debug("Cache for endpoint set up: " + this.getUri());
            } catch (Exception e) {
                log.debug("Could not instantiate cache for Endpoint" + this.getUri(), e);
            }
        }

        // Add pagination
        if (this.pagination > 0) {
            qef = new QueryExecutionFactoryPaginated(qef, this.pagination);
        }

        if (this.queryLimit > 0) {
            qef = new QueryExecutionFactoryLimit(qef, true, this.queryLimit);
        }

        return qef;
    }

    /**
     * set Cache TTL
     *
     * @param cacheTTL the cache TTL
     */
    public void setCacheTTL(long cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    /**
     * set Query delay (builder).
     *
     * @param queryDelay the query delay
     */
    public void setQueryDelay(long queryDelay) {
        this.queryDelay = queryDelay;
    }

    /**
     * set Query limit.
     *
     * @param queryLimit the query limit
     */
    public void setQueryLimit(long queryLimit) {
        this.queryLimit = queryLimit;
    }

    /**
     * set Pagination.
     *
     * @param pagination the pagination
     */
    public void setPagination(long pagination) {
        this.pagination = pagination;
    }

    /**
     * Gets cache tTL.
     *
     * @return the cache tTL
     */
    public long getCacheTTL() {
        return cacheTTL;
    }

    /**
     * <p>Getter for the field <code>queryDelay</code>.</p>
     *
     * @return a long.
     */
    public long getQueryDelay() {
        return queryDelay;
    }

    /**
     * <p>Getter for the field <code>queryLimit</code>.</p>
     *
     * @return a long.
     */
    public long getQueryLimit() {
        return queryLimit;
    }

    /**
     * <p>Getter for the field <code>pagination</code>.</p>
     *
     * @return a long.
     */
    public long getPagination() {
        return pagination;
    }
}
