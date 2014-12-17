package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.services.PrefixNSService;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12/17/14 5:07 PM
 */
public class TestCaseValidator {

    private final TestCase testCase;

    public TestCaseValidator(TestCase testCase) {
        this.testCase = testCase;
    }

    /**
     * <p>validateQueries.</p>
     *
     * @throws org.aksw.rdfunit.exceptions.TestCaseInstantiationException if any.
     */
    public void validate() throws TestCaseInstantiationException {
        // TODO move this in a separate class

        validateSPARQL(new QueryGenerationSelectFactory().getSparqlQueryAsString(testCase), "SPARQL");
        validateSPARQL(new QueryGenerationExtendedSelectFactory().getSparqlQueryAsString(testCase), "SPARQL Extended");
        validateSPARQL(new QueryGenerationCountFactory().getSparqlQueryAsString(testCase), "SPARQL Count");
        validateSPARQL(new QueryGenerationAskFactory().getSparqlQueryAsString(testCase), "ASK");
        if (!testCase.getSparqlPrevalence().trim().equals("")) { // Prevalence in not always defined
            validateSPARQL(PrefixNSService.getSparqlPrefixDecl() + testCase.getSparqlPrevalence(), "prevalence");
        }

        Collection<String> vars = new QueryGenerationSelectFactory().getSparqlQuery(testCase).getResultVars();
        // check for Resource & message
        boolean hasResource = false;
        for (String v : vars) {
            if (v.equals("resource")) {
                hasResource = true;
            }

        }
        if (!hasResource) {
            throw new TestCaseInstantiationException("?resource is not included in SELECT for Test: " + testCase.getTestURI());
        }

        // Message is allowed to exist either in SELECT or as a result annotation
        if (testCase.getResultMessage().equals("")) {
            throw new TestCaseInstantiationException("No test case dcterms:description message included in TestCase: " + testCase.getTestURI());
        }

        if (testCase.getLogLevel() == null) {
            throw new TestCaseInstantiationException("No (or malformed) log level included for Test: " + testCase.getTestURI());
        }
    }



    private void validateSPARQL(String sparql, String type) throws TestCaseInstantiationException {
        try {
            QueryFactory.create(sparql);
        } catch (QueryParseException e) {
            String message = "QueryParseException in " + type + " query (line " + e.getLine() + ", column " + e.getColumn() + " for Test: " + testCase.getTestURI() + "\n" + PrefixNSService.getSparqlPrefixDecl() + sparql;
            throw new TestCaseInstantiationException(message, e);
        }
    }
}
