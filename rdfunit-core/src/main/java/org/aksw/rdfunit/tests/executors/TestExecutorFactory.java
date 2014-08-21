package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.tests.QueryGenerationAskFactory;
import org.aksw.rdfunit.tests.QueryGenerationCountFactory;
import org.aksw.rdfunit.tests.QueryGenerationExtendedSelectFactory;
import org.aksw.rdfunit.tests.QueryGenerationSelectFactory;

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
                return new StatusTestExecutor(new QueryGenerationAskFactory());
            case aggregatedTestCaseResult:
                return new AggregatedTestExecutor(new QueryGenerationCountFactory());
            case rlogTestCaseResult:
                return new RLOGTestExecutor(new QueryGenerationSelectFactory());
            case extendedTestCaseResult:
                return new ExtendedTestExecutor(new QueryGenerationExtendedSelectFactory());
            default:
                return null;
        }
    }
}
