package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: Dimitris Kontokostas
 * Reads a model from an InputStream (or a filename)
 * Created: 11/14/13 8:37 AM
 */
public class RDFStreamReader extends DataReader {
    private final InputStream inputStream;
    private final String format;

    public RDFStreamReader(String filename) {
        this(getInputStreamFromFilename(filename), "TURTLE");
    }

    public RDFStreamReader(InputStream inputStream) {
        this(inputStream, "TURTLE");
    }

    public RDFStreamReader(String filename, String format) {
        this(getInputStreamFromFilename(filename), format);
    }

    public RDFStreamReader(InputStream inputStream, String format) {
        this.inputStream = inputStream;
        this.format = format;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        try {
            model.read(inputStream, null, format);
        } catch (Exception e) {
            throw new TripleReaderException(e.getMessage(), e);
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
}
