package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12 /10/14 9:54 AM
 * @version $Id: $Id
 */
public interface TestSource extends Source {

    /**
     * <p>getQueryingConfig.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.QueryingConfig} object.
     */
    QueryingConfig getQueryingConfig();

    /**
     * <p>getReferencesSchemata.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<SchemaSource> getReferencesSchemata();

    /**
     * <p>getExecutionFactory.</p>
     *
     * @return a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     */
    QueryExecutionFactory getExecutionFactory();



}
