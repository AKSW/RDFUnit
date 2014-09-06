package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Dimitris Kontokostas
 *         Encapsulates reading directly from a model
 * @since 5/7/14 3:36 PM
 */
public class RDFModelReader extends RDFReader {

    private final Model model;

    public RDFModelReader(Model model) {
        super();
        this.model = model;
    }

    @Override
    public void read(Model model) throws RDFReaderException {
        model.add(this.model);
    }
}
