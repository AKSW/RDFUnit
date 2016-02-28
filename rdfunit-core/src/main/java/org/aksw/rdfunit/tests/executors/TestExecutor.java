package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.SimpleShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Takes a dataset source and executes the test queries against the endpoint.
 *
 * @author Dimitris Kontokostas
 * @since 9 /30/13 11:11 AM
 * @version $Id: $Id
 */
public abstract class TestExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestExecutor.class);
    /**
     * Used in {@code cancel()} to stop the current execution
     */
    private volatile boolean isCanceled = false;

    /**
     * Collection of subscribers in the current test execution
     */
    private final Collection<TestExecutorMonitor> progressMonitors = new ArrayList<>();

    /**
     * Used to transform TestCases to SPARQL Queries
     */
    protected final QueryGenerationFactory queryGenerationFactory;

    /**
     * Instantiates a new Test executor.
     *
     * @param queryGenerationFactory a {@link org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory} object.
     */
    public TestExecutor(QueryGenerationFactory queryGenerationFactory) {
        this.queryGenerationFactory = queryGenerationFactory;
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
     * @param testSource   the source
     * @param testCase the test case
     * @return the java . util . collection
     * @throws org.aksw.rdfunit.exceptions.TestCaseExecutionException the test case execution exception
     */
    protected abstract Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase) throws TestCaseExecutionException;


    /**
     * Test execution for a Source against a TestSuite
     *
     * @param testSource    the source we want to test
     * @param testSuite the test suite we test the source against
     * @return true if all TC executed successfully, false otherwise
     */
    public boolean execute(TestSource testSource, TestSuite testSuite) {
        // used to hold the whole status of the execution
        boolean success = true;

        // reset to false for this execution
        isCanceled = false;

        /*notify start of testing */
        for (TestExecutorMonitor monitor : progressMonitors) {
            monitor.testingStarted(testSource, testSuite);
        }

        for (TestCase testCase : testSuite.getTestCases()) {
            if (isCanceled) {
                break;
            }

            /*notify start of single test */
            for (TestExecutorMonitor monitor : progressMonitors) {
                monitor.singleTestStarted(testCase);
            }

            Collection<TestCaseResult> results = new ArrayList<>();
            TestCaseResultStatus status;

            // Test case execution and debug logging
            long executionTimeStartInMS = System.currentTimeMillis();
            LOGGER.debug("{} : started execution", testCase.getAbrTestURI());

            try {
                results = executeSingleTest(testSource, testCase);
            } catch (TestCaseExecutionException e) {
                status = e.getStatus();
            } catch (RuntimeException e) {
                //try {
                    //Thread.sleep(40000);// when VOS (SPARQL Endpoint crashes we can put a sleep here until it restarts and comment the throw
                //} catch (InterruptedException e1) {
                //    e1.printStackTrace();
                //}
                LOGGER.error("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
                //throw new RuntimeException("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
            } catch (Exception e) {
                LOGGER.error("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
                status = TestCaseResultStatus.Error;
            }

            long executionTimeEndInMS = System.currentTimeMillis();
            LOGGER.debug("{} : execution completed in {}ms", testCase.getAbrTestURI(), (executionTimeEndInMS - executionTimeStartInMS));

            if (results.isEmpty()) {
                status = TestCaseResultStatus.Success;
            } else if (results.size() > 1) {
                status = TestCaseResultStatus.Fail;
            } else {
                status = TestCaseResultStatus.Error; // Default
                TestCaseResult r = RDFUnitUtils.getFirstItemInCollection(results);
                if (r instanceof StatusTestCaseResult) {
                    status = ((StatusTestCaseResult) r).getStatus();
                } else {
                    if (r instanceof RLOGTestCaseResult || r instanceof SimpleShaclTestCaseResult) {
                        status = TestCaseResultStatus.Fail;
                    }
                }
            }

            // If at least one TC fails the whole TestSuite fails
            if (status != TestCaseResultStatus.Success) {
                success = false;
            }


            /*notify end of single test */
            for (TestExecutorMonitor monitor : progressMonitors) {
                monitor.singleTestExecuted(testCase, status, results);
            }

        } // End of TC execution for loop

        /*notify end of testing */
        progressMonitors.forEach(TestExecutorMonitor::testingFinished);

        return success;
    }

    /**
     * Add test executor monitor / subscriber.
     *
     * @param monitor the monitor
     */
    public void addTestExecutorMonitor(TestExecutorMonitor monitor) {

        if (!progressMonitors.contains(monitor)) {
            progressMonitors.add(monitor);
        }
    }

    /**
     * Remove a test executor monitor  / subscriber.
     *
     * @param monitor the monitor
     */
    public void removeTestExecutorMonitor(TestExecutorMonitor monitor) {
        progressMonitors.remove(monitor);
    }

    /**
     * Clears all test executor monitors.
     */
    public void clearTestExecutorMonitor() {
        progressMonitors.clear();
    }

}
