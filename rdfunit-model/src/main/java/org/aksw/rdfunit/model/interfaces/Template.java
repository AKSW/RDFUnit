package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Dimitris Kontokostas
 * @since 8/21/15 2:30 PM

 */
public interface Template extends Element {

    Collection<ComponentParameter> getArguments();

    String getSparql();

    Optional<String> getLabelTemplate();
}
