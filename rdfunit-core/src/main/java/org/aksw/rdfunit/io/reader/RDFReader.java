package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Triple reader superclass
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:33 AM
 */
public abstract class RDFReader {

    /**
     * Reads RDF and returns a {@code Model}
     *
     * @return a {@code Model} with the data read
     * @throws RDFReaderException
     */
    public Model read() throws RDFReaderException {
        try {
            Model model = ModelFactory.createDefaultModel();
            read(model);
            return model;
        } catch (Exception e) {
            throw new RDFReaderException(e);
        }
    }

    /**
     * Reads RDF and writes the data in the Model provided by the user
     *
     * @param model the model we want to write our data into
     * @throws RDFReaderException
     */
    public abstract void read(Model model) throws RDFReaderException;
}
