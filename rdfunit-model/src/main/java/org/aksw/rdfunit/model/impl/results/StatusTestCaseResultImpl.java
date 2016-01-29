package org.aksw.rdfunit.model.impl.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;


/**
 * @author Dimitris Kontokostas
 * @since 1 /6/14 3:26 PM
 * @version $Id: $Id
 */
public class StatusTestCaseResultImpl extends BaseTestCaseResultImpl implements StatusTestCaseResult {
    private final TestCaseResultStatus status;

    public StatusTestCaseResultImpl(TestCase testCase, TestCaseResultStatus status) {
        this(testCase.getTestURI(), testCase.getLogLevel(), testCase.getResultMessage(), status);
    }

    public StatusTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, TestCaseResultStatus status) {
        super(testCaseUri, severity, message);
        this.status = status;
    }

    public StatusTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, TestCaseResultStatus status) {
        super(element, testCaseUri, severity, message, timestamp);
        this.status = status;
    }

    public TestCaseResultStatus getStatus() {
        return status;
    }
}
