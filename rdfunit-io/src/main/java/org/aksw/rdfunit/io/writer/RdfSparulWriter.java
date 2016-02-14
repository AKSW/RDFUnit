package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

/**
 * <p>RDFSPARULWriter class.</p>
 *
 * @author Dimitris Kontokostas
 *         Stores a model to a triplestore
 * @since 11/14/13 1:04 PM
 * @version $Id: $Id
 */
public class RdfSparulWriter implements RdfWriter {
    /** {@inheritDoc} */
    @Override
    public void write(QueryExecutionFactory qef) throws RdfWriterException {
        //TODO implement
        throw new RdfWriterException("RDFSPARQLReader not implemented yet");
    }
}
