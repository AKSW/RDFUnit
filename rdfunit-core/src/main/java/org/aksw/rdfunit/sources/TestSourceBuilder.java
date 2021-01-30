package org.aksw.rdfunit.sources;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.util.Collection;
import java.util.Collections;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.format.SerializationFormatGraphType;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.io.reader.RdfStreamReader;

/**
 * TestSource builder
 *
 * @author Dimitris Kontokostas
 * @since 8/19/15 11:58 PM
 */
public class TestSourceBuilder {

  private TestSourceType testSourceType = TestSourceType.InMemSingle;
  private SourceConfig sourceConfig = null;
  private Collection<SchemaSource> referenceSchemata = null;
  private QueryingConfig queryingConfig = null;
  private RdfReader inMemReader = null;
  private String sparqlEndpoint = null;
  private Collection<String> endpointGraphs = null;
  private String endpointUsername = null;
  private String endpointPassword = null;

  public TestSourceBuilder setPrefixUri(String prefix, String uri) {
    this.sourceConfig = new SourceConfig(prefix, uri);
    return this;
  }

  public TestSourceBuilder setEndpoint(String sparqlEndpoint, Collection<String> endpointGraphs) {
    return setEndpoint(sparqlEndpoint, endpointGraphs, "", "");
  }

  public TestSourceBuilder setEndpoint(String sparqlEndpoint, Collection<String> endpointGraphs,
      String username, String password) {
    testSourceType = TestSourceType.Endpoint;
    this.sparqlEndpoint = sparqlEndpoint.trim();
    this.endpointGraphs = endpointGraphs;
    this.endpointUsername = username.trim();
    this.endpointPassword = password.trim();
    if (queryingConfig == null) {
      queryingConfig = QueryingConfig.createEndpoint();
    }
    return this;
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
    SerializationFormat format = FormatService
        .getInputFormat(FormatService.getFormatFromExtension(uri));
    if (format != null && format.getGraphType().equals(SerializationFormatGraphType.dataset)) {
      setImMemDataset();
    } else {
      setImMemSingle();
    }
    this.inMemReader = RdfReaderFactory.createDereferenceReader(uri);
    return this;
  }

  public TestSourceBuilder setInMemFromPipe() {
    this.inMemReader = new RdfStreamReader(new BufferedInputStream(System.in), "TURTLE");
    setImMemSingle();
    return this;
  }

  public TestSourceBuilder setInMemFromCustomText(String customTextSource, String customTextFormat)
      throws UndefinedSerializationException {

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

  public TestSourceBuilder setReferenceSchemata(Collection<SchemaSource> referenceSchemata) {
    this.referenceSchemata = referenceSchemata;
    return this;
  }

  public TestSourceBuilder setReferenceSchemata(SchemaSource referenceSchema) {
    this.referenceSchemata = Collections.singletonList(referenceSchema);
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
    checkNotNull(sourceConfig, "Source configuration not set in TestSourceBuilder");
    checkNotNull(referenceSchemata, "Referenced schemata not set in TestSourceBuilder");

    if (testSourceType.equals(TestSourceType.Endpoint)) {
      if (queryingConfig == null) {
        queryingConfig = QueryingConfig.createEndpoint();
      }
      checkNotNull(sparqlEndpoint);
      checkNotNull(endpointGraphs);
      return new EndpointTestSource(sourceConfig, queryingConfig, referenceSchemata, sparqlEndpoint,
          endpointGraphs, endpointUsername, endpointPassword);
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

  public RdfReader getInMemReader() {
    return this.inMemReader;
  }

  public TestSourceBuilder setInMemReader(RdfReader reader) {
    this.inMemReader = reader;
    if (queryingConfig == null) {
      queryingConfig = QueryingConfig.createInMemory();
    }
    return this;
  }

  private enum TestSourceType {Endpoint, InMemSingle, InMemDataset}
}
