package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.Utils.RDFUnitUtils;
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
 * Takes a dataset source and executes the test queries against the endpoint.
 *
 * @author Dimitris Kontokostas
 * @since 9 /30/13 11:11 AM
 */
public abstract class TestExecutor {
    private static Logger log = LoggerFactory.getLogger(TestExecutor.class);
    /**
     * Used in {@code cancel()} to stop the current execution
     */
    private volatile boolean isCanceled = false;

    /**
     * Collection of subscribers in the current test execution
     */
    private final java.util.Collection<TestExecutorMonitor> progressMonitors = new ArrayList<>();

    /**
     * Instantiates a new Test executor.
     */
    public TestExecutor() {

    }

    /**
     * Cancel the current execution. After the current query that is executed, the test execution is halted
     */
    public void cancel() {
        isCanceled = true;
    }

    /**
     * Executes single test.
     *
     * @param source the source
     * @param testCase the test case
     * @return the java . util . collection
     * @throws TestCaseExecutionException the test case execution exception
     */
    abstract protected java.util.Collection<TestCaseResult> executeSingleTest(Source source, TestCase testCase) throws TestCaseExecutionException;


    /**
     * Test execution for a Source againsts a TestSuite
     *
     * @param source the source we want to test
     * @param testSuite the test suite we test the source against
     * @param delay delay between sparql queries
     */
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
            } catch (Exception e) {
                //throw new RuntimeException("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
                log.error("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
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

    /**
     * Add test executor monitor / subscriber.
     *
     * @param monitor the monitor
     */
    public void addTestExecutorMonitor(TestExecutorMonitor monitor) {
        progressMonitors.add(monitor);
    }

    /**
     * Remove a test executor monitor  / subscriber.
     *
     * @param monitor the monitor
     */
    public void removeTestExecutorMonitor(TestExecutorMonitor monitor) {
        progressMonitors.remove(monitor);
    }

}
