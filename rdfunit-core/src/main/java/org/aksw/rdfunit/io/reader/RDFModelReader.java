package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * <p>RDFModelReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         Encapsulates reading directly from a model
 * @since 5/7/14 3:36 PM
 * @version $Id: $Id
 */
public class RDFModelReader extends AbstractRDFReader implements RDFReader  {

    private final Model model;

    /**
     * <p>Constructor for RDFModelReader.</p>
     *
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     */
    public RDFModelReader(Model model) {
        super();
        this.model = model;
    }

    @Override
    public Model read() throws RDFReaderException {
        return model;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {
        model.add(this.model);
    }

    @Override
    public String toString() {
        return "RDFModelReader{" +
                "model=" + model +
                '}';
    }
}
