package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.model.interfaces.GenericTestCase;

public interface TargetBasedTestCase extends GenericTestCase {
    ShapeTarget getTarget();
}
