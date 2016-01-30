package org.aksw.rdfunit.tests.query_generation;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

/**
 * Factory that returns aggregate count queries
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:07 PM
 * @version $Id: $Id
 */
public class QueryGenerationCountFactory implements QueryGenerationFactory {

    private static final String selectClauseSimple = " SELECT (count(DISTINCT ?this ) AS ?total ) WHERE ";

    private static final String selectClauseGroupStart = " SELECT (count(DISTINCT ?this ) AS ?total ) WHERE {" +
                                                   " SELECT ?this WHERE ";
    private static final String selectClauseGroupEnd = "}";

    /** {@inheritDoc} */
    @Override
    public String getSparqlQueryAsString(TestCase testCase) {
        return getSparqlQuery(testCase).toString();
    }

    /** {@inheritDoc} */
    @Override
    public Query getSparqlQuery(TestCase testCase) {
        Query query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
                        selectClauseSimple + testCase.getSparqlWhere()
        );
        if (!query.hasGroupBy()) {
            return query;
        }

        // When we have HAVING the aggregate is calculated against the HAVING expression
        // This way we enclose the query in a sub-select and calculate the count () correctly
        // See https://issues.apache.org/jira/browse/JENA-766
        query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
                        selectClauseGroupStart
                        + testCase.getSparqlWhere() +
                        selectClauseGroupEnd
        );

        return query;
    }
}
