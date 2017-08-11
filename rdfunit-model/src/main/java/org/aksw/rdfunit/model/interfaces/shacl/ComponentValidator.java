package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.enums.ShapeType;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Map;


public interface ComponentValidator extends Validator {

    String getFilter();
    ComponentValidatorType getType();
    boolean filterAppliesForBindings(ShapeType shapeType, Map<ComponentParameter, RDFNode> bindings);

}
