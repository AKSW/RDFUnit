package org.aksw.rdfunit.model.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;

/**
 * A wrapper object for a collection of test cases with a logical operator, defining the logical
 * relation between their results
 */
public interface TestCaseGroup extends TargetBasedTestCase {

  /**
   * Helper function to create a double grouping of internal test results (for a given test case
   * group) by focus node and triple value. The resulting groups should cover all results of the
   * internal test cases of a test group for a single triple (value) Example: if there are multiple
   * instances of a given property in a focus node with invalid values, without the grouping by
   * value we could not distinguish which TestCaseResult referred to which property instance.
   * TestResults without the value annotation (e.g. results for node shapes) are grouped under the
   * focus node iri. NOTE: we assume that a given test case covers a single property path, thereby
   * ignoring the dimension of a property path
   *
   * @param internalResults - all TestResults stemming from all internal tests of a TestGroup
   */
  static Map<RDFNode, Map<RDFNode, List<TestCaseResult>>> groupInternalResults(
      Collection<TestCaseResult> internalResults) {
    return internalResults.stream()
        .filter(r -> ShaclLiteTestCaseResult.class.isAssignableFrom(r.getClass()))
        .map(r -> ((ShaclLiteTestCaseResult) r))
        .collect(Collectors.groupingBy(ShaclLiteTestCaseResult::getFailingNode,
            Collectors.groupingBy(TestCaseGroup::getValue, Collectors.toList())));
  }

  static RDFNode getValue(ShaclLiteTestCaseResult result) {
    if (ShaclTestCaseResult.class.isAssignableFrom(result.getClass())) {
      Set<PropertyValuePair> values = ((ShaclTestCaseResult) result).getResultAnnotations().stream()
          .filter(ra -> ra.getProperty().equals(SHACL.value)).collect(Collectors.toSet());
      if (!values.isEmpty()) {
        return values.iterator().next().getValues().iterator().next();
      }
    }
    return result
        .getFailingNode(); // the default case concerns non property nodes which are grouped under the focus node iri
  }

  /**
   * The test cases which have a logical relation
   */
  Set<TargetBasedTestCase> getTestCases();

  /**
   * the logical operator (default is 'atomic' which means that there is no logical relation between
   * them)
   */
  SHACL.LogicalConstraint getLogicalOperator();

  /**
   * Function for evaluating the results of the contained tests. Will return an empty set of results
   * if the logical condition holds. Will add a summary TestCaseResult if it fails.
   *
   * @param internalResults - the result collection of the internal tests
   */
  Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults);
}
