package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.tests.query_generation.*;

/**
 * Factory methods for Test Executors instantiation
 *
 * @author Dimitris Kontokostas
 * @since 6 /16/14 10:44 AM

 */
public final class TestExecutorFactory {

    private TestExecutorFactory() {
    }

    /**
     * Creates a test executor based on a TestCaseExecutionType
     */
    public static TestExecutor createTestExecutor(TestCaseExecutionType executionType) {
        QueryGenerationFactory qgf = createQueryGeneration(executionType);
        return createTestExecutor(executionType, qgf);
    }

    /**
     * Creates a test executor based on a TestCaseExecutionType.
     * The executor has an internal cache that caches the parsed SPARQL query object
     * this speeds up test execution when the same test suite is called repeatedly on different datasets
     */
    public static TestExecutor createTestExecutorWithCache(TestCaseExecutionType executionType, int cacheSize) {
        QueryGenerationFactory qgf = createQueryGeneration(executionType);
        QueryGenerationFactory cached = new QueryGenerationFactoryCache(qgf, cacheSize);
        return createTestExecutor(executionType, cached);
    }

    private static TestExecutor createTestExecutor(TestCaseExecutionType executionType, QueryGenerationFactory qgf) {
        switch (executionType) {
            case statusTestCaseResult:
                return new StatusTestExecutor(qgf);
            case aggregatedTestCaseResult:
                return new AggregatedTestExecutor(qgf);
            case shaclLiteTestCaseResult:
                return new ShaclSimpleTestExecutor(qgf);
            case shaclTestCaseResult:
                return new ShaclTestExecutor(qgf);
            default:
                throw new IllegalArgumentException("Unknown execution type");
        }
    }

    private static QueryGenerationFactory createQueryGeneration(TestCaseExecutionType executionType) {
        switch (executionType) {
            case statusTestCaseResult:
                return new QueryGenerationAskFactory();
            case aggregatedTestCaseResult:
                return new QueryGenerationCountFactory();
            case shaclLiteTestCaseResult:
                return new QueryGenerationSelectFactory();
            case shaclTestCaseResult:
                return new QueryGenerationExtendedSelectFactory();
            default:
                throw new IllegalArgumentException("Unknown execution type");
        }
    }


}
