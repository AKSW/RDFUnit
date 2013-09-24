package org.aksw.databugger.sources;


import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

/**
 * User: Dimitris Kontokostas
 * Abstract class for a data source. A source can be various things like a dataset, a vocabulary or an application
 * Date: 9/16/13 1:15 PM
 */

public abstract class Source {
    public final String uri;
    public QueryExecutionFactory queryFactory;

    public Source(String uri) {
        this.uri = uri;
    }

    protected abstract QueryExecutionFactory initQueryFactory();
}
