package org.aksw.rdfunit.io.reader;

import org.aksw.rdfunit.io.format.FormatService;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * <p>RDFStreamReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         Reads a model from an InputStream (or a filename)
 * @since 11/14/13 8:37 AM
 * @version $Id: $Id
 */
public class RdfStreamReader implements RdfReader {
    private final InputStream inputStream;
    private final String format;

    /**
     * <p>Constructor for RDFStreamReader.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public RdfStreamReader(String filename) {
        this(getInputStreamFromFilename(filename), FormatService.getFormatFromExtension(filename));
    }

    //public RDFStreamReader(InputStream inputStream) {
    //    this(inputStream, "TURTLE");
    //}

    /**
     * <p>Constructor for RDFStreamReader.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @param format a {@link java.lang.String} object.
     */
    public RdfStreamReader(String filename, String format) {
        this(getInputStreamFromFilename(filename), format);
    }

    /**
     * <p>Constructor for RDFStreamReader.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @param format a {@link java.lang.String} object.
     */
    public RdfStreamReader(InputStream inputStream, String format) {
        super();
        this.inputStream = inputStream;
        this.format = format;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RdfReaderException {
        try {
            RDFDataMgr.read(model, inputStream, null, RDFLanguages.nameToLang(format));
        } catch (Exception e) {
            throw new RdfReaderException(e.getMessage(), e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RdfReaderException {
        try {
            RDFDataMgr.read(dataset, inputStream, null, RDFLanguages.nameToLang(format));
        } catch (Exception e) {
            throw new RdfReaderException(e.getMessage(), e);
        }

    }

    // Returns a FileInputStream or null in case of exception
    // We want to raise the exception only at ready time
    private static InputStream getInputStreamFromFilename(String filename) {
        try {
            return new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            // do not handle exception at construction time
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RDFStreamReader{" +
                "inputStream=" + inputStream +
                ", format=" + format +
                '}';
    }
}
