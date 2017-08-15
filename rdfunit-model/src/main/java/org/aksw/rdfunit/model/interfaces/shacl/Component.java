package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.rdf.model.Property;

import java.util.Collection;
import java.util.Optional;

public interface Component extends Element {

    Collection<ComponentParameter> getParameters();
    default Optional<ComponentParameter> getParameter(Property property) {
        return getParameters().stream().filter(cp -> cp.getPredicate().equals(property)).findAny();
    }

    Collection<ComponentValidator> getValidators();
}
