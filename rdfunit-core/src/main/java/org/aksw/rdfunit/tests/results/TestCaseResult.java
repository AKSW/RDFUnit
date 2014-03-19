package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.tests.TestCase;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/2/14 3:44 PM
 */
public abstract class TestCaseResult {
    private final TestCase testCase;

    protected TestCaseResult(TestCase testCase) {
        this.testCase = testCase;
    }

    public abstract Resource serialize(Model model, String sourceURI);

    public TestCase getTestCase() {
        return testCase;
    }
}
