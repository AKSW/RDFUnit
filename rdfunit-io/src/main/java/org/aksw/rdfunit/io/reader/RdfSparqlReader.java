package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * Reads from a SPARQL Endpoint
 *         TODO implement
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:58 AM
 */
public class RdfSparqlReader implements RdfReader {

    private final String endpoint;
    private final String graph;

    public RdfSparqlReader(String endpoint, String graph) {
        super();
        this.endpoint = endpoint;
        this.graph = graph;
    }


    @Override
    public void read(Model model) throws RdfReaderException {
        //TODO implement
        throw new RdfReaderException("RDFSPARQLReader (" + endpoint + " / " + graph + ") not implemented yet");
    }


    @Override
    public void readDataset(Dataset dataset) throws RdfReaderException {
        //TODO implement
        throw new RdfReaderException("RDFSPARQLReader (" + endpoint + " / " + graph + ") not implemented yet");
    }


    @Override
    public String toString() {
        return "RDFSPARQLReader{" +
                "endpoint='" + endpoint + '\'' +
                ", graph='" + graph + '\'' +
                '}';
    }
}
