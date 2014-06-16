package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.TestCaseExecutionType;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/16/14 10:44 AM
 */
public final class TestExecutorFactory {

    public static TestExecutor createTestExecutor(TestCaseExecutionType executionType) {
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
}
