package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.tests.executors.ShaclSimpleTestExecutor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 *
 * @author Michael Leuthold
 * @version $Id: $Id
 */

final class RdfUnitJunitStatusTestExecutor extends ShaclSimpleTestExecutor {

    /**
     * <p>Constructor for RdfUnitJunitStatusTestExecutor.</p>
     */
    public RdfUnitJunitStatusTestExecutor() {
        super(new QueryGenerationSelectFactory());
    }

    protected Collection<TestCaseResult> runTest(RdfUnitJunitTestCase rdfUnitJunitTestCase)
            throws IllegalAccessException, InvocationTargetException {

        try {
            rdfUnitJunitTestCase.prepareForExecution();

            return this.executeSingleTest(
                    rdfUnitJunitTestCase.getModelSource(),
                    rdfUnitJunitTestCase.getTestCase()
            );
        } catch (TestCaseExecutionException e) {
            /// Should never happen (TM)
            throw new IllegalStateException(e);
        }
    }
}
