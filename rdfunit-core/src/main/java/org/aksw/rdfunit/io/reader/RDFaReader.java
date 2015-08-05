package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;
import org.semarglproject.jena.rdf.rdfa.JenaRdfaReader;

/**
 * Creates an RDFa reader by using the
 * http://semarglproject.org/ library
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:48 AM
 * @version $Id: $Id
 */
public class RDFaReader extends AbstractRDFReader implements RDFReader {

    private final String uri;

    /**
     * <p>Constructor for RDFaReader.</p>
     *
     * @param uri a {@link java.lang.String} object.
     */
    public RDFaReader(String uri) {
        super();
        this.uri = uri;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {
        try {
            // Init RDFa Reader
            JenaRdfaReader.inject();
            model.read(uri, null, "RDFA");
        }
        catch (Exception e) {
            throw new RDFReaderException(e);
        }
    }
}
