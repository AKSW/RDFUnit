package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12 /10/14 9:54 AM
 */
public interface TestSource extends Source {

    QueryingConfig getQueryingConfig();

    Collection<SchemaSource> getReferencesSchemata();

    /**
     * <p>getExecutionFactory.</p>
     *
     * @return a {@link QueryExecutionFactory} object.
     */
    QueryExecutionFactory getExecutionFactory();



}
