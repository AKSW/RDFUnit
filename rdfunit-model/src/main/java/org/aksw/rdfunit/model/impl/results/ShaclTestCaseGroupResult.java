package org.aksw.rdfunit.model.impl.results;

import java.util.List;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class ShaclTestCaseGroupResult extends ShaclLiteTestCaseResultImpl {

  private List<TestCaseResult> internalResults;

  public ShaclTestCaseGroupResult(Resource testCaseUri, RLOGLevel severity, String message,
      RDFNode focusNode, List<TestCaseResult> internalResults) {
    super(testCaseUri, severity, message, focusNode);
    this.internalResults = internalResults;
  }

  public List<TestCaseResult> getInternalResults() {
    return internalResults;
  }
}
