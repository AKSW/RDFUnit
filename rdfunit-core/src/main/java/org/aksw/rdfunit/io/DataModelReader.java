package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * User: Dimitris Kontokostas
 * Encapsulates reading directly from a model
 * Created: 5/7/14 3:36 PM
 */
public class DataModelReader extends DataReader {

    private final Model model;

    public DataModelReader(Model model) {
        this.model = model;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        model.add(this.model);
    }
}
