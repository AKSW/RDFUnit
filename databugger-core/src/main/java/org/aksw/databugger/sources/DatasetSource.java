package org.aksw.databugger.sources;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:54 PM
 */
public class DatasetSource extends Source {

    public final String sparqlEndpoint;

    public DatasetSource(String uri) {
        super(uri);
        this.sparqlEndpoint = uri;
    }

    public DatasetSource(String uri, String sparqlEndpoint) {
        super(uri);
        this.sparqlEndpoint = sparqlEndpoint;
    }
}
