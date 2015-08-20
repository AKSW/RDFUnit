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
 * @version $Id: $Id
 */
public abstract class AbstractTestSource implements TestSource {
    /** Constant <code>log</code> */
    protected static final Logger log = LoggerFactory.getLogger(TestSource.class);


    protected final SourceConfig sourceConfig;
    protected final QueryingConfig queryingConfig;
    protected final Collection<SchemaSource> referenceSchemata;

    private QueryExecutionFactory queryFactory = null;


    /**
     * <p>Constructor for AbstractTestSource.</p>
     *
     * @param sourceConfig a {@link org.aksw.rdfunit.sources.SourceConfig} object.
     * @param queryingConfig a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     * @param referenceSchemata a {@link java.util.Collection} object.
     */
    public AbstractTestSource(SourceConfig sourceConfig, QueryingConfig queryingConfig, Collection<SchemaSource> referenceSchemata) {
        this.sourceConfig = checkNotNull(sourceConfig);
        this.queryingConfig = checkNotNull(queryingConfig);
        this.referenceSchemata = Collections.unmodifiableCollection(checkNotNull(referenceSchemata));
    }

    /**
     * <p>initQueryFactory.</p>
     *
     * @return a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     */
    abstract protected QueryExecutionFactory initQueryFactory();


    /** {@inheritDoc} */
    @Override
    public QueryingConfig getQueryingConfig() {
        return queryingConfig;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<SchemaSource> getReferencesSchemata() {
        return referenceSchemata;
    }

    /** {@inheritDoc} */
    @Override
    public String getPrefix() {
        return sourceConfig.getPrefix();
    }

    /** {@inheritDoc} */
    @Override
    public String getUri() {
        return sourceConfig.getUri();
    }

    /** {@inheritDoc} */
    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Dataset;
    }

    /** {@inheritDoc} */
    @Override
    public QueryExecutionFactory getExecutionFactory() {
        if (queryFactory == null) {
            queryFactory = initQueryFactory();
        }
        return queryFactory;
    }

    /**
     * <p>masqueradeQEF.</p>
     *
     * @param originalQEF a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @param testSource a {@link org.aksw.rdfunit.sources.TestSource} object.
     * @return a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     */
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

    /**
     * <p>getCacheTTL.</p>
     *
     * @return a long.
     */
    public long getCacheTTL() {
        return queryingConfig.getCacheTTL();
    }

    /**
     * <p>getQueryDelay.</p>
     *
     * @return a long.
     */
    public long getQueryDelay() {
        return queryingConfig.getQueryDelay();
    }

    /**
     * <p>getQueryLimit.</p>
     *
     * @return a long.
     */
    public long getQueryLimit() {
        return queryingConfig.getQueryLimit();
    }

    /**
     * <p>getPagination.</p>
     *
     * @return a long.
     */
    public long getPagination() {
        return queryingConfig.getPagination();
    }


}
