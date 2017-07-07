package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.enums.ShapeTargetType;

public interface ShapeTarget {

    ShapeTargetType getTargetType();

    String getUri();

    String getPattern();




}
