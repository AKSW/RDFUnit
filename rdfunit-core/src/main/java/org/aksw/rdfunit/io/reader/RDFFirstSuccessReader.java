package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import java.util.Collection;

/**
 * <p>RDFFirstSuccessReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 8:51 AM
 * @version $Id: $Id
 */
public class RDFFirstSuccessReader extends AbstractRDFReader implements RDFReader  {

    private final Collection<RDFReader> readers;

    /**
     * <p>Constructor for RDFFirstSuccessReader.</p>
     *
     * @param readers a {@link java.util.Collection} object.
     */
    public RDFFirstSuccessReader(Collection<RDFReader> readers) {
        super();
        this.readers = readers;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {
        StringBuilder message = new StringBuilder();
        // return the first successful attempt
        for (RDFReader r : readers) {
            try {
                r.read(model);
                // return on first read() that does not throw an exception
                return;
            } catch (RDFReaderException e) {
                message.append("\n");
                if (e.getMessage() != null) {
                    message.append(e.getMessage());
                } else {
                    message.append(e);
                }
            }
        }

        throw new RDFReaderException("Cannot read from any reader: " + message.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RDFReaderException {
        StringBuilder message = new StringBuilder();
        // return the first successful attempt
        for (RDFReader r : readers) {
            try {
                r.readDataset(dataset);
                // return on first read() that does not throw an exception
                return;
            } catch (RDFReaderException e) {
                message.append("\n");
                if (e.getMessage() != null) {
                    message.append(e.getMessage());
                } else {
                    message.append(e);
                }
            }
        }

        throw new RDFReaderException("Cannot read from any reader: " + message.toString());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RDFFirstSuccessReader{" +
                "readers=" + readers +
                '}';
    }
}
