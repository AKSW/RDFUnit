package org.aksw.rdfunit.model.interfaces;

import com.google.common.collect.Sets;
import org.aksw.rdfunit.model.impl.shacl.TestCaseWithTarget;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface TestCaseGroup extends GenericTestCase {

    Set<GenericTestCase> getTestCases();

    SHACL.LogicalConstraint getLogicalOperator();

    Collection<TestCaseResult> evaluateInternalResults(Collection<TestCaseResult> internalResults);

    static Set<Resource> getTestCaseUris(Set<GenericTestCase> testCases){

        final HashSet<Resource> testCaseUris = Sets.newHashSet();
        testCases.stream()
                .filter(x -> x instanceof TestCaseWithTarget)
                .map(tc -> ((TestCaseWithTarget) tc))
                .map(tc -> tc.getTestCase().getElement())
                .forEach(testCaseUris::add);
        testCases.stream()
                .map(GenericTestCase::getElement)
                .forEach(testCaseUris::add);
        return testCaseUris;
    }
}
