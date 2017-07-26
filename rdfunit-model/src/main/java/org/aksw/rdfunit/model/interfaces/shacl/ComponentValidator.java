package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.enums.ShapeType;
import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Map;
import java.util.Optional;
import java.util.Set;


public interface ComponentValidator extends Element {

    Optional<String> getDefaultMessage();

    String getFilter();

    String getSparqlQuery();

    ComponentValidatorType getType();

    Set<PrefixDeclaration> getPrefixDeclarations();

    boolean filterAppliesForBindings(ShapeType shapeType, Map<ComponentParameter, RDFNode> bindings);

}
