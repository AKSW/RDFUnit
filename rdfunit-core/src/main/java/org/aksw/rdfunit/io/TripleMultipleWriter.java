package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleWriterException;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 1:13 PM
 */
public class TripleMultipleWriter extends TripleWriter {
    private final java.util.Collection <TripleWriter> writers;

    public TripleMultipleWriter(java.util.Collection <TripleWriter> writers) {
        this.writers = writers;
    }

    @Override
    public void write(Model model) throws TripleWriterException {
        //TODO check for early exceptions
        for (TripleWriter w : writers) {
            w.write(model);
        }
    }
}
