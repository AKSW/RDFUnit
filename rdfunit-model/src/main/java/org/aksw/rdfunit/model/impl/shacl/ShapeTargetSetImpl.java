package org.aksw.rdfunit.model.impl.shacl;

import lombok.EqualsAndHashCode;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTargetSet;
import org.aksw.rdfunit.vocabulary.SHACL;

import java.util.Collections;
import java.util.Set;

@EqualsAndHashCode()
public class ShapeTargetSetImpl implements ShapeTargetSet {

    private final Set<ShapeTarget> targets;
    private final SHACL.LogicalConstraint operator;

    public ShapeTargetSetImpl(Set<ShapeTarget> targets, SHACL.LogicalConstraint operator) {
        this.targets = targets;
        this.operator = operator;
    }

    public ShapeTargetSetImpl(ShapeTarget target) {
        this(Collections.singleton(target), SHACL.LogicalConstraint.atomic);         // defaults to atomic
    }

    @Override
    public Set<ShapeTarget> getTargets() {
        return targets;
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return operator;
    }
}
