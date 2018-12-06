package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.vocabulary.SHACL;

import java.util.Set;

public interface ShapeTargetSet {

    Set<ShapeTarget> getTargets();

    SHACL.LogicalConstraint getLogicalOperator();

}
