package org.aksw.databugger.tripleReaders;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.exceptions.TripleReaderException;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 8:48 AM
 */
public class TripleDereferenceReader extends TripleReader {

    private final String uri;

    public TripleDereferenceReader(String uri) {
        this.uri = uri;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        try {
            model.read(uri);
        } catch (Exception e) {
            throw new TripleReaderException(e.getMessage());
        }
    }
}
