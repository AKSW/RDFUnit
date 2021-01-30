package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

/**
 * Stores a model to a triplestore
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 1:04 PM
 */
public class RdfSparulWriter implements RdfWriter {

  @Override
  public void write(QueryExecutionFactory qef) throws RdfWriterException {
    //TODO implement
    throw new RdfWriterException("RDFSPARQLReader not implemented yet");
  }
}
