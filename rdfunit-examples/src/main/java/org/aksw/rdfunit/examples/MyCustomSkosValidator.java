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
                        .build()
        );

    }

    public TestExecution validate(Model input) {
        // uses the TestSuite initiated above
        return RDFUnitStaticValidator.validate(input, TestCaseExecutionType.shaclFullTestCaseResult);
    }

    public boolean isValid(Model input) {
        return validate(input).getDatasetOverviewResults().getFailedTests() > 0;
    }

    public long getViolationIntances(Model input) {
        return validate(input).getDatasetOverviewResults().getIndividualErrors();
    }
}
