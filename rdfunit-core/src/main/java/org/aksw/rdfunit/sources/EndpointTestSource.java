package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.rdfunit.enums.TestAppliesTo;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Describes an arbitary datatest source
 * TODO make this abstract and create an EndpointSource and a DumpSource
 *
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:54 PM
 * @version $Id: $Id
 */
public class EndpointTestSource extends TestSource {

    private final String sparqlEndpoint;
    private final Collection<String> sparqlGraph;

    /**
     * <p>Constructor for EndpointTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     */
    public EndpointTestSource(String prefix, String uri) {
        this(prefix, uri, uri, new ArrayList<String>(), null);
    }

    /**
     * <p>Constructor for EndpointTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param sparqlEndpoint a {@link java.lang.String} object.
     * @param sparqlGraph a {@link java.util.Collection} object.
     * @param schemata a {@link java.util.Collection} object.
     */
    public EndpointTestSource(String prefix, String uri, String sparqlEndpoint, Collection<String> sparqlGraph, Collection<SchemaSource> schemata) {
        super(prefix, uri);
        this.sparqlEndpoint = sparqlEndpoint;
        this.sparqlGraph = new ArrayList<>(sparqlGraph);
        if (schemata != null) {
            addReferencesSchemata(schemata);
        }
    }

    /**
     * <p>Constructor for EndpointTestSource.</p>
     *
     * @param source a {@link org.aksw.rdfunit.sources.EndpointTestSource} object.
     */
    public EndpointTestSource(EndpointTestSource source) {
        this(source.getPrefix(), source.getUri(), source.getSparqlEndpoint(), source.getSparqlGraphs(), source.getReferencesSchemata());
    }

    /**
     * Instantiates a new Test source along with a collection os schemata.
     *
     * @param source the source
     * @param referencesSchemata the references schemata
     */
    public EndpointTestSource(EndpointTestSource source, Collection<SchemaSource> referencesSchemata ) {
        super(source);
        this.addReferencesSchemata(referencesSchemata);

        this.sparqlEndpoint = source.sparqlEndpoint;
        this.sparqlGraph = new ArrayList<>(source.getSparqlGraphs());

    }

    /** {@inheritDoc} */
    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Dataset;
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

        return masqueradeQEF(qef);
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
