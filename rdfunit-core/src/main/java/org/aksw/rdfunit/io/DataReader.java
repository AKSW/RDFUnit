package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * User: Dimitris Kontokostas
 * Interface for a reader
 * Created: 11/14/13 8:33 AM
 */
public abstract class DataReader {

    /*
    * reads and returns a new jena Model
    * */
    public Model read() throws TripleReaderException {
        Model model = ModelFactory.createDefaultModel();
        read(model);
        return model;
    }

    /*
    * reads and adds triples in an existing jena Model
    * */
    public abstract void read(Model model) throws TripleReaderException;
}
