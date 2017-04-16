package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.enums.ShapeTargetType;

import java.util.Optional;

public interface ShapeTarget {

    ShapeTargetType getTargetType();

    Optional<String> getUri();

    String getPattern();




}
