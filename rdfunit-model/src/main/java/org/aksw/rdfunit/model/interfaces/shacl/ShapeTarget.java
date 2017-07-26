package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.enums.ShapeTargetType;
import org.apache.jena.rdf.model.RDFNode;

public interface ShapeTarget {

    ShapeTargetType getTargetType();

    RDFNode getNode();

    String getPattern();




}
