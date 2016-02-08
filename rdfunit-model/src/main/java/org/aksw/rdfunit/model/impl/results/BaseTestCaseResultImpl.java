package org.aksw.rdfunit.model.impl.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Calendar;

/**
 * An abstract Test Case Result.
 *
 * @author Dimitris Kontokostas
 * @since 1 /2/14 3:44 PM
 * @version $Id: $Id
 */
class BaseTestCaseResultImpl implements TestCaseResult {
    private final Resource element;
    private final String testCaseUri;
    private final RLOGLevel severity;
    private final String message;
    private final XSDDateTime timestamp;

    /**
     * Constructor
     *
     * @param testCase the test case this result is related with
     */
    protected BaseTestCaseResultImpl(TestCase testCase) {
        this(testCase.getTestURI(), testCase.getLogLevel(), testCase.getResultMessage());
    }

    protected BaseTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message) {
        this(ResourceFactory.createResource(), testCaseUri, severity, message, new XSDDateTime(Calendar.getInstance()));
    }

    protected BaseTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp) {
        this.element = element;
        this.testCaseUri = testCaseUri;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Resource getElement() {
        return element;
    }

    public String getTestCaseUri() {
        return testCaseUri;
    }

    public XSDDateTime getTimestamp() {
        return timestamp;
    }

    public RLOGLevel getSeverity() {
        return severity;
    }

    public String  getMessage() {
        return message;
    }
}
