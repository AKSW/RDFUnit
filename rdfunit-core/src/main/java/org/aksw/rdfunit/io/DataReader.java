package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * Triple reader superclass
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:33 AM
 */
public abstract class DataReader {

    /**
     * Reads RDF and returns a {@code Model}
     *
     * @return a {@code Model} with the data read
     * @throws TripleReaderException
     */
    public Model read() throws TripleReaderException {
        try {
            Model model = ModelFactory.createDefaultModel();
            read(model);
            return model;
        } catch (Exception e) {
            throw new TripleReaderException(e);
        }
    }

    /**
     * Reads RDF and writes the data in the Model provided by the user
     *
     * @param model the model we want to write our data into
     * @throws TripleReaderException
     */
    public abstract void read(Model model) throws TripleReaderException;
}
