package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 8:51 AM
 */
public class RDFFirstSuccessReader extends RDFReader {

    private final Collection<RDFReader> readers;

    public RDFFirstSuccessReader(Collection<RDFReader> readers) {
        super();
        this.readers = readers;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        String message = "";
        // return the first successful attempt
        for (RDFReader r : readers) {
            try {
                r.read(model);
                // return on first read() that does not throw an exception
                return;
            } catch (TripleReaderException e) {
                message += "\n";
                if (e.getMessage() != null) {
                    message += e.getMessage();
                } else {
                    message += e.toString();
                }
            }
        }

        throw new TripleReaderException("Cannot read from any reader: " + message);
    }
}
