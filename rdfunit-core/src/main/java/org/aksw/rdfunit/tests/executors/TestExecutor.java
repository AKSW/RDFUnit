package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.QueryGenerationFactory;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.results.RLOGTestCaseResult;
import org.aksw.rdfunit.tests.results.StatusTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Takes a dataset source and executes the test queries against the endpoint.
 *
 * @author Dimitris Kontokostas
 * @since 9 /30/13 11:11 AM
 */
public abstract class TestExecutor {
    private static final Logger log = LoggerFactory.getLogger(TestExecutor.class);
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
     * @param source   the source
     * @param testCase the test case
     * @return the java . util . collection
     * @throws TestCaseExecutionException the test case execution exception
     */
    abstract protected Collection<TestCaseResult> executeSingleTest(Source source, TestCase testCase) throws TestCaseExecutionException;


    /**
     * Test execution for a Source against a TestSuite
     *
     * @param source    the source we want to test
     * @param testSuite the test suite we test the source against
     * @param delay     delay between sparql queries
     * @return true if all TC executed successfully, false otherwise
     */
    public boolean execute(Source source, TestSuite testSuite, int delay) {
        // used to hold the whole status of the execution
        boolean success = true;

        // reset to false for this execution
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

            Collection<TestCaseResult> results = new ArrayList<>();
            TestCaseResultStatus status;

            // Test case execution and debug logging
            long executionTimeStartInMS = System.currentTimeMillis();
            log.debug(testCase.getAbrTestURI() + " : started execution");

            try {
                results = executeSingleTest(source, testCase);
            } catch (TestCaseExecutionException e) {
                status = e.getStatus();
            } catch (RuntimeException e) {
                //Thread.sleep(...) when VOS (SPARQL Endpoint crashes we can put a sleep here until it restarts and comment the throw
                throw new RuntimeException("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
            } catch (Exception e) {
                log.error("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
                status = TestCaseResultStatus.Error;
            }

            long executionTimeEndInMS = System.currentTimeMillis();
            log.debug(testCase.getAbrTestURI() + " : execution completed in " + (executionTimeEndInMS - executionTimeStartInMS) + "ms");

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

            // If at least one TC fails the whole TestSuite fails
            if (status != TestCaseResultStatus.Success) {
                success = false;
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

        } // End of TC execution for loop

        /*notify end of testing */
        for (TestExecutorMonitor monitor : progressMonitors) {
            monitor.testingFinished();
        }

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
