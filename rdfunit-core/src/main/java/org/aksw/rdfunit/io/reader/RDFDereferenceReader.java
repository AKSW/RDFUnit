package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.NotFoundException;
import org.aksw.rdfunit.io.IOUtils;
import org.apache.jena.riot.RDFDataMgr;

import java.io.File;

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
        if (IOUtils.isFile(uri)) {
            this.uri = new File(uri).getAbsolutePath();
        } else {
            this.uri = uri;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {
        try {
            RDFDataMgr.read(model, uri);

            // Not found
        } catch (NotFoundException e) {
            throw new RDFReaderException("'" + uri + "' not found", e);
        }

        //org.apache.jena.riot.RiotException -> if wrong format, i.e. turtle instead of RDF/XML

        catch (Exception e) {
            throw new RDFReaderException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RDFReaderException {
        try {
            RDFDataMgr.read(dataset, uri);

            // Not found
        } catch (NotFoundException e) {
            throw new RDFReaderException("'" + uri + "' not found", e);
        }

        //org.apache.jena.riot.RiotException -> if wrong format, i.e. turtle instead of RDF/XML

        catch (Exception e) {
            throw new RDFReaderException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RDFDereferenceReader{" +
                "uri='" + uri + '\'' +
                '}';
    }
}
