package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.NotFoundException;

/**
 * <p>RDFDereferenceReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 8:48 AM
 * @version $Id: $Id
 */
public class RDFDereferenceReader extends AbstractRDFReader implements RDFReader {

    private final String uri;

    /**
     * <p>Constructor for RDFDereferenceReader.</p>
     *
     * @param uri a {@link java.lang.String} object.
     */
    public RDFDereferenceReader(String uri) {
        super();
        this.uri = uri;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {
        try {
            //TODO check for relative file names and convert to absolute paths
            model.read(uri);

            // Not found
        } catch (NotFoundException e) {
            throw new RDFReaderException("'" + uri + "' not found", e);
        }

        //org.apache.jena.riot.RiotException -> if wrong format, i.e. turtle instead of RDF/XML

        catch (Exception e) {
            throw new RDFReaderException(e);
        }
    }
}
