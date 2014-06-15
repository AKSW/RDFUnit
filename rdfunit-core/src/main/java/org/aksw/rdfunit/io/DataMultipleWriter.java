package org.aksw.rdfunit.io;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.exceptions.TripleWriterException;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 1:13 PM
 */
public class DataMultipleWriter extends DataWriter {
    private final java.util.Collection<DataWriter> writers;

    public DataMultipleWriter(java.util.Collection<DataWriter> writers) {
        this.writers = writers;
    }

    @Override
    public void write(QueryExecutionFactory model) throws TripleWriterException {
        //TODO check for early exceptions
        for (DataWriter w : writers) {
            if (w != null) {
                w.write(model);
            }
        }
    }
}
