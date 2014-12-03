package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.query.Query;

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
    public abstract String getSparqlQueryAsString(TestCase testCase);

    /**
     * Gets a TestCase and returns an appropriate sparql query as Jena Query
     *
     * @param testCase the test case
     * @return the sparql query
     */
    public abstract Query getSparqlQuery(TestCase testCase);
}

