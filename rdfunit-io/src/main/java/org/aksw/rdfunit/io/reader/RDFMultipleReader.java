package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

import java.util.Collection;

/**
 * <p>RDFMultipleReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 8:51 AM
 * @version $Id: $Id
 */
public class RDFMultipleReader extends AbstractRDFReader implements RDFReader  {

    private final Collection<RDFReader> readers;

    /**
     * <p>Constructor for RDFMultipleReader.</p>
     *
     * @param readers a {@link java.util.Collection} object.
     */
    public RDFMultipleReader(Collection<RDFReader> readers) {
        super();
        this.readers = readers;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {

        for (RDFReader r : readers) {
            try {
                r.read(model);
            } catch (RDFReaderException e) {
                throw new RDFReaderException("Cannot read from reader: " + e.getMessage());
            }
        }


    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RDFReaderException {

        for (RDFReader r : readers) {
            try {
                r.readDataset(dataset);
            } catch (RDFReaderException e) {
                throw new RDFReaderException("Cannot read from reader: " + e.getMessage());
            }
        }


    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RDFMultipleReader{" +
                "readers=" + readers +
                '}';
    }
}
