package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.tests.executors.ShaclSimpleTestExecutor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;

import java.util.Collection;

/**
 *
 * @author Michael Leuthold

 */

final class RdfUnitJunitStatusTestExecutor extends ShaclSimpleTestExecutor {

    public RdfUnitJunitStatusTestExecutor() {
        super(new QueryGenerationSelectFactory());
    }

    protected Collection<TestCaseResult> runTest(RdfUnitJunitTestCase rdfUnitJunitTestCase) {

        rdfUnitJunitTestCase.prepareForExecution();

        return this.executeGenericTest(
                rdfUnitJunitTestCase.getModelSource(),
                rdfUnitJunitTestCase.getTestCase()
        );
    }
}
