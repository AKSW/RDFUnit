package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.exceptions.TripleWriterException;

/**
 * User: Dimitris Kontokostas
 * reads from a DataReader and caches result
 * Created: 11/14/13 1:09 PM
 */
public class DataReadAndCacheReader extends DataReader {
    private final DataReader reader;
    private final DataWriter writer;

    public DataReadAndCacheReader(DataReader reader, DataWriter writer) {
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
