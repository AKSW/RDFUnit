package org.aksw.databugger.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.databugger.exceptions.TripleWriterException;

import java.io.StringWriter;

/**
 * User: Dimitris Kontokostas
 * Writes a model to a string (for display in screen / web page)
 * Created: 11/14/13 5:12 PM
 */
public class TripleStringWriter extends TripleWriter {

    private final StringBuilder str;
    private final String format;

    public TripleStringWriter() {
        this(new StringBuilder(), "TURTLE");
    }

    public TripleStringWriter(StringBuilder str) {
        this(str, "TURTLE");
    }

    public TripleStringWriter(StringBuilder str, String format) {
        this.str = str;
        this.format = format;
    }

    public String getString(){
        return str.toString();
    }

    @Override
    public void write(Model model) throws TripleWriterException {
        try {
            StringWriter out = new StringWriter();
            model.write(out, format);
            str.append(out.toString());
        } catch (Exception e) {
            throw new TripleWriterException(e.getMessage());
        }

    }
}
