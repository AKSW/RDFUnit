package org.aksw.rdfunit.model.interfaces;

import com.google.common.collect.Sets;
import org.aksw.rdfunit.model.impl.shacl.TestCaseSingletonGroup;
import org.aksw.rdfunit.model.impl.shacl.TestCaseWithTarget;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A wrapper object for a collection of test cases with a logical operator,
 * defining the logical relation between their results
 */
public interface TestCaseGroup extends GenericTestCase {

    /**
     * The test cases which have a logical relation
     */
    Set<GenericTestCase> getTestCases();

    /**
     * the logical operator (default is 'atomic' which means that there is no logical relation between them)
     */
    SHACL.LogicalConstraint getLogicalOperator();

    /**
     * Function for evaluating the results of the contained tests.
     * Will return an empty set of results if the logical condition holds.
     * Will add a summary TestCaseResult if it fails.
     * @param internalResults - the result collection of the internal tests
     * @return
     */
    Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults);

    /**
     * Will extract all test case uris uf the given test case collection
     * - adding the internal test case uri of a TestCaseWithTarget
     * - adding the internal test case uri of a TestCaseSingletonGroup
     * @param testCases - the test cases of which to extract all relevant uris
     * @return - the collection  of extracted test case uris
     */
    static Set<Resource> getTestCaseUris(Set<GenericTestCase> testCases){
        final HashSet<Resource> testCaseUris = Sets.newHashSet();
        // flattening the internal test case of TestCaseWithTarget
        testCases.stream()
                .filter(x -> x instanceof TestCaseWithTarget)
                .map(tc -> ((TestCaseWithTarget) tc))
                .map(tc -> tc.getTestCase().getElement())
                .forEach(testCaseUris::add);
        // flattening the internal test case of TestCaseSingletonGroup
        testCases.stream()
                .filter(x -> x instanceof TestCaseSingletonGroup)
                .map(tc -> ((TestCaseSingletonGroup) tc))
                .flatMap(tc -> getTestCaseUris(tc.getTestCases()).stream())
                .forEach(testCaseUris::add);
        // adding all direct uris
        testCases.stream()
                .map(GenericTestCase::getElement)
                .forEach(testCaseUris::add);
        return testCaseUris;
    }
}
