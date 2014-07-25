package org.aksw.rdfunit.tests.executors.monitors;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.util.Collection;


/**
 * interface for monitoring a TestExecutor
 * .
 * @author Dimitris Kontokostas
 *         Description
 * @since 1 /3/14 12:23 PM
 */
public interface TestExecutorMonitor {
    /**
     * Called when testing starts
     *
     * @param dataset the dataset
     * @param testSuite the test suite
     */
    void testingStarted(final Source dataset, final TestSuite testSuite);

    /**
     * Called when a single test starts
     *
     * @param test the test
     */
    void singleTestStarted(final TestCase test);

    /**
     * Called when a single test is executed
     *
     * @param test the test
     * @param status the status
     * @param results the results
     */
    void singleTestExecuted(final TestCase test, final TestCaseResultStatus status, final Collection<TestCaseResult> results);

    /**
     * Called when testing ends
     */
    void testingFinished();
}
