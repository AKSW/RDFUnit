package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Skeleton abstract class for RDFReader
 *
 * @author Dimitris Kontokostas
 * @since 5/28/15 8:39 PM
 */
public abstract class AbstractRDFReader implements RDFReader {

    @Override
    public Model read() throws RDFReaderException {
        try {
            Model model = ModelFactory.createDefaultModel();
            read(model);
            return model;
        } catch (Exception e) {
            throw new RDFReaderException(e);
        }
    }


    @Override
    public Dataset readDataset() throws RDFReaderException {
        try {
            Dataset dataset = DatasetFactory.createMem();
            readDataset(dataset);
            return dataset;
        } catch (Exception e) {
            throw new RDFReaderException(e);
        }
    }
}
