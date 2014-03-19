package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.exceptions.TripleWriterException;

/**
 * User: Dimitris Kontokostas
 * reades from a TripleReader and caches result
 * Created: 11/14/13 1:09 PM
 */
public class TripleReadAndCacheReader extends TripleReader {
    private final TripleReader reader;
    private final TripleWriter writer;

    public TripleReadAndCacheReader(TripleReader reader, TripleWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        reader.read(model);
        //If read succeeds try to write
        try {
            writer.write(model);
        } catch (TripleWriterException e) {

        }
    }
}
