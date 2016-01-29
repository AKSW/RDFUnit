package org.aksw.rdfunit.tests.query_generation;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.query.Query;

/**
 * Abstract Factory that takes a test case and transforms it to a valid SPARQL QUery
 *
 * @author Dimitris Kontokostas
 * @since 7 /25/14 9:57 PM
 * @version $Id: $Id
 */
public interface QueryGenerationFactory {

    /**
     * Gets a TestCase and returns an appropriate sparql query as String
     *
     * @param testCase the test case
     * @return the sparql query as string
     */
    String getSparqlQueryAsString(TestCase testCase);

    /**
     * Gets a TestCase and returns an appropriate sparql query as Jena Query
     *
     * @param testCase the test case
     * @return the sparql query
     */
    Query getSparqlQuery(TestCase testCase);
}

