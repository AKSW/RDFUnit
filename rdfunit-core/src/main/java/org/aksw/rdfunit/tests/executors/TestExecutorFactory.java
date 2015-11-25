package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationAskFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationCountFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationExtendedSelectFactory;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;

/**
 * Factory methods for Test Executors instantiation
 *
 * @author Dimitris Kontokostas
 * @since 6 /16/14 10:44 AM
 * @version $Id: $Id
 */
public final class TestExecutorFactory {

    private TestExecutorFactory() {
    }

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
            case shaclSimpleTestCaseResult:
                return new ShaclSimpleTestExecutor(new QueryGenerationSelectFactory());
            case shaclFullTestCaseResult:
                return new ShaclFullTestExecutor(new QueryGenerationExtendedSelectFactory());
            case rlogTestCaseResult:
                return new RLOGTestExecutor(new QueryGenerationSelectFactory());
            case extendedTestCaseResult:
                return new ExtendedTestExecutor(new QueryGenerationExtendedSelectFactory());
            default:
                return null;
        }
    }
}
