package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.semarglproject.jena.rdf.rdfa.JenaRdfaReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Dimitris Kontokostas
 *         Reads a model from an InputStream (or a filename)
 * @since 11/14/13 8:37 AM
 */
public class RDFStreamReader extends RDFReader {
    private final InputStream inputStream;
    private final String format;

    public RDFStreamReader(String filename) {
        this(getInputStreamFromFilename(filename), getFormatFromExtension(filename));
    }

    //public RDFStreamReader(InputStream inputStream) {
    //    this(inputStream, "TURTLE");
    //}

    public RDFStreamReader(String filename, String format) {
        this(getInputStreamFromFilename(filename), format);
    }

    public RDFStreamReader(InputStream inputStream, String format) {
        super();
        this.inputStream = inputStream;
        this.format = format;
    }

    @Override
    public void read(Model model) throws RDFReaderException {
        try {
            if (format.equals("RDFA")) {
                // Temporary solution until clearer solution found
                JenaRdfaReader.inject();
            }
            model.read(inputStream, null, format);
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

    public static String getFormatFromExtension(String filename) {
        String format = "TURTLE";
        try {
            int index = filename.lastIndexOf('.');
            String extension = filename.substring(index+1, filename.length() );
            SerializationFormat f = FormatService.getInputFormat(extension);
            if (f != null) {
                format = f.getName();
            }
        } catch (Exception e) {
            return "TURTLE";
        }
        return format;
    }
}
