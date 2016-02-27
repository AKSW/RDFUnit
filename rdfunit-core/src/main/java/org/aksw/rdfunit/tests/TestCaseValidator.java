package org.aksw.rdfunit.tests;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationAskFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationCountFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationExtendedSelectFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12/17/14 5:07 PM
 * @version $Id: $Id
 */
public class TestCaseValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseValidator.class);


    private final TestCase testCase;

    /**
     * <p>Constructor for TestCaseValidator.</p>
     *
     * @param testCase a {@link org.aksw.rdfunit.model.interfaces.TestCase} object.
     */
    public TestCaseValidator(TestCase testCase) {
        this.testCase = testCase;
    }

    /**
     * <p>validateQueries.</p>
     *
     */
    public void validate() {
        // TODO move this in a separate class

        validateSPARQL(new QueryGenerationSelectFactory().getSparqlQueryAsString(testCase), "SPARQL");
        validateSPARQL(new QueryGenerationExtendedSelectFactory().getSparqlQueryAsString(testCase), "SPARQL Extended");
        validateSPARQL(new QueryGenerationCountFactory().getSparqlQueryAsString(testCase), "SPARQL Count");
        validateSPARQL(new QueryGenerationAskFactory().getSparqlQueryAsString(testCase), "ASK");
        if (!testCase.getSparqlPrevalence().trim().isEmpty()) { // Prevalence in not always defined
            validateSPARQL(PrefixNSService.getSparqlPrefixDecl() + testCase.getSparqlPrevalence(), "prevalence");
        }

        Collection<String> vars = new QueryGenerationSelectFactory().getSparqlQuery(testCase).getResultVars();
        // check for Resource & message
        boolean hasResource = false;
        for (String v : vars) {
            if ("this".equals(v)) {
                hasResource = true;
            }

        }
        if (!hasResource) {
           // throw new TestCaseInstantiationException("?this is not included in SELECT for Test: " + testCase.getTestURI());
            LOGGER.warn("?this is not included in SELECT for Test: {}", testCase.getTestURI());
        }

        // Message is allowed to exist either in SELECT or as a result annotation
        if (testCase.getResultMessage().trim().isEmpty()) {
            //throw new TestCaseInstantiationException("No test case dcterms:description message included in TestCase: " + testCase.getTestURI());
            LOGGER.warn("No test case dcterms:description message included in TestCase: {}", testCase.getTestURI());
        }

        if (testCase.getLogLevel() == null) {
            //throw new TestCaseInstantiationException("No (or malformed) log level included for Test: " + testCase.getTestURI());
            LOGGER.warn("No (or malformed) log level included for Test: {}", testCase.getTestURI());
        }
    }



    private void validateSPARQL(String sparql, String type)  {
        try {
            QueryFactory.create(sparql);
        } catch (QueryParseException e) {
            String message = "QueryParseException in " + type + " query (line " + e.getLine() + ", column " + e.getColumn() + " for Test: " + testCase.getTestURI() + "\n" + PrefixNSService.getSparqlPrefixDecl() + sparql;
            //throw new TestCaseInstantiationException(message, e);
            LOGGER.warn(message,e);
        }
    }
}
