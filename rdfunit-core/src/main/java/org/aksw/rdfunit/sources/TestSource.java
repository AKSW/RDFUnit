package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.io.Closeable;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 12 /10/14 9:54 AM
 */
public interface TestSource extends Source, AutoCloseable {

    QueryingConfig getQueryingConfig();

    Collection<SchemaSource> getReferencesSchemata();

    QueryExecutionFactory getExecutionFactory();

}
