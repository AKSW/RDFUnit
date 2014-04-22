package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleWriterException;

import java.io.StringWriter;

/**
 * User: Dimitris Kontokostas
 * Writes a model to a string (for display in screen / web page)
 * Created: 11/14/13 5:12 PM
 */
public class RDFStringWriter extends DataWriter {

    private final StringBuilder str;
    private final String format;

    public RDFStringWriter() {
        this(new StringBuilder(), "TURTLE");
    }

    public RDFStringWriter(StringBuilder str) {
        this(str, "TURTLE");
    }

    public RDFStringWriter(StringBuilder str, String format) {
        this.str = str;
        this.format = format;
    }

    public String getString() {
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
