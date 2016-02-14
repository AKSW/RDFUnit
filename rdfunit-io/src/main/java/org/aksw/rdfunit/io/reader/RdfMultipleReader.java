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
public class RdfMultipleReader implements RdfReader {

    private final Collection<RdfReader> readers;

    /**
     * <p>Constructor for RDFMultipleReader.</p>
     *
     * @param readers a {@link java.util.Collection} object.
     */
    public RdfMultipleReader(Collection<RdfReader> readers) {
        super();
        this.readers = readers;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RdfReaderException {

        for (RdfReader r : readers) {
            try {
                r.read(model);
            } catch (RdfReaderException e) {
                throw new RdfReaderException("Cannot read from reader: " + e.getMessage());
            }
        }


    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RdfReaderException {

        for (RdfReader r : readers) {
            try {
                r.readDataset(dataset);
            } catch (RdfReaderException e) {
                throw new RdfReaderException("Cannot read from reader: " + e.getMessage());
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
