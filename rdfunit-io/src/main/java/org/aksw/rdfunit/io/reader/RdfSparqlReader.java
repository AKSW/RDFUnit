package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * <p>RDFSPARQLReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         Reads from a SPARQL Endpoint
 *         TODO implement
 * @since 11/14/13 8:58 AM
 * @version $Id: $Id
 */
public class RdfSparqlReader implements RdfReader {

    private final String endpoint;
    private final String graph;

    /**
     * <p>Constructor for RDFSPARQLReader.</p>
     *
     * @param endpoint a {@link java.lang.String} object.
     * @param graph a {@link java.lang.String} object.
     */
    public RdfSparqlReader(String endpoint, String graph) {
        super();
        this.endpoint = endpoint;
        this.graph = graph;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RdfReaderException {
        //TODO implement
        throw new RdfReaderException("RDFSPARQLReader (" + endpoint + " / " + graph + ") not implemented yet");
    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RdfReaderException {
        //TODO implement
        throw new RdfReaderException("RDFSPARQLReader (" + endpoint + " / " + graph + ") not implemented yet");
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RDFSPARQLReader{" +
                "endpoint='" + endpoint + '\'' +
                ", graph='" + graph + '\'' +
                '}';
    }
}
