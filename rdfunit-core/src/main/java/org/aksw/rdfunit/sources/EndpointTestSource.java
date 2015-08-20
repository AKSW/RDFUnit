package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a SPARQL Endpoint source
 *
 * @author Dimitris Kontokostas
 * @version $Id: $Id
 */
public class EndpointTestSource extends AbstractTestSource implements TestSource {

    private final String sparqlEndpoint;
    private final Collection<String> sparqlGraph;

    EndpointTestSource(SourceConfig sourceConfig, QueryingConfig queryingConfig, Collection<SchemaSource> referenceSchemata, String sparqlEndpoint, Collection<String> sparqlGraph) {
        super(sourceConfig, queryingConfig, referenceSchemata);
        this.sparqlEndpoint = checkNotNull(sparqlEndpoint);
        this.sparqlGraph = Collections.unmodifiableCollection(checkNotNull(sparqlGraph));
    }

    EndpointTestSource(EndpointTestSource endpointTestSource, Collection<SchemaSource> referenceSchemata) {
        this(endpointTestSource.sourceConfig, endpointTestSource.queryingConfig, referenceSchemata, endpointTestSource.sparqlEndpoint, endpointTestSource.sparqlGraph);
    }

    /** {@inheritDoc} */
    @Override
    protected QueryExecutionFactory initQueryFactory() {

        QueryExecutionFactory qef;
        // if empty
        if (getSparqlGraphs() == null || getSparqlGraphs().isEmpty()) {
            qef = new QueryExecutionFactoryHttp(getSparqlEndpoint());
        } else {
            qef = new QueryExecutionFactoryHttp(getSparqlEndpoint(), getSparqlGraphs());
        }

        return masqueradeQEF(qef, this);
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
     * <p>getSparqlGraphs.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<String> getSparqlGraphs() {
        return sparqlGraph;
    }


}
