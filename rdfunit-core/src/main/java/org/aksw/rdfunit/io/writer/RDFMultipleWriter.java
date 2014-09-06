package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

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
    public void write(QueryExecutionFactory model) throws RDFWriterException {
        //TODO check for early exceptions
        for (RDFWriter w : writers) {
            if (w != null) {
                w.write(model);
            }
        }
    }
}
