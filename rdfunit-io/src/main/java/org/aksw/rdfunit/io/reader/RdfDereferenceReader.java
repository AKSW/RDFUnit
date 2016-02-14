package org.aksw.rdfunit.io.reader;

import org.aksw.rdfunit.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.NotFoundException;

import java.io.File;

/**
 * <p>RDFDereferenceReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 8:48 AM
 * @version $Id: $Id
 */
public class RdfDereferenceReader implements RdfReader {

    private final String uri;

    /**
     * <p>Constructor for RDFDereferenceReader.</p>
     *
     * @param uri a {@link java.lang.String} object.
     */
    public RdfDereferenceReader(String uri) {
        super();
        if (IOUtils.isFile(uri)) {
            this.uri = new File(uri).getAbsolutePath();
        } else {
            this.uri = uri;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RdfReaderException {
        try {
            RDFDataMgr.read(model, uri);

            // Not found
        } catch (NotFoundException e) {
            throw new RdfReaderException("'" + uri + "' not found", e);
        }

        //org.apache.jena.riot.RiotException -> if wrong format, i.e. turtle instead of RDF/XML

        catch (Exception e) {
            throw new RdfReaderException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RdfReaderException {
        try {
            RDFDataMgr.read(dataset, uri);

            // Not found
        } catch (NotFoundException e) {
            throw new RdfReaderException("'" + uri + "' not found", e);
        }

        //org.apache.jena.riot.RiotException -> if wrong format, i.e. turtle instead of RDF/XML

        catch (Exception e) {
            throw new RdfReaderException(e);
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
