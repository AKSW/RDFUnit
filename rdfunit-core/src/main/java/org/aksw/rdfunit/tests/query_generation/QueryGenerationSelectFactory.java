package org.aksw.rdfunit.tests.query_generation;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;

import static org.aksw.rdfunit.tests.query_generation.QueryGenerationUtils.getPrefixDeclarations;

/**
 * Factory that returns simple select queries
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:02 PM

 */
public class QueryGenerationSelectFactory implements QueryGenerationFactory {

    private static final String SELECT_CLAUSE = " SELECT DISTINCT ?this WHERE ";


    @Override
    public String getSparqlQueryAsString(TestCase testCase) {
        return getPrefixDeclarations(testCase) +
                SELECT_CLAUSE + testCase.getSparqlWhere();
    }


    @Override
    public Query getSparqlQuery(TestCase testCase) {
        String query = this.getSparqlQueryAsString(testCase);
        try {
            return QueryFactory.create(query);
        } catch (QueryParseException e) {
            throw new IllegalArgumentException("Illegal query: \n" + query, e);
        }
    }
}
