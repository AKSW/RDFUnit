package org.aksw.rdfunit.sources;

import com.hp.hpl.jena.query.Query;
import org.aksw.jena_sparql_api.cache.core.QueryExecutionFactoryCacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheBackend;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontend;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontendImpl;
import org.aksw.jena_sparql_api.cache.staging.CacheBackendDao;
import org.aksw.jena_sparql_api.cache.staging.CacheBackendDaoPostgres;
import org.aksw.jena_sparql_api.cache.staging.CacheBackendDataSource;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.limit.QueryExecutionFactoryLimit;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
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


        if (this.cacheTTL > 0 ) {

            try {
                // Copied from
                // https://github.com/AKSW/jena-sparql-api/blob/master/jena-sparql-api-cache-h2/src/test/java/org/aksw/jena_sparql_api/SparqlTest.java

                Class.forName("org.h2.Driver");

                JdbcDataSource dataSource = new JdbcDataSource();
                dataSource.setURL("jdbc:h2:file:./cache/sparql/" + getPrefix() + ";DB_CLOSE_DELAY=-1");
                dataSource.setUser("sa");
                dataSource.setPassword("sa");

                String schemaResourceName = "/org/aksw/jena_sparql_api/cache/cache-schema-pgsql.sql";
                InputStream in = CacheBackendDao.class.getResourceAsStream(schemaResourceName);

                if (in == null) {
                    throw new RuntimeException("Failed to load resource: " + schemaResourceName);
                }

                InputStreamReader reader = new InputStreamReader(in);
                Connection conn = dataSource.getConnection();
                try {
                    RunScript.execute(conn, reader);
                } finally {
                    conn.close();
                }

                CacheBackendDao dao = new CacheBackendDaoPostgres(this.cacheTTL);
                CacheBackend cacheBackend = new CacheBackendDataSource(dataSource, dao);
                CacheFrontend cacheFrontend = new CacheFrontendImpl(cacheBackend);
                qef = new QueryExecutionFactoryCacheEx(qef, cacheFrontend);
                log.debug("Cache for endpoint set up: " + this.getSparqlEndpoint());

            } catch (Exception e) {
                //Try to create cache, if fails continue...
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
}
