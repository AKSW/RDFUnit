package org.aksw.databugger.tripleReaders;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.exceptions.TripleReaderException;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 8:51 AM
 */
public class TripleFirstSuccessReader extends TripleReader {

    private final List<TripleReader> readers;

    public TripleFirstSuccessReader(List<TripleReader> readers) {
        this.readers = readers;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        // return the first successful attempt
        for (TripleReader r: readers ) {
            try {
                r.read(model);
                // return on first read() that does not throw an exception
                return;
            } catch (TripleReaderException e) {
                String message = e.getMessage();
            }
        }

        throw new TripleReaderException("Cannot read from any reader");
    }
}
