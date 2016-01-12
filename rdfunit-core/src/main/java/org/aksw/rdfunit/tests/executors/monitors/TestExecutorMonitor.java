package org.aksw.rdfunit.tests.executors.monitors;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;

import java.util.Collection;


/**
 * interface for monitoring a TestExecutor
 * .
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1 /3/14 12:23 PM
 * @version $Id: $Id
 */
public interface TestExecutorMonitor {
    /**
     * Called when testing starts
     *
     *  @param testSource   the dataset
     * @param testSuite the test suite
     */
    void testingStarted(final TestSource testSource, final TestSuite testSuite);

    /**
     * Called when a single test starts
     *
     * @param test the test
     */
    void singleTestStarted(final TestCase test);

    /**
     * Called when a single test is executed
     *
     * @param test    the test
     * @param status  the status
     * @param results the results
     */
    void singleTestExecuted(final TestCase test, final TestCaseResultStatus status, final Collection<TestCaseResult> results);

    /**
     * Called when testing ends
     */
    void testingFinished();
}
