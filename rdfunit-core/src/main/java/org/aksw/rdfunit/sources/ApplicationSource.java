package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.enums.TestAppliesTo;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/16/13 1:57 PM
 */
public class ApplicationSource extends Source {

    public ApplicationSource(String prefix, String uri) {
        super(prefix, uri);
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Application;
    }

    @Override
    protected QueryExecutionFactory initQueryFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
