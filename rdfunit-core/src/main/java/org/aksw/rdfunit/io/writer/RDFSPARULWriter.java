package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

/**
 * @author Dimitris Kontokostas
 *         Stores a model to a triplestore
 * @since 11/14/13 1:04 PM
 */
public class RDFSPARULWriter extends RDFWriter {
    @Override
    public void write(QueryExecutionFactory qef) throws RDFWriterException {
        //TODO implement
        throw new RDFWriterException("RDFSPARQLReader not implemented yet");
    }
}
