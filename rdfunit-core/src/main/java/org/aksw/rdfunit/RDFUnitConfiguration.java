package org.aksw.rdfunit;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.io.reader.RDFStreamReader;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.*;
import org.aksw.rdfunit.statistics.DatasetStatistics;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>RDFUnitConfiguration class.</p>
 *
 * @author Dimitris Kontokostas
 *         Holds a configuration for a complete test
 *         TODO: Got too big, maybe break it down a bit
 *         TODO: Got really really big!!!
 * @since 11/15/13 11:50 AM
 * @version $Id: $Id
 */
public class RDFUnitConfiguration {

    /* Main Dataset URI (also used for loading dataset tests) */
    private final String datasetURI;
    private String prefix = null;

    /* folder where we store tests / configuration.  */
    private final String dataFolder;
    private final String testFolder;

    /* SPARQL endpoint configuration */
    private String endpointURI = null;
    private Collection<String> endpointGraphs = null;
    private long endpointQueryDelayMS = -1;
    private long endpointQueryCacheTTL = -1;
    private long endpointQueryPagination = -1;
    private long endpointQueryLimit = -1;

    /* Dereference testing (if different from datasetURI) */
    private String customDereferenceURI = null;

    /* used to cache the test source when we initially do stats for auto loading test cases */
    private TestSource testSource = null;

    /* Use text directly as a source */
    private String customTextSource = null;
    private SerializationFormat customTextFormat = null;

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
     * @param datasetURI a {@link java.lang.String} object.
     * @param dataFolder a {@link java.lang.String} object.
     */
    public RDFUnitConfiguration(String datasetURI, String dataFolder) {
        this(datasetURI, dataFolder, dataFolder + "tests/");
    }

    /**
     * <p>Constructor for RDFUnitConfiguration.</p>
     *
     * @param datasetURI a {@link java.lang.String} object.
     * @param dataFolder a {@link java.lang.String} object.
     * @param testFolder a {@link java.lang.String} object.
     */
    public RDFUnitConfiguration(String datasetURI, String dataFolder, String testFolder) {
        this.datasetURI = datasetURI;
        this.dataFolder = dataFolder;
        this.testFolder = testFolder;

        prefix = CacheUtils.getAutoPrefixForURI(datasetURI); // default prefix
    }

    /**
     * <p>setEndpointConfiguration.</p>
     *
     * @param endpointURI a {@link java.lang.String} object.
     * @param endpointGraphs a {@link java.util.Collection} object.
     */
    public void setEndpointConfiguration(String endpointURI, Collection<String> endpointGraphs) {
        this.endpointURI = endpointURI;
        this.endpointGraphs = new ArrayList<>();
        this.endpointGraphs.addAll(endpointGraphs);
    }

    /**
     * <p>Setter for the field <code>customDereferenceURI</code>.</p>
     *
     * @param customDereferenceURI a {@link java.lang.String} object.
     */
    public void setCustomDereferenceURI(String customDereferenceURI) {
        this.customDereferenceURI = customDereferenceURI;
    }

    /**
     * <p>Setter for the field <code>customTextSource</code>.</p>
     *
     * @param text a {@link java.lang.String} object.
     * @param format a {@link java.lang.String} object.
     * @throws org.aksw.rdfunit.exceptions.UndefinedSerializationException if any.
     */
    public void setCustomTextSource(String text, String format) throws UndefinedSerializationException {
        this.customTextSource = text;
        this.customTextFormat = FormatService.getInputFormat(format);
        if (this.customTextFormat == null) {
            throw new UndefinedSerializationException(format);
        }

        //Clear endpoint / custom dereference
        this.endpointURI = null;
        this.customDereferenceURI = null;
    }

    /**
     * <p>setAutoSchemataFromQEF.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     */
    public void setAutoSchemataFromQEF(QueryExecutionFactory qef) {
        setAutoSchemataFromQEF(qef,true);
    }

    /**
     * <p>setAutoSchemataFromQEF.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     */
    public void setAutoSchemataFromQEF(QueryExecutionFactory qef, boolean all) {
        DatasetStatistics datasetStatistics = new DatasetStatistics(qef, false);
        if (all) {
            this.schemas = datasetStatistics.getIdentifiedSchemataAll();
        } else {
            this.schemas = datasetStatistics.getIdentifiedSchemataOntology();
        }
    }

    /**
     * <p>setSchemataFromPrefixes.</p>
     *
     * @param schemaPrefixes a {@link java.util.Collection} object.
     * @throws org.aksw.rdfunit.exceptions.UndefinedSchemaException if any.
     */
    public void setSchemataFromPrefixes(Collection<String> schemaPrefixes) throws UndefinedSchemaException {
        this.schemas = SchemaService.getSourceList(testFolder, schemaPrefixes);
    }

    /**
     * <p>setSchemata.</p>
     *
     * @param schemata a {@link java.util.Collection} object.
     */
    public void setSchemata(Collection<SchemaSource> schemata) {
        this.schemas = new ArrayList<>();
        this.schemas.addAll(schemata);
    }

    /**
     * <p>Setter for the field <code>enrichedSchema</code>.</p>
     *
     * @param enrichedSchemaPrefix a {@link java.lang.String} object.
     */
    public void setEnrichedSchema(String enrichedSchemaPrefix) {
        if (enrichedSchemaPrefix != null && !enrichedSchemaPrefix.isEmpty()) {
            enrichedSchema = SourceFactory.createEnrichedSchemaSourceFromCache(testFolder, enrichedSchemaPrefix, datasetURI);
        }
    }

    /**
     * <p>getAllSchemata.</p>
     *
     * @return a {@link java.util.Collection} object.
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
     * @return a {@link org.aksw.rdfunit.sources.Source} object.
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

        String prefix = CacheUtils.getAutoPrefixForURI(datasetURI);

        if (endpointURI != null && !endpointURI.isEmpty()) {
            // return a SPARQL Endpoint source
            testSource = new EndpointTestSource(
                    prefix,
                    datasetURI,
                    endpointURI,
                    endpointGraphs,
                    getAllSchemata());
        } else {

            RDFReader dumpReader;

            // Return a text source
            if (customTextSource != null) { // read from custom text
                dumpReader = RDFReaderFactory.createReaderFromText(customTextSource, customTextFormat.getName());
            }
            else {
                // return a DumpSource
                String tmp_customDereferenceURI = datasetURI;

                if (customDereferenceURI != null && !customDereferenceURI.isEmpty()) {
                    tmp_customDereferenceURI = customDereferenceURI;
                }

                if (customDereferenceURI != null && "-".equals(customDereferenceURI)) {
                    // Read from standard input / pipe
                    dumpReader = new RDFStreamReader(new BufferedInputStream(System.in), "TURTLE");  // TODO make format configurable
                } else {
                    // dereference the source (check local file or remote)
                    dumpReader = RDFReaderFactory.createDereferenceReader(tmp_customDereferenceURI);
                }
            }

            // Set the DumpSource with the configured reader
            testSource = new DumpTestSource(
                    prefix,
                    datasetURI,
                    dumpReader,
                    getAllSchemata());
        }

        // Set TestSource configuration
        if (this.endpointQueryCacheTTL != -1)
            testSource.setCacheTTL(this.endpointQueryCacheTTL);

        if (this.endpointQueryDelayMS != -1)
            testSource.setQueryDelay(this.endpointQueryDelayMS);

        if (this.endpointQueryPagination != -1)
            testSource.setPagination(this.endpointQueryPagination);

        if (this.endpointQueryLimit != -1)
            testSource.setQueryLimit(this.endpointQueryLimit);

        return testSource;
    }

    /**
     * <p>setOutputFormatTypes.</p>
     *
     * @param outputNames a {@link java.util.Collection} object.
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
     * @return a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
     */
    public TestCaseExecutionType getTestCaseExecutionType() {
        return testCaseExecutionType;
    }

    /**
     * <p>Setter for the field <code>testCaseExecutionType</code>.</p>
     *
     * @param testCaseExecutionType a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
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
     * @return a {@link java.lang.String} object.
     */
    public String getDataFolder() {
        return dataFolder;
    }

    /**
     * <p>Getter for the field <code>testFolder</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTestFolder() {
        return testFolder;
    }

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Setter for the field <code>prefix</code>.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * <p>Getter for the field <code>datasetURI</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDatasetURI() {
        return datasetURI;
    }

    /**
     * <p>Getter for the field <code>endpointURI</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEndpointURI() {
        return endpointURI;
    }

    /**
     * <p>Getter for the field <code>endpointGraphs</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<String> getEndpointGraphs() {
        return endpointGraphs;
    }

    /**
     * <p>Getter for the field <code>customDereferenceURI</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCustomDereferenceURI() {
        return customDereferenceURI;
    }

    /**
     * <p>Getter for the field <code>enrichedSchema</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.EnrichedSchemaSource} object.
     */
    public EnrichedSchemaSource getEnrichedSchema() {
        return enrichedSchema;
    }

    /**
     * <p>Getter for the field <code>outputFormats</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<SerializationFormat> getOutputFormats() {
        return outputFormats;
    }

    /**
     * <p>geFirstOutputFormat.</p>
     *
     * @return a {@link org.aksw.rdfunit.io.format.SerializationFormat} object.
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
