package org.aksw.rdfunit.sources;

import com.hp.hpl.jena.query.Query;
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
    private final java.util.Collection<String> sparqlGraph;

    public EndpointTestSource(String prefix, String uri) {
        this(prefix, uri, uri, new ArrayList<String>(), null);
    }

    public EndpointTestSource(String prefix, String uri, String sparqlEndpoint, java.util.Collection<String> sparqlGraph, java.util.Collection<SchemaSource> schemata) {
        super(prefix, uri);
        this.sparqlEndpoint = sparqlEndpoint;
        this.sparqlGraph = new ArrayList<>(sparqlGraph);
        if (schemata != null) {
            addReferencesSchemata(schemata);
        }
    }

    public EndpointTestSource(EndpointTestSource source) {
        this(source.getPrefix(), source.getUri(), source.getSparqlEndpoint(), source.getSparqlGraphs(), source.getReferencesSchemata());
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Dataset;
    }

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
                QueryExecutionFactory _qef = CacheUtilsH2.createQueryExecutionFactory(qef, "./cache/sparql/" + getPrefix(), false, cacheTTL);
                qef = _qef;
                log.debug("Cache for endpoint set up: " + this.getSparqlEndpoint());
            } catch (Exception e) {
                log.debug("Could not instantiate cache for Endpoint" + this.getSparqlEndpoint(), e);
            }
        }

        // Add pagination
        if (this.pagination > 0) {
            qef = new QueryExecutionFactoryPaginated(qef, 900);
        }

        if (this.queryLimit > 0 || this.queryLimit < Query.NOLIMIT) {
            qef = new QueryExecutionFactoryLimit(qef, true, this.queryLimit);
        }

        return qef;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public Collection<String> getSparqlGraphs() {
        return sparqlGraph;
    }

    public void setCacheTTL(long cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    public void setQueryDelay(long queryDelay) {
        this.queryDelay = queryDelay;
    }

    public void setQueryLimit(long queryLimit) {
        this.queryLimit = queryLimit;
    }

    public void setPagination(long pagination) {
        this.pagination = pagination;
    }

    public long getCacheTTL() {
        return cacheTTL;
    }

    public long getQueryDelay() {
        return queryDelay;
    }

    public long getQueryLimit() {
        return queryLimit;
    }

    public long getPagination() {
        return pagination;
    }
}
