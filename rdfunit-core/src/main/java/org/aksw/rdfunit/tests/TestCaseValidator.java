package org.aksw.rdfunit.tests;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationAskFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationCountFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationExtendedSelectFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 12/17/14 5:07 PM

 */
@Slf4j
public class TestCaseValidator {

    private final TestCase testCase;

    public TestCaseValidator(TestCase testCase) {
        this.testCase = testCase;
    }

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
            log.warn("?this is not included in SELECT for Test: {}", testCase.getTestURI());
        }

        // Message is allowed to exist either in SELECT or as a result annotation
        if (testCase.getResultMessage().trim().isEmpty()) {
            //throw new TestCaseInstantiationException("No test case dcterms:description message included in TestCase: " + testCase.getTestURI());
            log.warn("No test case dcterms:description message included in TestCase: {}", testCase.getTestURI());
        }

        if (testCase.getLogLevel() == null) {
            //throw new TestCaseInstantiationException("No (or malformed) log level included for Test: " + testCase.getTestURI());
            log.warn("No (or malformed) log level included for Test: {}", testCase.getTestURI());
        }
    }



    private void validateSPARQL(String sparql, String type)  {
        try {
            QueryFactory.create(sparql);
        } catch (QueryParseException e) {
            String message = "QueryParseException in " + type + " query (line " + e.getLine() + ", column " + e.getColumn() + " for Test: " + testCase.getTestURI() + "\n" + PrefixNSService.getSparqlPrefixDecl() + sparql;
            //throw new TestCaseInstantiationException(message, e);
            log.warn(message,e);
        }
    }
}
