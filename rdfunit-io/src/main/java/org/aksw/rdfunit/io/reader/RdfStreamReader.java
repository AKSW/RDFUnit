package org.aksw.rdfunit.io.reader;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.format.FormatService;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Reads a model from an InputStream (or a filename)
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:37 AM
 */
@Slf4j
public class RdfStreamReader implements RdfReader {

    private final InputStream inputStream;
    private final String format;

    public RdfStreamReader(String filename) {
        this(getInputStreamFromFilename(filename), FormatService.getFormatFromExtension(filename));
    }

    //public RDFStreamReader(InputStream inputStream) {
    //    this(inputStream, "TURTLE");
    //}

    public RdfStreamReader(String filename, String format) {
        this(getInputStreamFromFilename(filename), format);
    }

    public RdfStreamReader(InputStream inputStream, String format) {
        super();
        this.inputStream = inputStream;
        this.format = format;
    }


    @Override
    public void read(Model model) throws RdfReaderException {
        try {
            RDFDataMgr.read(model, inputStream, null, RDFLanguages.nameToLang(format));
        } catch (Exception e) {
            throw new RdfReaderException(e.getMessage(), e);
        }

    }


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
            log.debug("Error initializing RdfStreamReader, file '{}' not found", filename);
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
