package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.tests.TestCase;

import java.util.Calendar;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/2/14 3:44 PM
 */
public abstract class TestCaseResult {
    private final TestCase testCase;
    private final XSDDateTime timestamp;

    protected TestCaseResult(TestCase testCase) {
        this.testCase = testCase;
        this.timestamp = new XSDDateTime(Calendar.getInstance());
    }

    public abstract Resource serialize(Model model, String sourceURI);

    public TestCase getTestCase() {
        return testCase;
    }

    public XSDDateTime getTimestamp() {
        return timestamp;
    }
}
