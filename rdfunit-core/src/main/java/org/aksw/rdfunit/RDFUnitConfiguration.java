package org.aksw.rdfunit;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import java.util.*;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.sources.EnrichedSchemaSource;
import org.aksw.rdfunit.sources.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.sources.TestSourceFactory;
import org.aksw.rdfunit.statistics.NamespaceStatistics;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.aksw.rdfunit.utils.UriToPathUtils;

/**
 * Holds a configuration for a complete test TODO: Got too big, maybe break it down a bit TODO: Got
 * really really big!!!
 *
 * @author Dimitris Kontokostas
 * @since 11 /15/13 11:50 AM
 */
public class RDFUnitConfiguration {

  /* Main Dataset URI (also used for loading dataset tests) */
  private final String datasetURI;
  /* folder where we store tests / configuration.  */
  private final String dataFolder;
  private final String testFolder;
  private String prefix = null;
  /* SPARQL endpoint configuration */
  private long endpointQueryDelayMS = -1;
  private long endpointQueryCacheTTL = -1;
  private long endpointQueryPagination = -1;
  private long endpointQueryLimit = -1;

  /* Dereference testing (if different from datasetURI) */
  private String customDereferenceURI = null;

  /* used to cache the test source when we initially do stats for auto loading test cases */
  private TestSource testSource = null;
  private TestSourceBuilder testSourceBuilder = new TestSourceBuilder();

  /* list of schemas for testing a dataset */
  private Collection<SchemaSource> schemas = null;

  /* list of schemas always to be excluded from test generation */
  private ArrayList<SchemaSource> excludeSchemata = Lists.newArrayList();

  private EnrichedSchemaSource enrichedSchema = null;

  /* use the cache for loading tests (do not regenerate if already exists) */
  private boolean testCacheEnabled = true;

  /* if set to false it will generate only schema based test cases */
  private boolean manualTestsEnabled = true;

  /* if set to false it will load only manual test cases */
  private boolean autoTestsEnabled = true;

  /* if set to true, in addition to the schemata provided or discovered, all transitively discovered import schemata (owl:imports) are included into the schema set */
  private boolean augmentWithOwlImports = false;

  /* Execution type */
  private TestCaseExecutionType testCaseExecutionType = TestCaseExecutionType.aggregatedTestCaseResult;

  /* Output format types*/
  private Collection<SerializationFormat> outputFormats = null;

  /*  */
  private boolean calculateCoverageEnabled = false;

  public RDFUnitConfiguration(String datasetURI, String dataFolder) {
    this(datasetURI, dataFolder, dataFolder + "tests/");
  }

  public RDFUnitConfiguration(String datasetURI, String dataFolder, String testFolder) {
    this.datasetURI = datasetURI;
    this.dataFolder = dataFolder;
    this.testFolder = testFolder;

    prefix = UriToPathUtils.getAutoPrefixForURI(datasetURI); // default prefix
    setExcludeSchemataFromPrefixes(
        Arrays.asList("rdf", "rdfs", "owl", "rdfa")); // set default excludes
  }

  public void setEndpointConfiguration(String endpointURI, Collection<String> endpointGraphs,
      String username, String password) {
    this.testSourceBuilder.setEndpoint(endpointURI, endpointGraphs, username, password);
  }

  public void setCustomTextSource(String text, String format)
      throws UndefinedSerializationException {
    testSourceBuilder.setInMemFromCustomText(text, format);
  }

  public void setAutoSchemataFromQEF(QueryExecutionFactory qef) {
    setAutoSchemataFromQEF(qef, false);
  }

  public void setAutoSchemataFromQEF(QueryExecutionFactory qef, boolean all) {
    setAutoSchemataFromQEF(qef, all, true);
  }

  public void setAutoSchemataFromQEF(QueryExecutionFactory qef, boolean all, boolean limitToKnown) {

    NamespaceStatistics namespaceStatistics;
    if (all) {
      namespaceStatistics = limitToKnown ? NamespaceStatistics.createCompleteNSStatisticsKnown(this)
          : NamespaceStatistics.createCompleteNSStatisticsAll(this);
    } else {
      namespaceStatistics = limitToKnown ? NamespaceStatistics.createOntologyNSStatisticsKnown(this)
          : NamespaceStatistics.createOntologyNSStatisticsAll(this);
    }
    checkNotNull(namespaceStatistics);
    setSchemata(namespaceStatistics.getNamespaces(qef));
  }

  public void setSchemataFromPrefixes(Collection<String> schemaPrefixes)
      throws UndefinedSchemaException {
    this.setSchemata(SchemaService.getSourceList(testFolder, schemaPrefixes));
  }

  public void setSchemata(Collection<SchemaSource> schemata) {
    this.schemas = new ArrayList<>();
    this.schemas.addAll(schemata);
  }

  public boolean isAugmentWithOwlImports() {
    return augmentWithOwlImports;
  }

  public void setAugmentWithOwlImports(boolean augmentWithOwlImports) {
    this.augmentWithOwlImports = augmentWithOwlImports;
  }

  public void setExcludeSchemataFromPrefixes(Collection<String> schemaPrefixes) {
    this.excludeSchemata = new ArrayList<>();
    for (String prefix : schemaPrefixes) {
      Optional<SchemaSource> ss = SchemaService.getSourceFromPrefix(prefix);
      ss.ifPresent(schemaSource -> this.excludeSchemata.add(schemaSource));
    }
  }

  public Collection<SchemaSource> getAllSchemata() {
    Set<SchemaSource> allSchemas = new HashSet<>();
    if (this.schemas != null) {
      allSchemas.addAll(this.schemas);
    }
    if (this.enrichedSchema != null) {
      allSchemas.add(this.enrichedSchema);
    }
    Set<SchemaSource> excludeable = new HashSet<>(this.excludeSchemata);
    excludeable.removeAll(allSchemas);      //overriding excludes if explicitly provided
    if (augmentWithOwlImports) {
      allSchemas.addAll(RDFUnitUtils.augmentWithOwlImports(allSchemas));
    }
    allSchemas.removeAll(excludeable);
    return allSchemas;
  }

  public TestSource getTestSource() {

    if (testSource != null) {
      // When we use auto discovery of schemata we create a SchemaSource with no references
      // After we identify the schemata we add them in the existing TestSource to avoid re-loading the source
      Collection<SchemaSource> schemata = getAllSchemata();
      if (testSource.getReferencesSchemata().isEmpty() && !schemata.isEmpty()) {
        testSource = TestSourceFactory.createTestSource(testSource, schemata);
      }
      return testSource;
    }

    testSourceBuilder.setPrefixUri(prefix, datasetURI);
    testSourceBuilder.setReferenceSchemata(getAllSchemata());
    if (customDereferenceURI != null && "-".equals(customDereferenceURI)) {
      testSourceBuilder.setInMemFromPipe();
    }
    if (getEndpointURI() == null || getEndpointURI().isEmpty()) {
      String tmpCustomDereferenceURI = datasetURI;
      if (customDereferenceURI != null && !customDereferenceURI.isEmpty()) {
        tmpCustomDereferenceURI = customDereferenceURI;
      }
      if (testSourceBuilder.getInMemReader()
          == null) { // if the reader is not set already e.g. text
        testSourceBuilder
            .setInMemReader(RdfReaderFactory.createDereferenceReader(tmpCustomDereferenceURI));
      }
    }

    // Set TestSource configuration
    if (this.endpointQueryCacheTTL > 0) {
      testSourceBuilder.setCacheTTL(this.endpointQueryCacheTTL);
    }

    if (this.endpointQueryDelayMS > 0) {
      testSourceBuilder.setQueryDelay(this.endpointQueryDelayMS);
    }

    if (this.endpointQueryPagination > 0) {
      testSourceBuilder.setPagination(this.endpointQueryPagination);
    }

    if (this.endpointQueryLimit > 0) {
      testSourceBuilder.setQueryLimit(this.endpointQueryLimit);
    }

    testSource = testSourceBuilder.build();

    return testSource;
  }

  public void setOutputFormatTypes(Collection<String> outputNames)
      throws UndefinedSerializationException {
    outputFormats = new ArrayList<>();
    for (String name : outputNames) {
      SerializationFormat serializationFormat = FormatService.getOutputFormat(name);
      if (serializationFormat != null) {
        outputFormats.add(serializationFormat);
      } else {
        throw new UndefinedSerializationException(name);
      }
    }
    if (outputFormats.isEmpty()) {
      throw new UndefinedSerializationException("");
    }
  }

  public boolean isTestCacheEnabled() {
    return testCacheEnabled;
  }

  public void setTestCacheEnabled(boolean testCacheEnabled) {
    this.testCacheEnabled = testCacheEnabled;
  }

  public boolean isManualTestsEnabled() {
    return manualTestsEnabled;
  }

  public void setManualTestsEnabled(boolean manualTestsEnabled) {
    this.manualTestsEnabled = manualTestsEnabled;
  }

  public boolean isAutoTestsEnabled() {
    return autoTestsEnabled;
  }

  public void setAutoTestsEnabled(boolean autoTestsEnabled) {
    this.autoTestsEnabled = autoTestsEnabled;
    if (!this.autoTestsEnabled) {
      this.setTestCacheEnabled(false);
    }
  }

  public TestCaseExecutionType getTestCaseExecutionType() {
    return testCaseExecutionType;
  }

  public void setTestCaseExecutionType(TestCaseExecutionType testCaseExecutionType) {
    this.testCaseExecutionType = testCaseExecutionType;
  }

  public boolean isCalculateCoverageEnabled() {
    return calculateCoverageEnabled;
  }

  public void setCalculateCoverageEnabled(boolean calculateCoverageEnabled) {
    this.calculateCoverageEnabled = calculateCoverageEnabled;
  }

  public String getDataFolder() {
    return dataFolder;
  }

  public String getTestFolder() {
    return testFolder;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getDatasetURI() {
    return datasetURI;
  }

  public String getEndpointURI() {
    return testSourceBuilder.getSparqlEndpoint();
  }

  public Collection<String> getEndpointGraphs() {
    return testSourceBuilder.getEndpointGraphs();
  }

  public String getCustomDereferenceURI() {
    return customDereferenceURI;
  }

  public void setCustomDereferenceURI(String customDereferenceURI) {
    this.testSourceBuilder.setImMemFromUri(customDereferenceURI);
    this.customDereferenceURI = customDereferenceURI;
  }

  public EnrichedSchemaSource getEnrichedSchema() {
    return enrichedSchema;
  }

  public void setEnrichedSchema(String enrichedSchemaPrefix) {
    if (enrichedSchemaPrefix != null && !enrichedSchemaPrefix.isEmpty()) {
      enrichedSchema = SchemaSourceFactory
          .createEnrichedSchemaSourceFromCache(testFolder, enrichedSchemaPrefix, datasetURI);
    }
  }

  public Collection<SerializationFormat> getOutputFormats() {
    return outputFormats;
  }

  public SerializationFormat geFirstOutputFormat() {
    return RDFUnitUtils.getFirstItemInCollection(outputFormats)
        .orElseThrow(() -> new IllegalStateException("No output format was provided."));
  }

  public long getEndpointQueryDelayMS() {
    return endpointQueryDelayMS;
  }

  public void setEndpointQueryDelayMS(long endpointQueryDelayMS) {
    this.endpointQueryDelayMS = endpointQueryDelayMS;
  }

  public long getEndpointQueryCacheTTL() {
    return endpointQueryCacheTTL;
  }

  public void setEndpointQueryCacheTTL(long endpointQueryCacheTTL) {
    this.endpointQueryCacheTTL = endpointQueryCacheTTL;
  }

  public long getEndpointQueryPagination() {
    return endpointQueryPagination;
  }

  public void setEndpointQueryPagination(long endpointQueryPagination) {
    this.endpointQueryPagination = endpointQueryPagination;
  }

  public long getEndpointQueryLimit() {
    return endpointQueryLimit;
  }

  public void setEndpointQueryLimit(long endpointQueryLimit) {
    this.endpointQueryLimit = endpointQueryLimit;
  }

  public Collection<SchemaSource> getExcludeSchemata() {
    return excludeSchemata;
  }

  public void setExcludeSchemata(Collection<SchemaSource> schemata) {
    this.excludeSchemata = new ArrayList<>();
    this.excludeSchemata.addAll(schemata);
  }
}
