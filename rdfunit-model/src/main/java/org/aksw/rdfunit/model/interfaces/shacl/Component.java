package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.model.interfaces.Element;

import java.util.Collection;

public interface Component extends Element {

    Collection<ComponentParameter> getParameters();

    Collection<ComponentValidator> getValidators();

    boolean isPartial();
}
