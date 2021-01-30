package org.aksw.rdfunit.junit;

import java.util.Collection;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.tests.executors.ShaclSimpleTestExecutor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;

/**
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
