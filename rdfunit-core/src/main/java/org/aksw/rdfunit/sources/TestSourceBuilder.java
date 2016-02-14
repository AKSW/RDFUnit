package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.format.SerializationFormatGraphType;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.io.reader.RdfStreamReader;

import java.io.BufferedInputStream;
import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TestSource builder
 *
 * @author Dimitris Kontokostas
 * @since 8/19/15 11:58 PM
 * @version $Id: $Id
 */
public class TestSourceBuilder {

    private enum TestSourceType {Endpoint, InMemSingle, InMemDataset}
    private TestSourceType testSourceType = TestSourceType.InMemSingle;

    private SourceConfig sourceConfig = null;
    private Collection<SchemaSource> referenceSchemata = null;
    private QueryingConfig queryingConfig = null;

    private RdfReader inMemReader = null;
    private String sparqlEndpoint = null;
    private Collection<String> endpointGraphs = null;

    /**
     * <p>setPrefixUri.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setPrefixUri(String prefix, String uri) {
        this.sourceConfig = new SourceConfig(prefix, uri);
        return this;
    }

    /**
     * <p>setEndpoint.</p>
     *
     * @param sparqlEndpoint a {@link java.lang.String} object.
     * @param endpointGraphs a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setEndpoint(String sparqlEndpoint, Collection<String> endpointGraphs) {
        testSourceType = TestSourceType.Endpoint;
        this.sparqlEndpoint = sparqlEndpoint;
        this.endpointGraphs = endpointGraphs;
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createEndpoint();
        }
        return  this;
    }

    /**
     * <p>setImMemSingle.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setImMemSingle() {
        testSourceType = TestSourceType.InMemSingle;
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createInMemory();
        }
        return this;
    }

    /**
     * <p>setImMemDataset.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setImMemDataset() {
        testSourceType = TestSourceType.InMemDataset;
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createInMemory();
        }
        return this;
    }

    /**
     * <p>setImMemFromUri.</p>
     *
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setImMemFromUri(String uri) {
        SerializationFormat format = FormatService.getInputFormat(FormatService.getFormatFromExtension(uri));
        if (format != null && format.getGraphType().equals(SerializationFormatGraphType.dataset)) {
            setImMemDataset();
        } else {
            setImMemSingle();
        }
        this.inMemReader = RdfReaderFactory.createDereferenceReader(uri);
        return this;
    }

    /**
     * <p>Setter for the field <code>inMemReader</code>.</p>
     *
     * @param reader a {@link RdfReader} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setInMemReader(RdfReader reader) {
        this.inMemReader = reader;
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createInMemory();
        }
        return this;
    }

    /**
     * <p>setInMemFromPipe.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setInMemFromPipe() {
        this.inMemReader = new RdfStreamReader(new BufferedInputStream(System.in), "TURTLE");
        setImMemSingle();
        return this;
    }

    /**
     * <p>setInMemFromCustomText.</p>
     *
     * @param customTextSource a {@link java.lang.String} object.
     * @param customTextFormat a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     * @throws org.aksw.rdfunit.exceptions.UndefinedSerializationException if any.
     */
    public TestSourceBuilder setInMemFromCustomText(String customTextSource, String customTextFormat) throws UndefinedSerializationException {

        SerializationFormat format = FormatService.getInputFormat(customTextFormat);
        if (format == null) {
            throw new UndefinedSerializationException(customTextFormat);
        }

        this.inMemReader = RdfReaderFactory.createReaderFromText(customTextSource, format.getName());
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createInMemory();
        }

        return this;
    }

    /**
     * <p>Setter for the field <code>referenceSchemata</code>.</p>
     *
     * @param referenceSchemata a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setReferenceSchemata(Collection<SchemaSource> referenceSchemata) {
        this.referenceSchemata = referenceSchemata;
        return this;
    }

    /**
     * <p>Setter for the field <code>referenceSchemata</code>.</p>
     *
     * @param referenceSchema a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setReferenceSchemata(SchemaSource referenceSchema) {
        this.referenceSchemata = Collections.singletonList(referenceSchema);
        return this;
    }

    /**
     * <p>setCacheTTL.</p>
     *
     * @param cacheTTL a long.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setCacheTTL(long cacheTTL) {
        queryingConfig = queryingConfig.copyWithNewCacheTTL(cacheTTL);
        return this;
    }

    /**
     * <p>setQueryLimit.</p>
     *
     * @param queryLimit a long.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setQueryLimit(long queryLimit) {
        queryingConfig = queryingConfig.copyWithNewQueryLimit(queryLimit);
        return this;
    }

    /**
     * <p>setQueryDelay.</p>
     *
     * @param queryDelay a long.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setQueryDelay(long queryDelay) {
        queryingConfig = queryingConfig.copyWithNewQueryDelay(queryDelay);
        return this;
    }

    /**
     * <p>setPagination.</p>
     *
     * @param pagination a long.
     * @return a {@link org.aksw.rdfunit.sources.TestSourceBuilder} object.
     */
    public TestSourceBuilder setPagination(long pagination) {
        queryingConfig = queryingConfig.copyWithNewPagination(pagination);
        return this;
    }


    /**
     * <p>build.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.TestSource} object.
     */
    public TestSource build() {
        checkNotNull(sourceConfig , "Source configuration not set in TestSourceBuilder");
        checkNotNull(referenceSchemata, "Referenced schemata not set in TestSourceBuilder");

        if (testSourceType.equals(TestSourceType.Endpoint)) {
            if (queryingConfig == null) {
                queryingConfig = QueryingConfig.createEndpoint();
            }
            checkNotNull(sparqlEndpoint);
            checkNotNull(endpointGraphs);
            return new EndpointTestSource (sourceConfig, queryingConfig, referenceSchemata, sparqlEndpoint, endpointGraphs);
        }

        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createEndpoint();
        }

        checkNotNull(inMemReader);

        if (testSourceType.equals(TestSourceType.InMemSingle)) {
            return new DumpTestSource(sourceConfig, queryingConfig, referenceSchemata, inMemReader);
        }
        if (testSourceType.equals(TestSourceType.InMemDataset)) {
            return new DatasetTestSource(sourceConfig, queryingConfig, referenceSchemata, inMemReader);
        }

        throw new IllegalStateException("Should not be here");
    }

    /**
     * <p>Getter for the field <code>sparqlEndpoint</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    /**
     * <p>Getter for the field <code>endpointGraphs</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<String> getEndpointGraphs() {
        return endpointGraphs;
    }

    public RdfReader getInMemReader() {
        return this.inMemReader;
    }
}
