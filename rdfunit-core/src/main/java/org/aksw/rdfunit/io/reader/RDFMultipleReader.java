package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 8:51 AM
 */
public class RDFMultipleReader extends RDFReader {

    private final Collection<RDFReader> readers;

    public RDFMultipleReader(Collection<RDFReader> readers) {
        super();
        this.readers = readers;
    }

    @Override
    public void read(Model model) throws TripleReaderException {

        for (RDFReader r : readers) {
            try {
                r.read(model);
            } catch (TripleReaderException e) {
                throw new TripleReaderException("Cannot read from reader: " + e.getMessage());
            }
        }


    }
}
