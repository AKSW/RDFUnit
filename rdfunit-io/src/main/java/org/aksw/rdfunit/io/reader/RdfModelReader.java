package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * Encapsulates reading directly from a model
 *
 * @author Dimitris Kontokostas
 * @since 5/7/14 3:36 PM
 */
public class RdfModelReader implements RdfReader {

    private final Model model;

    public RdfModelReader(Model model) {
        super();
        this.model = model;
    }


    @Override
    public Model read() {
        return model;
    }


    @Override
    public void read(Model model) {
        model.add(this.model);
    }


    @Override
    public void readDataset(Dataset dataset){
        dataset.setDefaultModel(dataset.getDefaultModel().union(read()));
    }


    @Override
    public String toString() {
        return "RDFModelReader{" +
                "model=" + model +
                '}';
    }
}
