package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.cache.h2.CacheUtilsH2;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.limit.QueryExecutionFactoryLimit;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12 /10/14 9:54 AM
 */
public abstract class AbstractTestSource implements TestSource {
    protected static final Logger log = LoggerFactory.getLogger(TestSource.class);


    protected final SourceConfig sourceConfig;
    protected final QueryingConfig queryingConfig;
    protected final Collection<SchemaSource> referenceSchemata;

    private QueryExecutionFactory queryFactory = null;


    public AbstractTestSource(SourceConfig sourceConfig, QueryingConfig queryingConfig, Collection<SchemaSource> referenceSchemata) {
        this.sourceConfig = checkNotNull(sourceConfig);
        this.queryingConfig = checkNotNull(queryingConfig);
        this.referenceSchemata = Collections.unmodifiableCollection(checkNotNull(referenceSchemata));
    }

    abstract protected QueryExecutionFactory initQueryFactory();


    @Override
    public QueryingConfig getQueryingConfig() {
        return queryingConfig;
    }

    @Override
    public Collection<SchemaSource> getReferencesSchemata() {
        return referenceSchemata;
    }

    @Override
    public String getPrefix() {
        return sourceConfig.getPrefix();
    }

    @Override
    public String getUri() {
        return sourceConfig.getUri();
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Dataset;
    }

    @Override
    public synchronized QueryExecutionFactory getExecutionFactory() {
        if (queryFactory == null) {
            queryFactory = initQueryFactory();
        }
        return queryFactory;
    }

    protected QueryExecutionFactory masqueradeQEF(QueryExecutionFactory originalQEF, TestSource testSource) {

        QueryExecutionFactory qef = originalQEF;

        // Add delay in order to be nice to the remote server (delay in milli seconds)
        if (queryingConfig.getQueryDelay() > 0) {
            qef = new QueryExecutionFactoryDelay(qef, queryingConfig.getQueryDelay());
        }

        if (queryingConfig.getCacheTTL() > 0) {

            try {
                qef = CacheUtilsH2.createQueryExecutionFactory(qef, "./cache/sparql/" + testSource.getPrefix(), false, queryingConfig.getCacheTTL());
                log.debug("Cache for endpoint set up: " + testSource.getUri());
            } catch (Exception e) {
                log.debug("Could not instantiate cache for Endpoint" + testSource.getUri(), e);
            }
        }

        // Add pagination
        if (queryingConfig.getPagination() > 0) {
            qef = new QueryExecutionFactoryPaginated(qef, queryingConfig.getPagination());
        }

        if (queryingConfig.getQueryLimit() > 0) {
            qef = new QueryExecutionFactoryLimit(qef, true, queryingConfig.getQueryLimit());
        }

        return qef;
    }

    public long getCacheTTL() {
        return queryingConfig.getCacheTTL();
    }

    public long getQueryDelay() {
        return queryingConfig.getQueryDelay();
    }

    public long getQueryLimit() {
        return queryingConfig.getQueryLimit();
    }

    public long getPagination() {
        return queryingConfig.getPagination();
    }


}
