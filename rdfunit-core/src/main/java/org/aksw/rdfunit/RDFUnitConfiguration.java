package org.aksw.rdfunit;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.sources.*;
import org.aksw.rdfunit.statistics.NamespaceStatistics;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.aksw.rdfunit.utils.UriToPathUtils;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>RDFUnitConfiguration class.</p>
 *
 * @author Dimitris Kontokostas
 *         Holds a configuration for a complete test
 *         TODO: Got too big, maybe break it down a bit
 *         TODO: Got really really big!!!
 * @version $Id : $Id
 * @since 11 /15/13 11:50 AM
 */
public class RDFUnitConfiguration {

    /* Main Dataset URI (also used for loading dataset tests) */
    private final String datasetURI;
    private String prefix = null;

    /* folder where we store tests / configuration.  */
    private final String dataFolder;
    private final String testFolder;

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

    private EnrichedSchemaSource enrichedSchema = null;

    /* use the cache for loading tests (do not regenerate if already exists) */
    private boolean testCacheEnabled = true;

    /* if set to false it will generate only schema based test cases */
    private boolean manualTestsEnabled = true;

    /* if set to false it will load only manual test cases */
    private boolean autoTestsEnabled = true;

    /* Execution type */
    private TestCaseExecutionType testCaseExecutionType = TestCaseExecutionType.aggregatedTestCaseResult;

    /* Output format types*/
    private Collection<SerializationFormat> outputFormats = null;

    /*  */
    private boolean calculateCoverageEnabled = false;

    /**
     * <p>Constructor for RDFUnitConfiguration.</p>
     *
     * @param datasetURI a
     * object.
     * @param dataFolder a
     * object.
     */
    public RDFUnitConfiguration(String datasetURI, String dataFolder) {
        this(datasetURI, dataFolder, dataFolder + "tests/");
    }

    /**
     * <p>Constructor for RDFUnitConfiguration.</p>
     *
     * @param datasetURI a
     * object.
     * @param dataFolder a
     * object.
     * @param testFolder a
     * object.
     */
    public RDFUnitConfiguration(String datasetURI, String dataFolder, String testFolder) {
        this.datasetURI = datasetURI;
        this.dataFolder = dataFolder;
        this.testFolder = testFolder;

        prefix = UriToPathUtils.getAutoPrefixForURI(datasetURI); // default prefix
    }

    /**
     * <p>setEndpointConfiguration.</p>
     *
     * @param endpointURI a
     * object.
     * @param endpointGraphs a
     * object.
     */
    public void setEndpointConfiguration(String endpointURI, Collection<String> endpointGraphs) {
        this.testSourceBuilder.setEndpoint(endpointURI, endpointGraphs);
    }

    /**
     * <p>Setter for the field <code>customDereferenceURI</code>.</p>
     *
     * @param customDereferenceURI a
     * object.
     */
    public void setCustomDereferenceURI(String customDereferenceURI) {
        this.testSourceBuilder.setImMemFromUri(customDereferenceURI);
        this.customDereferenceURI = customDereferenceURI;
    }

    /**
     * <p>Setter for the field <code>customTextSource</code>.</p>
     *
     * @param text a
     * object.
     * @param format a
     * object.
     * @throws org.aksw.rdfunit.exceptions.UndefinedSerializationException if any.
     */
    public void setCustomTextSource(String text, String format) throws UndefinedSerializationException {
        testSourceBuilder.setInMemFromCustomText(text, format);
    }

    /**
     * <p>setAutoSchemataFromQEF.</p>
     *
     * @param qef a
     * object.
     */
    public void setAutoSchemataFromQEF(QueryExecutionFactory qef) {
        setAutoSchemataFromQEF(qef, false);
    }

    /**
     * <p>setAutoSchemataFromQEF.</p>
     *
     * @param qef a
     * object.
     * @param all the all
     */
    public void setAutoSchemataFromQEF(QueryExecutionFactory qef, boolean all) {
        setAutoSchemataFromQEF(qef, all, true);
    }

    /**
     * Sets auto schemata from qEF.
     *
     * @param qef the qef
     * @param all the all
     * @param limitToKnown the limit to known
     */
    public void setAutoSchemataFromQEF(QueryExecutionFactory qef, boolean all, boolean limitToKnown) {

        NamespaceStatistics namespaceStatistics;
        if (all) {
            namespaceStatistics = limitToKnown ? NamespaceStatistics.createCompleteNSStatisticsKnown() : NamespaceStatistics.createCompleteNSStatisticsAll();
        } else {
            namespaceStatistics = limitToKnown ? NamespaceStatistics.createOntologyNSStatisticsKnown() : NamespaceStatistics.createOntologyNSStatisticsAll();
        }
        checkNotNull(namespaceStatistics);
        this.schemas = namespaceStatistics.getNamespaces(qef);
    }

    /**
     * <p>setSchemataFromPrefixes.</p>
     *
     * @param schemaPrefixes a
     * object.
     * @throws org.aksw.rdfunit.exceptions.UndefinedSchemaException if any.
     */
    public void setSchemataFromPrefixes(Collection<String> schemaPrefixes) throws UndefinedSchemaException {
        this.schemas = SchemaService.getSourceList(testFolder, schemaPrefixes);
    }

    /**
     * <p>setSchemata.</p>
     *
     * @param schemata a
     * object.
     */
    public void setSchemata(Collection<SchemaSource> schemata) {
        this.schemas = new ArrayList<>();
        this.schemas.addAll(schemata);
    }

    /**
     * <p>Setter for the field <code>enrichedSchema</code>.</p>
     *
     * @param enrichedSchemaPrefix a
     * object.
     */
    public void setEnrichedSchema(String enrichedSchemaPrefix) {
        if (enrichedSchemaPrefix != null && !enrichedSchemaPrefix.isEmpty()) {
            enrichedSchema = SchemaSourceFactory.createEnrichedSchemaSourceFromCache(testFolder, enrichedSchemaPrefix, datasetURI);
        }
    }

    /**
     * <p>getAllSchemata.</p>
     *
     * @return a  object.
     */
    public Collection<SchemaSource> getAllSchemata() {
        Collection<SchemaSource> allSchemas = new ArrayList<>();
        if (this.schemas != null) {
            allSchemas.addAll(this.schemas);
        }
        if (this.enrichedSchema != null) {
            allSchemas.add(this.enrichedSchema);
        }

        return allSchemas;
    }

    /**
     * <p>Getter for the field <code>testSource</code>.</p>
     *
     * @return a  object.
     */
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
            if (testSourceBuilder.getInMemReader() == null) { // if the reader is not set already e.g. text
                testSourceBuilder.setInMemReader(RdfReaderFactory.createDereferenceReader(tmpCustomDereferenceURI));
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

    /**
     * <p>setOutputFormatTypes.</p>
     *
     * @param outputNames a
     * object.
     * @throws org.aksw.rdfunit.exceptions.UndefinedSerializationException if any.
     */
    public void setOutputFormatTypes(Collection<String> outputNames) throws UndefinedSerializationException {
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

    /**
     * <p>isTestCacheEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isTestCacheEnabled() {
        return testCacheEnabled;
    }

    /**
     * <p>Setter for the field <code>testCacheEnabled</code>.</p>
     *
     * @param testCacheEnabled a boolean.
     */
    public void setTestCacheEnabled(boolean testCacheEnabled) {
        this.testCacheEnabled = testCacheEnabled;
    }

    /**
     * <p>isManualTestsEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isManualTestsEnabled() {
        return manualTestsEnabled;
    }

    /**
     * <p>Setter for the field <code>manualTestsEnabled</code>.</p>
     *
     * @param manualTestsEnabled a boolean.
     */
    public void setManualTestsEnabled(boolean manualTestsEnabled) {
        this.manualTestsEnabled = manualTestsEnabled;
    }

    /**
     * <p>isAutoTestsEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isAutoTestsEnabled() {
        return autoTestsEnabled;
    }

    /**
     * <p>Setter for the field <code>autoTestsEnabled</code>.</p>
     *
     * @param autoTestsEnabled a boolean.
     */
    public void setAutoTestsEnabled(boolean autoTestsEnabled) {
        this.autoTestsEnabled = autoTestsEnabled;
        if (!this.autoTestsEnabled) {
            this.setTestCacheEnabled(false);
        }
    }

    /**
     * <p>Getter for the field <code>testCaseExecutionType</code>.</p>
     *
     * @return a  object.
     */
    public TestCaseExecutionType getTestCaseExecutionType() {
        return testCaseExecutionType;
    }

    /**
     * <p>Setter for the field <code>testCaseExecutionType</code>.</p>
     *
     * @param testCaseExecutionType a
     * object.
     */
    public void setTestCaseExecutionType(TestCaseExecutionType testCaseExecutionType) {
        this.testCaseExecutionType = testCaseExecutionType;
    }

    /**
     * <p>isCalculateCoverageEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isCalculateCoverageEnabled() {
        return calculateCoverageEnabled;
    }

    /**
     * <p>Setter for the field <code>calculateCoverageEnabled</code>.</p>
     *
     * @param calculateCoverageEnabled a boolean.
     */
    public void setCalculateCoverageEnabled(boolean calculateCoverageEnabled) {
        this.calculateCoverageEnabled = calculateCoverageEnabled;
    }

    /**
     * <p>Getter for the field <code>dataFolder</code>.</p>
     *
     * @return a  object.
     */
    public String getDataFolder() {
        return dataFolder;
    }

    /**
     * <p>Getter for the field <code>testFolder</code>.</p>
     *
     * @return a  object.
     */
    public String getTestFolder() {
        return testFolder;
    }

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a  object.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Setter for the field <code>prefix</code>.</p>
     *
     * @param prefix a
     * object.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <p>Getter for the field <code>datasetURI</code>.</p>
     *
     * @return a  object.
     */
    public String getDatasetURI() {
        return datasetURI;
    }

    /**
     * <p>Getter for the field <code>endpointURI</code>.</p>
     *
     * @return a  object.
     */
    public String getEndpointURI() {
        return testSourceBuilder.getSparqlEndpoint();
    }

    /**
     * <p>Getter for the field <code>endpointGraphs</code>.</p>
     *
     * @return a  object.
     */
    public Collection<String> getEndpointGraphs() {
        return testSourceBuilder.getEndpointGraphs();
    }

    /**
     * <p>Getter for the field <code>customDereferenceURI</code>.</p>
     *
     * @return a  object.
     */
    public String getCustomDereferenceURI() {
        return customDereferenceURI;
    }

    /**
     * <p>Getter for the field <code>enrichedSchema</code>.</p>
     *
     * @return a  object.
     */
    public EnrichedSchemaSource getEnrichedSchema() {
        return enrichedSchema;
    }

    /**
     * <p>Getter for the field <code>outputFormats</code>.</p>
     *
     * @return a  object.
     */
    public Collection<SerializationFormat> getOutputFormats() {
        return outputFormats;
    }

    /**
     * <p>geFirstOutputFormat.</p>
     *
     * @return a  object.
     */
    public SerializationFormat geFirstOutputFormat() {
        return RDFUnitUtils.getFirstItemInCollection(outputFormats);
    }

    /**
     * <p>Getter for the field <code>endpointQueryDelayMS</code>.</p>
     *
     * @return a long.
     */
    public long getEndpointQueryDelayMS() {
        return endpointQueryDelayMS;
    }

    /**
     * <p>Setter for the field <code>endpointQueryDelayMS</code>.</p>
     *
     * @param endpointQueryDelayMS a long.
     */
    public void setEndpointQueryDelayMS(long endpointQueryDelayMS) {
        this.endpointQueryDelayMS = endpointQueryDelayMS;
    }

    /**
     * <p>Getter for the field <code>endpointQueryCacheTTL</code>.</p>
     *
     * @return a long.
     */
    public long getEndpointQueryCacheTTL() {
        return endpointQueryCacheTTL;
    }

    /**
     * <p>Setter for the field <code>endpointQueryCacheTTL</code>.</p>
     *
     * @param endpointQueryCacheTTL a long.
     */
    public void setEndpointQueryCacheTTL(long endpointQueryCacheTTL) {
        this.endpointQueryCacheTTL = endpointQueryCacheTTL;
    }

    /**
     * <p>Getter for the field <code>endpointQueryPagination</code>.</p>
     *
     * @return a long.
     */
    public long getEndpointQueryPagination() {
        return endpointQueryPagination;
    }

    /**
     * <p>Setter for the field <code>endpointQueryPagination</code>.</p>
     *
     * @param endpointQueryPagination a long.
     */
    public void setEndpointQueryPagination(long endpointQueryPagination) {
        this.endpointQueryPagination = endpointQueryPagination;
    }

    /**
     * <p>Getter for the field <code>endpointQueryLimit</code>.</p>
     *
     * @return a long.
     */
    public long getEndpointQueryLimit() {
        return endpointQueryLimit;
    }

    /**
     * <p>Setter for the field <code>endpointQueryLimit</code>.</p>
     *
     * @param endpointQueryLimit a long.
     */
    public void setEndpointQueryLimit(long endpointQueryLimit) {
        this.endpointQueryLimit = endpointQueryLimit;
    }
}
