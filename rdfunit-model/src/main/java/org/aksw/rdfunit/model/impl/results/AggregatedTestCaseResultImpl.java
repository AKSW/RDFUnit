package org.aksw.rdfunit.model.impl.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.AggregatedTestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

/**
 * The type Aggregated test case result.
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1 /2/14 3:44 PM
 * @version $Id: $Id
 */
public class AggregatedTestCaseResultImpl extends StatusTestCaseResultImpl implements AggregatedTestCaseResult {
    private final long errorCount;
    private final long prevalenceCount;

    /**
     * Instantiates a new Aggregated test case result.
     *
     * @param testCase        the test case
     * @param errorCount      the error count
     * @param prevalenceCount the prevalence count
     */
    public AggregatedTestCaseResultImpl(TestCase testCase, long errorCount, long prevalenceCount) {
        this(testCase, TestCaseResultStatus.resolve(errorCount), errorCount, prevalenceCount);
    }

    /**
     * Instantiates a new Aggregated test case result.
     *
     * @param testCase        the test case
     * @param status          the status
     * @param errorCount      the error count
     * @param prevalenceCount the prevalence count
     */
    public AggregatedTestCaseResultImpl(TestCase testCase, TestCaseResultStatus status, long errorCount, long prevalenceCount) {
        this(testCase.getTestURI(), testCase.getLogLevel(), testCase.getResultMessage(), status, errorCount, prevalenceCount);
    }

    public AggregatedTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, TestCaseResultStatus status, long errorCount, long prevalenceCount) {
        super(testCaseUri, severity, message, status);
        this.errorCount = errorCount;
        this.prevalenceCount = prevalenceCount;
    }

    public AggregatedTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, TestCaseResultStatus status, long errorCount, long prevalenceCount) {
        super(element, testCaseUri, severity, message, timestamp, status);
        this.errorCount = errorCount;
        this.prevalenceCount = prevalenceCount;
    }

    /**
     * Gets error count.
     *
     * @return the error count
     */
    public long getErrorCount() {
        return errorCount;
    }

    /**
     * Gets prevalence count.
     *
     * @return the prevalence count
     */
    public Optional<Long> getPrevalenceCount() {
        if (prevalenceCount>0) {
            return Optional.of(prevalenceCount);
        }
        else {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {

        return "Errors: " + errorCount + " / Prevalence: " + prevalenceCount + ". Test: " + getMessage();
    }
}
