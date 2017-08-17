package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Map;


public interface ComponentValidator extends Validator {

    String getFilter();
    ComponentValidatorType getType();
    boolean filterAppliesForBindings(Shape shape, Map<ComponentParameter, RDFNode> bindings);

}
