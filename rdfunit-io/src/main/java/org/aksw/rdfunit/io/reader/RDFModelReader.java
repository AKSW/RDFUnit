package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * <p>RDFModelReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         Encapsulates reading directly from a model
 * @since 5/7/14 3:36 PM
 * @version $Id: $Id
 */
public class RDFModelReader implements RDFReader  {

    private final Model model;

    /**
     * <p>Constructor for RDFModelReader.</p>
     *
     * @param model a {@link org.apache.jena.rdf.model.Model} object.
     */
    public RDFModelReader(Model model) {
        super();
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    public Model read() throws RDFReaderException {
        return model;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {
        model.add(this.model);
    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RDFReaderException {
        dataset.setDefaultModel(dataset.getDefaultModel().union(read()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RDFModelReader{" +
                "model=" + model +
                '}';
    }
}
