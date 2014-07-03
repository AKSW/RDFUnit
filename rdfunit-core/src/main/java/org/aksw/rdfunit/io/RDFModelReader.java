package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * @author Dimitris Kontokostas
 *         Encapsulates reading directly from a model
 * @since 5/7/14 3:36 PM
 */
public class RDFModelReader extends RDFReader {

    private final Model model;

    public RDFModelReader(Model model) {
        this.model = model;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        model.add(this.model);
    }
}
