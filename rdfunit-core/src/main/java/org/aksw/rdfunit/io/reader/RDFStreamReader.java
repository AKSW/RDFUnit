package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.io.format.FormatService;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.semarglproject.jena.rdf.rdfa.JenaRdfaReader;

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
public class RDFStreamReader extends AbstractRDFReader implements RDFReader  {
    private final InputStream inputStream;
    private final String format;

    /**
     * <p>Constructor for RDFStreamReader.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public RDFStreamReader(String filename) {
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
    public RDFStreamReader(String filename, String format) {
        this(getInputStreamFromFilename(filename), format);
    }

    /**
     * <p>Constructor for RDFStreamReader.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @param format a {@link java.lang.String} object.
     */
    public RDFStreamReader(InputStream inputStream, String format) {
        super();
        this.inputStream = inputStream;
        this.format = format;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {
        try {
            if ("RDFA".equals(format)) {
                // Temporary solution until clearer solution found
                JenaRdfaReader.inject();
            }
            RDFDataMgr.read(model, inputStream, null, RDFLanguages.nameToLang(format));
        } catch (Exception e) {
            throw new RDFReaderException(e.getMessage(), e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RDFReaderException {
        try {
            if ("RDFA".equals(format)) {
                // Temporary solution until clearer solution found
                JenaRdfaReader.inject();
            }
            RDFDataMgr.read(dataset, inputStream, null, RDFLanguages.nameToLang(format));
        } catch (Exception e) {
            throw new RDFReaderException(e.getMessage(), e);
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

    @Override
    public String toString() {
        return "RDFStreamReader{" +
                "inputStream=" + inputStream +
                ", format=" + format +
                '}';
    }
}
