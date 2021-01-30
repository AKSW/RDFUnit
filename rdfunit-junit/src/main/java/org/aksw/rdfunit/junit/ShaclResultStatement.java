package org.aksw.rdfunit.junit;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.apache.jena.rdf.model.RDFNode;
import org.junit.runners.model.Statement;

/**
 * @author Michael Leuthold
 */
class ShaclResultStatement extends Statement {

  private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor;
  private final RdfUnitJunitTestCase testCase;

  ShaclResultStatement(RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor,
      RdfUnitJunitTestCase testCase) {
    super();
    this.rdfUnitJunitStatusTestExecutor = rdfUnitJunitStatusTestExecutor;
    this.testCase = testCase;
  }


  @Override
  public void evaluate() {
    final Collection<TestCaseResult> testCaseResults = rdfUnitJunitStatusTestExecutor
        .runTest(testCase);
    final Collection<ShaclLiteTestCaseResult> remainingResults = new ArrayList<>();
    for (TestCaseResult t : testCaseResults) {
      ShaclLiteTestCaseResult r = (ShaclLiteTestCaseResult) t;
      if (!nodeIsPartOfInputModel(r)) {
        continue;
      }
      remainingResults.add(r);
    }
    final StringBuilder b = new StringBuilder();
    b.append(testCase.getTestCase().getResultMessage()).append(":\n");
    for (ShaclLiteTestCaseResult r : remainingResults) {
      b.append('\t').append(r.getFailingNode().toString()).append('\n');
    }
    assertThat(b.toString(), remainingResults.isEmpty());
  }

  private boolean nodeIsPartOfInputModel(ShaclLiteTestCaseResult r) {
    boolean isResourceAsSubject =
        r.getFailingNode().isResource() &&
            testCase.getTestInputModel()
                .contains(r.getFailingNode().asResource(), null, (RDFNode) null);
    boolean asObject = testCase.getTestInputModel().contains(null, null, r.getFailingNode());
    return isResourceAsSubject || asObject;
  }

}
