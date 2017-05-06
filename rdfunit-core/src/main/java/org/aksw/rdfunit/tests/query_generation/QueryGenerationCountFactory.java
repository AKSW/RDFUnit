package org.aksw.rdfunit.tests.query_generation;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;

/**
 * Factory that returns aggregate count queries
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:07 PM
 * @version $Id: $Id
 */
public class QueryGenerationCountFactory implements QueryGenerationFactory {

    private static final String SELECT_CLAUSE_SIMPLE = " SELECT (count(DISTINCT ?this ) AS ?total ) WHERE ";

    private static final String SELECT_CLAUSE_GROUP_START = " SELECT (count(DISTINCT ?this ) AS ?total ) WHERE {" +
                                                   " SELECT ?this WHERE ";
    private static final String SELECT_CLAUSE_GROUP_END = "}";

    /** {@inheritDoc} */
    @Override
    public String getSparqlQueryAsString(TestCase testCase) {
        return getSparqlQuery(testCase).toString();
    }

    /** {@inheritDoc} */
    @Override
    public Query getSparqlQuery(TestCase testCase) {
        String sparqlQuery = PrefixNSService.getSparqlPrefixDecl() +
                SELECT_CLAUSE_SIMPLE + testCase.getSparqlWhere();

        try {
            Query query = QueryFactory.create(sparqlQuery);
            if (!query.hasGroupBy()) {
                return query;
            }
        } catch (QueryParseException e) {
            throw new IllegalArgumentException("Illegal query: \n" + sparqlQuery, e);
        }


        // When we have HAVING the aggregate is calculated against the HAVING expression
        // This way we enclose the query in a sub-select and calculate the count () correctly
        // See https://issues.apache.org/jira/browse/JENA-766
        Query query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
                SELECT_CLAUSE_GROUP_START
                        + testCase.getSparqlWhere() +
                SELECT_CLAUSE_GROUP_END
        );

        return query;
    }
}
