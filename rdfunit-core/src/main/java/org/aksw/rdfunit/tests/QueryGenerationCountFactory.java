package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Factory that returns aggregate count queries
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:07 PM
 */
public class QueryGenerationCountFactory implements QueryGenerationFactory {
    @Override
    public String getSparqlQueryAsString(TestCase testCase) {
        return getSparqlQuery(testCase).toString();
    }

    @Override
    public Query getSparqlQuery(TestCase testCase) {
        Query query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
                " SELECT (count(DISTINCT ?resource ) AS ?total ) WHERE " + testCase.getSparqlWhere()
        );
        if (!query.hasHaving()) {
            return query;
        }

        // When we have HAVING the aggregate is calculated against the HAVING expression
        // This way we enclose the query in a sub-select and calculate the count () correctly
        // See https://issues.apache.org/jira/browse/JENA-766
        query = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() +
                " SELECT (count(DISTINCT ?resource ) AS ?total ) WHERE {" +
                " SELECT ?resource WHERE "
                + testCase.getSparqlWhere() +
                "}"
        );

        return query;
    }
}
