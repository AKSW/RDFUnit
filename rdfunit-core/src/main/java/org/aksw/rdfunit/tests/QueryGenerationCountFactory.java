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
        return PrefixNSService.getSparqlPrefixDecl() +
                " SELECT (count(DISTINCT ?resource ) AS ?total ) WHERE " + testCase.getSparqlWhere();
    }

    @Override
    public Query getSparqlQuery(TestCase testCase) {
        return QueryFactory.create(getSparqlQueryAsString(testCase));
    }
}
