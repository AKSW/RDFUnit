package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.util.Collection;

/**
 * <p>RDFMultipleWriter class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 1:13 PM
 * @version $Id: $Id
 */
public class RdfMultipleWriter implements RdfWriter {
    private final Collection<RdfWriter> writers;

    /**
     * <p>Constructor for RDFMultipleWriter.</p>
     *
     * @param writers a {@link java.util.Collection} object.
     */
    public RdfMultipleWriter(Collection<RdfWriter> writers) {
        super();
        this.writers = writers;
    }

    /** {@inheritDoc} */
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
