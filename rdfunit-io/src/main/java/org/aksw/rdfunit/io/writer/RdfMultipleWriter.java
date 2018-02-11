package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 11/14/13 1:13 PM
 */
public class RdfMultipleWriter implements RdfWriter {
    private final Collection<RdfWriter> writers;

    public RdfMultipleWriter(Collection<RdfWriter> writers) {
        super();
        this.writers = writers;
    }


    @Override
    public void write(QueryExecutionFactory model) throws RdfWriterException {
        //TODO check for early exceptions
        for (RdfWriter w : writers) {
            if (w != null) {
                w.write(model);
            }
        }
    }
}
