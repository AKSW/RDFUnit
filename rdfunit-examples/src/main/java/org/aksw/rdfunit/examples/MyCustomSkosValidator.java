package org.aksw.rdfunit.examples;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.apache.jena.rdf.model.Model;

/**
 * @author Dimitris Kontokostas
 * @since 26/2/2016 11:04 πμ
 */
public class MyCustomSkosValidator {

    static {
        //Generate a static test suite that we will use in our application
        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addSchemaURI("skos", "http://www.w3.org/2004/02/skos/core#")
                        // add other here as well
                        //.addSchemaURI("local-owl", "/home/foo/bar-owl.ttl")
                        // e.g. shacl rules
                        //.addSchemaURI("manual-shacl", "http://example.com/skos-shacl-rules#")
                        //.addSchemaURI("local-shacl", "/home/foo/bar-shacl.ttl")
                        .build()
        );

    }

    public TestExecution validate(Model input) throws Exception {
        // uses the TestSuite initiated above
        return RDFUnitStaticValidator.validate(input, TestCaseExecutionType.shaclTestCaseResult);
    }

    public boolean isValid(Model input) throws Exception {
        return validate(input).getDatasetOverviewResults().getFailedTests() == 0;
    }

    public long getViolationIntances(Model input) throws Exception {
        return validate(input).getDatasetOverviewResults().getIndividualErrors();
    }
}
