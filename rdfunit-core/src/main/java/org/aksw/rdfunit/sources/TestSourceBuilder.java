package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.format.SerializationFormatGraphType;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.io.reader.RDFStreamReader;

import java.io.BufferedInputStream;
import java.util.Arrays;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TestSource builder
 *
 * @author Dimitris Kontokostas
 * @since 8/19/15 11:58 PM
 */
public class TestSourceBuilder {

    private enum TestSourceType {Endpoint, InMemSingle, InMemDataset}
    private TestSourceType testSourceType = TestSourceType.InMemSingle;

    private SourceConfig sourceConfig = null;
    private Collection<SchemaSource> referenceSchemata = null;
    private QueryingConfig queryingConfig = null;

    private RDFReader inMemReader = null;
    private String sparqlEndpoint = null;
    private Collection<String> endpointGraphs = null;

    public TestSourceBuilder setPrefixUri(String prefix, String uri) {
        this.sourceConfig = new SourceConfig(prefix, uri);
        return this;
    }

    public TestSourceBuilder setEndpoint(String sparqlEndpoint, Collection<String> endpointGraphs) {
        testSourceType = TestSourceType.Endpoint;
        this.sparqlEndpoint = sparqlEndpoint;
        this.endpointGraphs = endpointGraphs;
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createEndpoint();
        }
        return  this;
    }

    public TestSourceBuilder setImMemSingle() {
        testSourceType = TestSourceType.InMemSingle;
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createInMemory();
        }
        return this;
    }

    public TestSourceBuilder setImMemDataset() {
        testSourceType = TestSourceType.InMemDataset;
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createInMemory();
        }
        return this;
    }

    public TestSourceBuilder setImMemFromUri(String uri) {
        SerializationFormat format = FormatService.getInputFormat(FormatService.getFormatFromExtension(uri));
        if (format != null && format.getGraphType().equals(SerializationFormatGraphType.dataset)) {
            setImMemDataset();
        } else {
            setImMemSingle();
        }
        this.inMemReader = RDFReaderFactory.createDereferenceReader(uri);
        return this;
    }

    public TestSourceBuilder setInMemReader(RDFReader reader) {
        this.inMemReader = reader;
        if (queryingConfig == null) {
            queryingConfig = QueryingConfig.createInMemory();
        }
        return this;
    }

    public TestSourceBuilder setInMemFromPipe() {
        this.inMemReader = new RDFStreamReader(new BufferedInputStream(System.in), "TURTLE");
        setImMemSingle();
        return this;
    }

    public TestSourceBuilder setInMemFromCustomText(String customTextSource, String customTextFormat) throws UndefinedSerializationException {

        SerializationFormat format = FormatService.getInputFormat(customTextFormat);
        if (format == null) {
            throw new UndefinedSerializationException(customTextFormat);
        }

        this.inMemReader = RDFReaderFactory.createReaderFromText(customTextSource, format.getName());

        return this;
    }

    public TestSourceBuilder setReferenceSchemata(Collection<SchemaSource> referenceSchemata) {
        this.referenceSchemata = referenceSchemata;
        return this;
    }

    public TestSourceBuilder setReferenceSchemata(SchemaSource referenceSchema) {
        this.referenceSchemata = Arrays.asList(referenceSchema);
        return this;
    }

    public TestSourceBuilder setCacheTTL(long cacheTTL) {
        queryingConfig = queryingConfig.copyWithNewCacheTTL(cacheTTL);
        return this;
    }

    public TestSourceBuilder setQueryLimit(long queryLimit) {
        queryingConfig = queryingConfig.copyWithNewQueryLimit(queryLimit);
        return this;
    }

    public TestSourceBuilder setQueryDelay(long queryDelay) {
        queryingConfig = queryingConfig.copyWithNewQueryDelay(queryDelay);
        return this;
    }

    public TestSourceBuilder setPagination(long pagination) {
        queryingConfig = queryingConfig.copyWithNewPagination(pagination);
        return this;
    }


    public TestSource build() {
        checkNotNull(sourceConfig);
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

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public Collection<String> getEndpointGraphs() {
        return endpointGraphs;
    }
}
