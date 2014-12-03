package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.cache.h2.CacheUtilsH2;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.limit.QueryExecutionFactoryLimit;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.rdfunit.enums.TestAppliesTo;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Describes an arbitary datatest source
 * TODO make this abstract and create an EndpointSource and a DumpSource
 *
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:54 PM
 * @version $Id: $Id
 */
public class EndpointTestSource extends Source {


    /**
     * cache time to live (in ms), set to 1 week by default
     */
    public static final long CACHE_TTL = 7l * 24l * 60l * 60l * 1000l;

    /**
     * Pagination for big results, set to 800 records by default
     */
    public static final long PAGINATION = 800;

    /**
     * Delay between queries in a SPARQL Endpoint, set to 5 seconds by default
     */
    public static final long QUERY_DELAY = 5l * 1000l;

    /**
     * Pose a limit on the returned results. Limit to pagination by default
     */
    public static final long QUERY_LIMIT = PAGINATION - 1;


    private long cacheTTL = CACHE_TTL;
    private long queryDelay = QUERY_DELAY;
    private long queryLimit = QUERY_LIMIT;
    private long pagination = PAGINATION;

    private final String sparqlEndpoint;
    private final Collection<String> sparqlGraph;

    /**
     * <p>Constructor for EndpointTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     */
    public EndpointTestSource(String prefix, String uri) {
        this(prefix, uri, uri, new ArrayList<String>(), null);
    }

    /**
     * <p>Constructor for EndpointTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param sparqlEndpoint a {@link java.lang.String} object.
     * @param sparqlGraph a {@link java.util.Collection} object.
     * @param schemata a {@link java.util.Collection} object.
     */
    public EndpointTestSource(String prefix, String uri, String sparqlEndpoint, Collection<String> sparqlGraph, Collection<SchemaSource> schemata) {
        super(prefix, uri);
        this.sparqlEndpoint = sparqlEndpoint;
        this.sparqlGraph = new ArrayList<>(sparqlGraph);
        if (schemata != null) {
            addReferencesSchemata(schemata);
        }
    }

    /**
     * <p>Constructor for EndpointTestSource.</p>
     *
     * @param source a {@link org.aksw.rdfunit.sources.EndpointTestSource} object.
     */
    public EndpointTestSource(EndpointTestSource source) {
        this(source.getPrefix(), source.getUri(), source.getSparqlEndpoint(), source.getSparqlGraphs(), source.getReferencesSchemata());
    }

    /** {@inheritDoc} */
    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Dataset;
    }

    /** {@inheritDoc} */
    @Override
    protected QueryExecutionFactory initQueryFactory() {

        QueryExecutionFactory qef;
        // if empty
        if (getSparqlGraphs() == null || getSparqlGraphs().isEmpty()) {
            qef = new QueryExecutionFactoryHttp(getSparqlEndpoint());
        } else {
            qef = new QueryExecutionFactoryHttp(getSparqlEndpoint(), getSparqlGraphs());
        }

        // Add delay in order to be nice to the remote server (delay in milli seconds)
        if (this.queryDelay > 0) {
            qef = new QueryExecutionFactoryDelay(qef, this.queryDelay);
        }


        if (this.cacheTTL > 0) {

            try {
                qef = CacheUtilsH2.createQueryExecutionFactory(qef, "./cache/sparql/" + getPrefix(), false, cacheTTL);
                log.debug("Cache for endpoint set up: " + this.getSparqlEndpoint());
            } catch (Exception e) {
                log.debug("Could not instantiate cache for Endpoint" + this.getSparqlEndpoint(), e);
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
     * <p>Getter for the field <code>sparqlEndpoint</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    /**
     * <p>getSparqlGraphs.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<String> getSparqlGraphs() {
        return sparqlGraph;
    }

    /**
     * <p>Setter for the field <code>cacheTTL</code>.</p>
     *
     * @param cacheTTL a long.
     */
    public void setCacheTTL(long cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    /**
     * <p>Setter for the field <code>queryDelay</code>.</p>
     *
     * @param queryDelay a long.
     */
    public void setQueryDelay(long queryDelay) {
        this.queryDelay = queryDelay;
    }

    /**
     * <p>Setter for the field <code>queryLimit</code>.</p>
     *
     * @param queryLimit a long.
     */
    public void setQueryLimit(long queryLimit) {
        this.queryLimit = queryLimit;
    }

    /**
     * <p>Setter for the field <code>pagination</code>.</p>
     *
     * @param pagination a long.
     */
    public void setPagination(long pagination) {
        this.pagination = pagination;
    }

    /**
     * <p>Getter for the field <code>cacheTTL</code>.</p>
     *
     * @return a long.
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
