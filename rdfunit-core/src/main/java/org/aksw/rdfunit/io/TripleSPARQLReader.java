package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * User: Dimitris Kontokostas
 * Reads from a SPARQL Endpoint
 * TODO implement
 * Created: 11/14/13 8:58 AM
 */
public class TripleSPARQLReader extends TripleReader {

    private final String endpoint;
    private final String graph;
    private final String sparqlQuery;

    public TripleSPARQLReader(String endpoint, String graph, String sparqlQuery) {
        this.endpoint = endpoint;
        this.graph = graph;
        this.sparqlQuery = sparqlQuery;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        //TODO implement
    }
}
