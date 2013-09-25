package org.aksw.databugger.sources;

import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.jena_sparql_api.cache.core.QueryExecutionFactoryCacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheCoreEx;
import org.aksw.jena_sparql_api.cache.extra.CacheCoreH2;
import org.aksw.jena_sparql_api.cache.extra.CacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheExImpl;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:54 PM
 */
public class DatasetSource extends Source {

    public final String sparqlEndpoint;
    public final String sparqlGraph;

    public DatasetSource(String uri) {
        this(uri,uri,"");
    }

    public DatasetSource(String uri, String sparqlEndpoint, String sparqlGraph) {
        super(uri);
        this.sparqlEndpoint = sparqlEndpoint;
        this.sparqlGraph = sparqlGraph;
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Dataset;
    }

    @Override
    protected QueryExecutionFactory initQueryFactory() {

        // Create a query execution over DBpedia
        QueryExecutionFactory qef = new QueryExecutionFactoryHttp(sparqlEndpoint, sparqlGraph);

        // Add delay in order to be nice to the remote server (delay in milli seconds)
        qef = new QueryExecutionFactoryDelay(qef, 7000);

        QueryExecutionFactory qefBackup = qef;
        try {
            // Set up a cache
            // Cache entries are valid for 1 day
            long timeToLive = 24l * 60l * 60l * 1000l;

            // This creates a 'cache' folder, with a database file named 'sparql.db'
            // Technical note: the cacheBackend's purpose is to only deal with streams,
            // whereas the frontend interfaces with higher level classes - i.e. ResultSet and Model
            CacheCoreEx cacheBackend = CacheCoreH2.create("sparql", timeToLive, true);
            CacheEx cacheFrontend = new CacheExImpl(cacheBackend);
            qef = new QueryExecutionFactoryCacheEx(qef, cacheFrontend);
        } catch (Exception e) {
            //Try to create cache, if fails continue...
            qef = qefBackup;
        }

        // Add pagination
        qef = new QueryExecutionFactoryPaginated(qef, 900);

        return qef;

    }
}
