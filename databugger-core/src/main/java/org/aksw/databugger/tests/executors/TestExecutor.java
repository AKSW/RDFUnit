package org.aksw.databugger.tests.executors;

import org.aksw.databugger.enums.TestCaseExecutionType;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.TestCase;
import org.aksw.databugger.tests.TestSuite;
import org.aksw.databugger.tests.results.TestCaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes a dataset source and executes the test queries against the endpoint
 * Description
 * Created: 9/30/13 11:11 AM
 */
public abstract class TestExecutor {
    private static Logger log = LoggerFactory.getLogger(TestExecutor.class);
    private boolean isCanceled = false;

    private final List<TestExecutorMonitor> progressMonitors = new ArrayList<TestExecutorMonitor>();

    public TestExecutor() {

    }

    public void cancel() {
        isCanceled = true;
    }

    abstract protected List<TestCaseResult> executeSingleTest(Source source, TestCase testCase);

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
        }
        return null;
    }

    public void execute(Source source, TestSuite testSuite, int delay) {
        isCanceled = false;

        /*notify start of testing */
        for (TestExecutorMonitor monitor : progressMonitors) {
            monitor.testingStarted(source, testSuite.size());
        }

        for (TestCase testCase : testSuite.getTestCases()) {
            if (isCanceled) {
                break;
            }

            /*notify start of single test */
            for (TestExecutorMonitor monitor : progressMonitors) {
                monitor.singleTestStarted(testCase);
            }

            List<TestCaseResult> results = executeSingleTest(source, testCase);

            /*notify end of single test */
            for (TestExecutorMonitor monitor : progressMonitors) {
                monitor.singleTestExecuted(testCase, results);
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
