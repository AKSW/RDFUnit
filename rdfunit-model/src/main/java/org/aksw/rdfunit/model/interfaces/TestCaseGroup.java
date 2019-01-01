package org.aksw.rdfunit.model.interfaces;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.impl.shacl.TestCaseGroupAtomic;
import org.aksw.rdfunit.model.impl.shacl.TestCaseWithTarget;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A wrapper object for a collection of test cases with a logical operator,
 * defining the logical relation between their results
 */
public interface TestCaseGroup extends TargetBasedTestCase {

    /**
     * The test cases which have a logical relation
     */
    Set<TargetBasedTestCase> getTestCases();

    /**
     * the logical operator (default is 'atomic' which means that there is no logical relation between them)
     */
    SHACL.LogicalConstraint getLogicalOperator();

    /**
     * Function for evaluating the results of the contained tests.
     * Will return an empty set of results if the logical condition holds.
     * Will add a summary TestCaseResult if it fails.
     * @param internalResults - the result collection of the internal tests
     */
    Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults);

    /**
     * Will extract all test case uris of the given test case collection
     * - adding the internal test case uri of a TestCaseWithTarget
     * - adding the internal test case uri of a TestCaseGroupAtomic
     * @param testCases - the test cases of which to extract all relevant uris
     * @return - the collection  of extracted test case uris
     */
    static Set<Resource> getTestCaseUris(Set<TargetBasedTestCase> testCases){
        final HashSet<Resource> testCaseUris = Sets.newHashSet();
        // adding all direct uris
        testCases.stream().map(TestCaseGroup::getInternalTestCase).forEach(testCaseUris::addAll);
        return testCaseUris;
    }

    static Set<Resource> getInternalTestCase(GenericTestCase tc){
        ImmutableSet.Builder<Resource> ret = ImmutableSet.builder();
        if(TestCaseWithTarget.class.isAssignableFrom(tc.getClass())){
            ret.addAll(getInternalTestCase(((TestCaseWithTarget) tc).getTestCase()));
        }
        else if(TestCaseGroupAtomic.class.isAssignableFrom(tc.getClass())){
            ret.addAll(getInternalTestCase(((TestCaseGroupAtomic) tc).getTestCases().iterator().next()));
        }
        ret.add(tc.getElement());
        return ret.build();
    }

    static Map<RDFNode, Map<RDFNode, List<TestCaseResult>>> groupInternalResults(Collection<TestCaseResult> internalResults, Set<Resource> allowedTestCaseUris){
        return internalResults.stream()
                .filter(r -> allowedTestCaseUris.contains(r.getTestCaseUri()))
                .filter(r -> ShaclLiteTestCaseResult.class.isAssignableFrom(r.getClass()))
                .map(r -> ((ShaclLiteTestCaseResult) r))
                .collect(Collectors.groupingBy(ShaclLiteTestCaseResult::getFailingNode,
                        Collectors.groupingBy(TestCaseGroup::getValue, Collectors.toList())));
    }

    static RDFNode getValue(ShaclLiteTestCaseResult result){
        if(ShaclTestCaseResult.class.isAssignableFrom(result.getClass())){
            Set<PropertyValuePair> values = ((ShaclTestCaseResult) result).getResultAnnotations().stream().filter(ra -> ra.getProperty().equals(SHACL.value)).collect(Collectors.toSet());
            if(! values.isEmpty()){
                return values.iterator().next().getValues().iterator().next();  // TODO we assume there is just one value ???
            }
        }
        return result.getFailingNode(); // the default case concerns non property nodes which are grouped under the focus node uri
    }
}
