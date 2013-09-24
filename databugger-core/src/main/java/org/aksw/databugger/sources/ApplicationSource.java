package org.aksw.databugger.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:57 PM
 */
public class ApplicationSource extends Source {

    public ApplicationSource(String uri) {
        super(uri);
    }

    @Override
    protected QueryExecutionFactory initQueryFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
