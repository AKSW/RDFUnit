package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 8:37 AM
 */
public class RDFFileReader extends DataReader {
    private final InputStream file;
    private final String format;

    public RDFFileReader(String filename) {
        this(getStreamFromFilename(filename),"TURTLE");
    }

    public RDFFileReader(InputStream file) {
        this(file, "TURTLE");
    }

    public RDFFileReader(String filename, String format) {
        this(getStreamFromFilename(filename),format);
    }

    public RDFFileReader(InputStream file, String format) {
        this.file = file;
        this.format = format;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        try {
            model.read(file, null, format);
        } catch (Exception e) {
            throw new TripleReaderException(e.getMessage());
        }

    }

    // Returns a FileInputStream or null in case of exception
    // We want to raise the exception only at ready time
    private static InputStream getStreamFromFilename(String filename) {
        try {
            return new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            // do not handle exception at construction time
            return null;
        }
    }
}
