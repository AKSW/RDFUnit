package org.aksw.rdfunit.model.interfaces.shacl;


import java.util.Map;
import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.apache.jena.rdf.model.RDFNode;


public interface ComponentValidator extends Validator {

  String getFilter();

  ComponentValidatorType getType();

  boolean filterAppliesForBindings(Shape shape, Map<ComponentParameter, RDFNode> bindings);

}
