package org.aksw.rdfunit.sources;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.sparql.core.DatasetDescription;

/**
 * Describes a SPARQL Endpoint source
 *
 * @author Dimitris Kontokostas
 */
public class EndpointTestSource extends AbstractTestSource implements TestSource {

  private final String sparqlEndpoint;
  private final Collection<String> sparqlGraph;
  private final String username;
  private final String password;

  EndpointTestSource(SourceConfig sourceConfig, QueryingConfig queryingConfig,
      Collection<SchemaSource> referenceSchemata, String sparqlEndpoint,
      Collection<String> sparqlGraph) {
    this(sourceConfig, queryingConfig, referenceSchemata, sparqlEndpoint, sparqlGraph, "", "");
  }

  EndpointTestSource(SourceConfig sourceConfig, QueryingConfig queryingConfig,
      Collection<SchemaSource> referenceSchemata, String sparqlEndpoint,
      Collection<String> sparqlGraph, String username, String password) {
    super(sourceConfig, queryingConfig, referenceSchemata);
    this.sparqlEndpoint = checkNotNull(sparqlEndpoint);
    this.sparqlGraph = Collections.unmodifiableCollection(checkNotNull(sparqlGraph));
    this.username = checkNotNull(username);
    this.password = checkNotNull(password);
  }

  EndpointTestSource(EndpointTestSource endpointTestSource,
      Collection<SchemaSource> referenceSchemata) {
    this(endpointTestSource.sourceConfig, endpointTestSource.queryingConfig, referenceSchemata,
        endpointTestSource.sparqlEndpoint, endpointTestSource.sparqlGraph,
        endpointTestSource.username, endpointTestSource.password);
  }


  @Override
  protected QueryExecutionFactory initQueryFactory() {

    QueryExecutionFactory qef;
    // if empty
    if (username.isEmpty() && password.isEmpty()) {
      qef = new QueryExecutionFactoryHttp(getSparqlEndpoint(), getSparqlGraphs());
    } else {
      DatasetDescription dd = new DatasetDescription(new ArrayList<>(getSparqlGraphs()),
          Collections.emptyList());
      CredentialsProvider provider = new BasicCredentialsProvider();
      UsernamePasswordCredentials credentials
          = new UsernamePasswordCredentials(username, password);
      provider.setCredentials(AuthScope.ANY, credentials);

      HttpClient client = HttpClientBuilder.create()
          .setDefaultCredentialsProvider(provider)
          .build();
      qef = new QueryExecutionFactoryHttp(getSparqlEndpoint(), dd, client);
    }

    return masqueradeQEF(qef, this);
  }

  public String getSparqlEndpoint() {
    return sparqlEndpoint;
  }

  public Collection<String> getSparqlGraphs() {
    return sparqlGraph;
  }


}
