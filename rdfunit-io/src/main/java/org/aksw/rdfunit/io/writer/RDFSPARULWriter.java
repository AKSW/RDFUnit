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
public class RDFSPARULWriter implements RDFWriter {
    /** {@inheritDoc} */
    @Override
    public void write(QueryExecutionFactory qef) throws RDFWriterException {
        //TODO implement
        throw new RDFWriterException("RDFSPARQLReader not implemented yet");
    }
}
