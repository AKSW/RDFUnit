package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.model.interfaces.Element;

import java.util.Collection;
import java.util.Optional;


public interface ComponentValidator extends Element {

    Optional<String> getDefaultMessage();

    Collection<String> getFilters();

    String getSparqlQuery();

    ComponentValidatorType getType();

}
