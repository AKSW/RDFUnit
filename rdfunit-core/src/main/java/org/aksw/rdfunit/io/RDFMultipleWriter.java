package org.aksw.rdfunit.io;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.exceptions.TripleWriterException;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 1:13 PM
 */
public class RDFMultipleWriter extends RDFWriter {
    private final Collection<RDFWriter> writers;

    public RDFMultipleWriter(Collection<RDFWriter> writers) {
        super();
        this.writers = writers;
    }

    @Override
    public void write(QueryExecutionFactory model) throws TripleWriterException {
        //TODO check for early exceptions
        for (RDFWriter w : writers) {
            if (w != null) {
                w.write(model);
            }
        }
    }
}
