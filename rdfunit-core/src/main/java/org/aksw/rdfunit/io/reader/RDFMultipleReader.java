package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;

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
    public void read(Model model) throws RDFReaderException {

        for (RDFReader r : readers) {
            try {
                r.read(model);
            } catch (RDFReaderException e) {
                throw new RDFReaderException("Cannot read from reader: " + e.getMessage());
            }
        }


    }
}
