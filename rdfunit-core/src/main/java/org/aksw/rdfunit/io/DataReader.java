package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * @author Dimitris Kontokostas
 *         Interface for a reader
 * @since 11/14/13 8:33 AM
 */
public abstract class DataReader {

    /*
    * reads and returns a new jena Model
    * */
    public Model read() throws TripleReaderException {
        try {
            Model model = ModelFactory.createDefaultModel();
            read(model);
            return model;
        } catch (Exception e) {
            throw new TripleReaderException(e);
        }
    }

    /*
    * reads and adds triples in an existing jena Model
    * */
    public abstract void read(Model model) throws TripleReaderException;
}
