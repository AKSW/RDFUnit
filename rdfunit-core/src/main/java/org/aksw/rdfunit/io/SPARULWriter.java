package org.aksw.rdfunit.io;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.exceptions.TripleWriterException;

/**
 * @author Dimitris Kontokostas
 *         Stores a model to a triplestore
 * @since 11/14/13 1:04 PM
 */
public class SPARULWriter extends DataWriter {
    @Override
    public void write(QueryExecutionFactory qef) throws TripleWriterException {
        //TODO implement
        throw new TripleWriterException("SPARQLReader not implemented yet");
    }
}
