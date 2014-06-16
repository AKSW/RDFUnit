package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.results.RLOGTestCaseResult;
import org.aksw.rdfunit.tests.results.StatusTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Takes a dataset source and executes the test queries against the endpoint
 * Description
 * Created: 9/30/13 11:11 AM
 */
public abstract class TestExecutor {
    private static Logger log = LoggerFactory.getLogger(TestExecutor.class);
    private boolean isCanceled = false;

    private final java.util.Collection<TestExecutorMonitor> progressMonitors = new ArrayList<>();

    public TestExecutor() {

    }

    public void cancel() {
        isCanceled = true;
    }

    abstract protected java.util.Collection<TestCaseResult> executeSingleTest(Source source, TestCase testCase) throws TestCaseExecutionException;

    public static TestExecutor initExecutorFactory(TestCaseExecutionType executionType) {
        switch (executionType) {
            case statusTestCaseResult:
                return new StatusTestExecutor();
            case aggregatedTestCaseResult:
                return new AggregatedTestExecutor();
            case rlogTestCaseResult:
                return new RLOGTestExecutor();
            case extendedTestCaseResult:
                return new ExtendedTestExecutor();
            default:
                return null;
        }
    }

    public void execute(Source source, TestSuite testSuite, int delay) {
        isCanceled = false;

        /*notify start of testing */
        for (TestExecutorMonitor monitor : progressMonitors) {
            monitor.testingStarted(source, testSuite);
        }

        for (TestCase testCase : testSuite.getTestCases()) {
            if (isCanceled) {
                break;
            }

            /*notify start of single test */
            for (TestExecutorMonitor monitor : progressMonitors) {
                monitor.singleTestStarted(testCase);
            }

            java.util.Collection<TestCaseResult> results = new ArrayList<>();
            TestCaseResultStatus status;

            try {
                results = executeSingleTest(source, testCase);
            } catch (TestCaseExecutionException e) {
                status = e.getStatus();
            }

            if (results.size() == 0) {
                status = TestCaseResultStatus.Success;
            } else if (results.size() > 1) {
                status = TestCaseResultStatus.Fail;
            } else {
                status = TestCaseResultStatus.Error; // Default
                TestCaseResult r = RDFUnitUtils.getFirstItemInCollection(results);
                if (r instanceof StatusTestCaseResult) {
                    status = ((StatusTestCaseResult) r).getStatus();
                } else {
                    if (r instanceof RLOGTestCaseResult) {
                        status = TestCaseResultStatus.Fail;
                    }
                }
            }

            /*notify end of single test */
            for (TestExecutorMonitor monitor : progressMonitors) {
                monitor.singleTestExecuted(testCase, status, results);
            }

            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }

        /*notify end of testing */
        for (TestExecutorMonitor monitor : progressMonitors) {
            monitor.testingFinished();
        }
    }

    public void addTestExecutorMonitor(TestExecutorMonitor monitor) {
        progressMonitors.add(monitor);
    }

    public void removeTestExecutorMonitor(TestExecutorMonitor monitor) {
        progressMonitors.remove(monitor);
    }

}
