package org.aksw.rdfunit.sources;

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
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

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

public class EndpointSource extends Source {

    private final String sparqlEndpoint;
    private final java.util.Collection<String> sparqlGraph;

    public EndpointSource(String prefix, String uri) {
        this(prefix, uri, uri, new ArrayList<String>(), null);
    }

    public EndpointSource(String prefix, String uri, String sparqlEndpoint, java.util.Collection<String> sparqlGraph, java.util.Collection<SchemaSource> schemata) {
        super(prefix, uri);
        this.sparqlEndpoint = sparqlEndpoint;
        this.sparqlGraph = new ArrayList<>(sparqlGraph);
        if (schemata != null) {
            addReferencesSchemata(schemata);
        }
    }

    public EndpointSource(EndpointSource source) {
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
        qef = new QueryExecutionFactoryDelay(qef, 15);

        QueryExecutionFactory qefBackup = qef;

        try {
            // Copied from
            // https://github.com/AKSW/jena-sparql-api/blob/master/jena-sparql-api-cache-h2/src/test/java/org/aksw/jena_sparql_api/SparqlTest.java
            //
            // Set up a cache
            // Cache entries are valid for 7 days
            long timeToLive = 7l * 24l * 60l * 60l * 1000l;

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


            CacheBackendDao dao = new CacheBackendDaoPostgres();
            CacheBackend cacheBackend = new CacheBackendDataSource(dataSource, dao);
            CacheFrontend cacheFrontend = new CacheFrontendImpl(cacheBackend);
            qef = new QueryExecutionFactoryCacheEx(qef, cacheFrontend);
        } catch (Exception e) {
            //Try to create cache, if fails continue...
            qef = qefBackup;
        }

        // Add pagination
        qef = new QueryExecutionFactoryPaginated(qef, 900);

        return qef;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public Collection<String> getSparqlGraphs() {
        return sparqlGraph;
    }
}
