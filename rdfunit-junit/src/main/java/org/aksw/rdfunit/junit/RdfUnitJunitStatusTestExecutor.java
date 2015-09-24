package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.tests.executors.RLOGTestExecutor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

final class RdfUnitJunitStatusTestExecutor extends RLOGTestExecutor {

    /**
     * <p>Constructor for RdfUnitJunitStatusTestExecutor.</p>
     */
    public RdfUnitJunitStatusTestExecutor() {
        super(new QueryGenerationSelectFactory());
    }

    Collection<TestCaseResult> runTest(RdfUnitJunitTestCase rdfUnitJunitTestCase)
            throws IllegalAccessException, InvocationTargetException {

        try {
            return this.executeSingleTest(
                    rdfUnitJunitTestCase.getModelSource(),
                    rdfUnitJunitTestCase.getTestCase()
            );
        } catch (TestCaseExecutionException e) {
            /// Should never happen (TM)
            throw new RuntimeException(e);
        }
    }
}
