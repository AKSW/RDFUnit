package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.io.IOUtils;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.rdf.model.Model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * <p>RDFStreamWriter class.</p>
 *
 * @author Dimitris Kontokostas
 *         Writes a QEF / Model to an arbitrary OutputStream (RDFFileWriter does more logic and provided separate)
 * @since 5/27/14 5:46 PM
 * @version $Id: $Id
 */
public class RdfStreamWriter implements RdfWriter {
    private final OutputStream outputStream;
    private final String filetype;

    /**
     * <p>Constructor for RDFStreamWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     * @param filetype a {@link java.lang.String} object.
     */
    public RdfStreamWriter(OutputStream outputStream, String filetype) {
        super();
        this.outputStream = outputStream;
        this.filetype = filetype;
    }

    /**
     * <p>Constructor for RDFStreamWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public RdfStreamWriter(OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
        this.filetype = "TURTLE";
    }

    /** {@inheritDoc} */
    @Override
    public void write(QueryExecutionFactory qef) throws RdfWriterException {
        try {
            Model model = IOUtils.getModelFromQueryFactory(qef);
            PrefixNSService.setNSPrefixesInModel(model);
            model.write(outputStream, filetype);

        } catch (Exception e) {
            throw new RdfWriterException("Error writing in OutputStream: " + e.getMessage(), e);
        }
    }

    // Returns a FileInputStream or null in case of exception
    // We want to raise the exception only at ready time
    /**
     * <p>getOutputStreamFromFilename.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link java.io.OutputStream} object.
     */
    public static OutputStream getOutputStreamFromFilename(String filename) {
        try {
            return new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // do not handle exception at construction time
            return null;
        }
    }
}
