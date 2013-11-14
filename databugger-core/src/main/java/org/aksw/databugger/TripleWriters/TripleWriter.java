package org.aksw.databugger.TripleWriters;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.databugger.exceptions.TripleWriterException;

/**
 * User: Dimitris Kontokostas
 * Triple writer superclass (could be an interface)
 * Created: 11/14/13 12:59 PM
 */
public abstract class TripleWriter {
    public abstract void write(Model model) throws TripleWriterException;
}
