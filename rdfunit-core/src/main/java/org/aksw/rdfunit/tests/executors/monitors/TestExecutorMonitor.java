package org.aksw.rdfunit.tests.executors.monitors;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.results.TestCaseResult;


/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/3/14 12:23 PM
 */
public interface TestExecutorMonitor {
    /*
    * Called when testing starts
    * */
    void testingStarted(final Source dataset, final TestSuite testSuite);

    /*
    * Called when a single test starts
    * */
    void singleTestStarted(final TestCase test);

    /*
    * Called when a single test is executed
    * */
    void singleTestExecuted(final TestCase test, final TestCaseResultStatus status, final java.util.Collection<TestCaseResult> results);

    /*
    * Called when testing ends
    * */
    void testingFinished();
}
