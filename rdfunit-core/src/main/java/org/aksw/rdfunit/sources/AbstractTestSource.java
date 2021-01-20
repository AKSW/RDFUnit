package org.aksw.rdfunit.sources;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Collection;
import org.aksw.jena_sparql_api.cache.h2.CacheUtilsH2;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.limit.QueryExecutionFactoryLimit;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12 /10/14 9:54 AM
 */
public abstract class AbstractTestSource implements TestSource {

  /**
   * Constant <code>log</code>
   */
  protected static final Logger log = LoggerFactory.getLogger(TestSource.class);


  protected final SourceConfig sourceConfig;
  protected final QueryingConfig queryingConfig;
  private final ImmutableSet<SchemaSource> referenceSchemata;

  private QueryExecutionFactory queryFactory = null;


  public AbstractTestSource(SourceConfig sourceConfig, QueryingConfig queryingConfig,
      Collection<SchemaSource> referenceSchemata) {
    this.sourceConfig = checkNotNull(sourceConfig);
    this.queryingConfig = checkNotNull(queryingConfig);
    this.referenceSchemata = ImmutableSet.copyOf(checkNotNull(referenceSchemata));
  }


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
  public QueryExecutionFactory getExecutionFactory() {
    if (queryFactory == null) {
      queryFactory = initQueryFactory();
    }
    return queryFactory;
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


  protected abstract QueryExecutionFactory initQueryFactory();

  protected QueryExecutionFactory masqueradeQEF(QueryExecutionFactory originalQEF,
      TestSource testSource) {

    QueryExecutionFactory qef = originalQEF;

    qef = masqueradeQueryDelayOrOriginal(qef);

    qef = masqueradeCacheTTLOrOriginal(testSource, qef);

    qef = masqueradePaginationOrOriginal(qef);

    qef = masqueradeQueryLimitOrOriginal(qef);

    return qef;
  }

  private QueryExecutionFactory masqueradeQueryDelayOrOriginal(QueryExecutionFactory qef) {
    if (queryingConfig.getQueryDelay() > 0) {
      return new QueryExecutionFactoryDelay(qef, queryingConfig.getQueryDelay());
    }
    return qef;
  }

  private QueryExecutionFactory masqueradeCacheTTLOrOriginal(TestSource testSource,
      QueryExecutionFactory qef) {
    if (queryingConfig.getCacheTTL() > 0) {

      try {
        return CacheUtilsH2
            .createQueryExecutionFactory(qef, "./cache/sparql/" + testSource.getPrefix(), false,
                queryingConfig.getCacheTTL());
      } catch (Exception e) {
        log.warn("Could not instantiate cache for Endpoint {}", testSource.getUri(), e);
      }
    }
    return qef;
  }

  private QueryExecutionFactory masqueradePaginationOrOriginal(QueryExecutionFactory qef) {
    if (queryingConfig.getPagination() > 0) {
      return new QueryExecutionFactoryPaginated(qef, queryingConfig.getPagination());
    }
    return qef;
  }

  private QueryExecutionFactory masqueradeQueryLimitOrOriginal(QueryExecutionFactory qef) {
    if (queryingConfig.getQueryLimit() > 0) {
      return new QueryExecutionFactoryLimit(qef, true, queryingConfig.getQueryLimit());
    }
    return qef;
  }

  @Override
  public void close() throws IOException {
    if (queryFactory != null) {
      try {
        queryFactory.close();
      } catch (Exception e) {
        log.error("Error closing Query Factory", e);
      }
      queryFactory = null;
    }
  }
}
