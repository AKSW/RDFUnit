package org.aksw.databugger.tests.executors;

import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.TestCase;
import org.aksw.databugger.tests.results.TestCaseResult;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/3/14 12:23 PM
 */
public interface TestExecutorMonitor {
    /*
    * Called when testing starts
    * */
    void testingStarted(final Source dataset, final long numberOfTests);

    /*
    * Called when a single test starts
    * */
    void singleTestStarted(final TestCase test);

    /*
    * Called when a single test is executed
    * */
    void singleTestExecuted(final TestCase test, final List<TestCaseResult> results);

    /*
    * Called when testing ends
    * */
    void testingFinished();
}
