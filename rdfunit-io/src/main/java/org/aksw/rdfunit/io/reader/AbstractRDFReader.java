package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Skeleton abstract class for RDFReader
 *
 * @author Dimitris Kontokostas
 * @since 5/28/15 8:39 PM
 * @version $Id: $Id
 */
public abstract class AbstractRDFReader implements RDFReader {

    /** {@inheritDoc} */
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


    /** {@inheritDoc} */
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
