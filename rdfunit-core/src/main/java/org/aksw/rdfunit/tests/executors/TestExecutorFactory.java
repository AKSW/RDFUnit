package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.TestCaseExecutionType;

/**
 * Factory methods for Test Executors instantiation
 *
 * @author Dimitris Kontokostas
 * @since 6 /16/14 10:44 AM
 */
public final class TestExecutorFactory {

    private TestExecutorFactory() {}

    /**
     * Creates a test executor based on a TestCaseExecutionType
     *
     * @param executionType the execution type
     * @return the test executor
     */
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
