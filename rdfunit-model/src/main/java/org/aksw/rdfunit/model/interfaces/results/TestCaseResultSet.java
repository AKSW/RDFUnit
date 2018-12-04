package org.aksw.rdfunit.model.interfaces.results;

import org.aksw.rdfunit.vocabulary.SHACL;

import java.util.Set;

public interface TestCaseResultSet {

    SHACL.LogicalConstraint getLogicalOperator();
}
