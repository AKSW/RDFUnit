package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.enums.ShapeScopeType;

import java.util.Optional;

public interface ShapeScope {

    ShapeScopeType getScopeType();

    Optional<String> getUri();

    String getPattern();




}
