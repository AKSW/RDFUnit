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
public class RDFMultipleWriter extends RDFWriter {
    private final Collection<RDFWriter> writers;

    /**
     * <p>Constructor for RDFMultipleWriter.</p>
     *
     * @param writers a {@link java.util.Collection} object.
     */
    public RDFMultipleWriter(Collection<RDFWriter> writers) {
        super();
        this.writers = writers;
    }

    /** {@inheritDoc} */
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
