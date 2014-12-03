package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.enums.TestAppliesTo;

/**
 * <p>ApplicationSource class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/16/13 1:57 PM
 * @version $Id: $Id
 */
public class ApplicationSource extends Source {

    /**
     * <p>Constructor for ApplicationSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     */
    public ApplicationSource(String prefix, String uri) {
        super(prefix, uri);
    }

    /** {@inheritDoc} */
    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Application;
    }

    /** {@inheritDoc} */
    @Override
    protected QueryExecutionFactory initQueryFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
