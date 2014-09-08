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
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.*;
import org.aksw.rdfunit.statistics.DatasetStatistics;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Holds a configuration for a complete test
 *         TODO: Got too big, maybe break it down a bit
 *         TODO: Got really really big!!!
 * @since 11/15/13 11:50 AM
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
    private long endpointQueryDelayMS = EndpointTestSource.QUERY_DELAY;
    private long endpointQueryCacheTTL = EndpointTestSource.CACHE_TTL;
    private long endpointQueryPagination = EndpointTestSource.PAGINATION;
    private long endpointQueryLimit = EndpointTestSource.QUERY_LIMIT;

    /* Dereference testing (if different from datasetURI) */
    private String customDereferenceURI = null;

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

    public RDFUnitConfiguration(String datasetURI, String dataFolder) {
        this(datasetURI, dataFolder, dataFolder + "tests/");
    }

    public RDFUnitConfiguration(String datasetURI, String dataFolder, String testFolder) {
        this.datasetURI = datasetURI;
        this.dataFolder = dataFolder;
        this.testFolder = testFolder;

        prefix = CacheUtils.getAutoPrefixForURI(datasetURI); // default prefix
    }

    public void setEndpointConfiguration(String endpointURI, Collection<String> endpointGraphs) {
        setEndpointConfiguration(endpointURI, endpointGraphs, this.endpointQueryDelayMS, this.endpointQueryCacheTTL, this.endpointQueryPagination);
    }

    public void setEndpointConfiguration(String endpointURI, Collection<String> endpointGraphs, long endpointDelayinMS, long endpointCacheTTL, long endpointPagination) {
        setEndpointConfiguration(endpointURI, endpointGraphs, endpointDelayinMS, endpointCacheTTL, endpointPagination, this.endpointQueryLimit);
    }

    public void setEndpointConfiguration(String endpointURI, Collection<String> endpointGraphs, long endpointQueryDelayMS, long endpointQueryCacheTTL, long endpointQueryPagination, long endpointQueryLimit) {
        this.endpointURI = endpointURI;
        this.endpointGraphs = new ArrayList<>();
        this.endpointGraphs.addAll(endpointGraphs);
        this.endpointQueryDelayMS = endpointQueryDelayMS;
        this.endpointQueryCacheTTL = endpointQueryCacheTTL;
        this.endpointQueryPagination = endpointQueryPagination;
        this.endpointQueryLimit = endpointQueryLimit;
    }

    public void setCustomDereferenceURI(String customDereferenceURI) {
        this.customDereferenceURI = customDereferenceURI;
    }

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

    public void setAutoSchemataFromQEF(QueryExecutionFactory qef) {
        DatasetStatistics datasetStatistics = new DatasetStatistics(qef, false);
        this.schemas = datasetStatistics.getIdentifiedSchemata();
    }

    public void setSchemataFromPrefixes(Collection<String> schemaPrefixes) throws UndefinedSchemaException {
        this.schemas = SchemaService.getSourceList(testFolder, schemaPrefixes);
    }

    public void setSchemata(Collection<SchemaSource> schemata) {
        this.schemas = new ArrayList<>();
        this.schemas.addAll(schemata);
    }

    public void setEnrichedSchema(String enrichedSchemaPrefix) {
        if (enrichedSchemaPrefix != null && !enrichedSchemaPrefix.isEmpty()) {
            enrichedSchema = SourceFactory.createEnrichedSchemaSourceFromCache(testFolder, enrichedSchemaPrefix, datasetURI);
        }
    }

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

    public Source getTestSource() {

        if (endpointURI != null && !endpointURI.isEmpty()) {
            // return a SPARQL Endpoint source
            EndpointTestSource endpointSource = new EndpointTestSource(
                    CacheUtils.getAutoPrefixForURI(datasetURI),
                    datasetURI,
                    endpointURI,
                    endpointGraphs,
                    getAllSchemata());

            endpointSource.setQueryDelay(this.endpointQueryDelayMS);
            endpointSource.setCacheTTL(this.endpointQueryCacheTTL);
            endpointSource.setPagination(this.endpointQueryPagination);
            endpointSource.setQueryLimit(this.endpointQueryLimit);

            return endpointSource;
        }

        // Return a text source
        if (customTextSource != null) {
            RDFReader textReader = RDFReaderFactory.createReaderFromText(customTextSource, customTextFormat.getName());
            return new DumpTestSource(
                    CacheUtils.getAutoPrefixForURI(datasetURI),
                    datasetURI,
                    textReader,
                    getAllSchemata());
        }

        // return a DumpSource
        String tmp_customDereferenceURI = datasetURI;

        if (customDereferenceURI != null && !customDereferenceURI.isEmpty()) {
            tmp_customDereferenceURI = customDereferenceURI;
        }
        return new DumpTestSource(
                CacheUtils.getAutoPrefixForURI(datasetURI),
                datasetURI,
                tmp_customDereferenceURI,
                getAllSchemata());
    }

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
        return endpointURI;
    }

    public Collection<String> getEndpointGraphs() {
        return endpointGraphs;
    }

    public String getCustomDereferenceURI() {
        return customDereferenceURI;
    }

    public EnrichedSchemaSource getEnrichedSchema() {
        return enrichedSchema;
    }

    public Collection<SerializationFormat> getOutputFormats() {
        return outputFormats;
    }

    public SerializationFormat geFirstOutputFormat() {
        return RDFUnitUtils.getFirstItemInCollection(outputFormats);
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
}
