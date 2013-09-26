package org.aksw.databugger.sources;


import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.net.URI;

/**
 * User: Dimitris Kontokostas
 * Abstract class for a data source. A source can be various things like a dataset, a vocabulary or an application
 * Date: 9/16/13 1:15 PM
 */

public abstract class Source {
    public final String uri;
    private QueryExecutionFactory queryFactory;

    public Source(String uri) {
        this.uri = uri;
    }

    public abstract TestAppliesTo getSourceType();

    protected abstract QueryExecutionFactory initQueryFactory();

    public QueryExecutionFactory getExecutionFactory() {
        // TODO not thread safe but minor
        if (queryFactory == null)
            queryFactory = initQueryFactory();
        return queryFactory;
    }

    public String getRelativeFilename(){
        String retVal = null;
        try {
            URI tmp = new URI(uri);
            String host = tmp.getHost();
            String path = tmp.getPath();
            retVal = host + path + "/" + getSourceType().name() + ".ttl";
        } catch (Exception e) {
            // TODO handle exception
        }

        return retVal;

    }
}
