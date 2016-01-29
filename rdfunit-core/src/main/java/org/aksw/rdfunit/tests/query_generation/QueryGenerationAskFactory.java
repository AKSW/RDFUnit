package org.aksw.rdfunit.tests.query_generation;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.query.Query;

/**
 * Factory that returns ASK queries
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:10 PM
 * @version $Id: $Id
 */
public class QueryGenerationAskFactory implements QueryGenerationFactory {

    private final QueryGenerationSelectFactory queryGenerationSelectFactory = new QueryGenerationSelectFactory();

    /** {@inheritDoc} */
    @Override
    public String getSparqlQueryAsString(TestCase testCase) {
        return getSparqlQuery(testCase).toString();
    }

    /** {@inheritDoc} */
    @Override
    public Query getSparqlQuery(TestCase testCase) {
        Query query = queryGenerationSelectFactory.getSparqlQuery(testCase);
        query.setQueryAskType();
        return query;
    }
}
