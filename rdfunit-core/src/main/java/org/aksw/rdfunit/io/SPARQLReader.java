package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * @author Dimitris Kontokostas
 *         Reads from a SPARQL Endpoint
 *         TODO implement
 * @since 11/14/13 8:58 AM
 */
public class SPARQLReader extends DataReader {

    private final String endpoint;
    private final String graph;

    public SPARQLReader(String endpoint, String graph) {
        this.endpoint = endpoint;
        this.graph = graph;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        //TODO implement
        throw new TripleReaderException("SPARQLReader (" + endpoint + " / " + graph +") not implemented yet");
    }
}
