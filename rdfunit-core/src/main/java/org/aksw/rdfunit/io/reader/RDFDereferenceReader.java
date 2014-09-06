package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.NotFoundException;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 8:48 AM
 */
public class RDFDereferenceReader extends RDFReader {

    private final String uri;

    public RDFDereferenceReader(String uri) {
        super();
        this.uri = uri;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        try {
            //TODO check for relative file names and convert to absolute paths
            model.read(uri);

            // Not found
        } catch (NotFoundException e) {
            throw new TripleReaderException("'" + uri + "' not found", e);
        }

        //org.apache.jena.riot.RiotException -> if wrong format, i.e. turtle instead of RDF/XML

        catch (Exception e) {
            throw new TripleReaderException(e);
        }
    }
}
