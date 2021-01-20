package org.aksw.rdfunit.sources;

import java.util.Collection;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

/**
 * @author Dimitris Kontokostas
 * @since 12 /10/14 9:54 AM
 */
public interface TestSource extends Source, AutoCloseable {

  QueryingConfig getQueryingConfig();

  Collection<SchemaSource> getReferencesSchemata();

  QueryExecutionFactory getExecutionFactory();

}
