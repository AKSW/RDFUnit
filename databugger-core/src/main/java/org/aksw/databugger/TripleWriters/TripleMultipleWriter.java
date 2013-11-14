package org.aksw.databugger.TripleWriters;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.databugger.exceptions.TripleWriterException;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 1:13 PM
 */
public class TripleMultipleWriter extends TripleWriter {
    private final List<TripleWriter> writers;

    public TripleMultipleWriter(List<TripleWriter> writers) {
        this.writers = writers;
    }

    @Override
    public void write(Model model) throws TripleWriterException {
        //TODO check for early exceptions
        for (TripleWriter w: writers) {
            w.write(model);
        }
    }
}
