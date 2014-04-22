package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 8:48 AM
 */
public class RDFDereferenceReader extends DataReader {

    private final String uri;

    public RDFDereferenceReader(String uri) {
        this.uri = uri;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        try {
            //TODO check for relative file names and convert to absolute paths
            model.read(uri);
        } catch (Exception e) {
            throw new TripleReaderException(e.getMessage());
        }
    }
}
