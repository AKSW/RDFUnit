package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.vocabulary.SHACL;

import java.util.Set;

public interface TestCaseGroup extends GenericTestCase {

    Set<TestCase> getTestCases();

    SHACL.LogicalConstraint getLogicalOperator();
}
