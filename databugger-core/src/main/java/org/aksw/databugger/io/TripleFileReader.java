package org.aksw.databugger.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.databugger.exceptions.TripleReaderException;

import java.io.File;
import java.io.FileInputStream;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 8:37 AM
 */
public class TripleFileReader extends TripleReader {
    private final String filename;
    private final String format;

    public TripleFileReader(String filename) {
        this.filename = filename;
        this.format = "TURTLE";
    }

    public TripleFileReader(String filename, String format) {
        this.filename = filename;
        this.format = format;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        try {
            File f = new File(filename);
            model.read(new FileInputStream(f), null, format);
        } catch (Exception e) {
            throw new TripleReaderException(e.getMessage());
        }

    }
}
