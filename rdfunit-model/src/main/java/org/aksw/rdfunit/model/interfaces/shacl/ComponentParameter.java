package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Optional;

/**
 * Interface for SHACL Arguments
 *
 * @author Dimitris Kontokostas
 * @since 6 /17/15 3:15 PM
 * @version $Id: $Id
 */
public interface ComponentParameter extends Element {

    boolean isOptional();
    default boolean isRequired() {return !isOptional();}

    Property getPredicate();

    Optional<RDFNode> getDefaultValue();

    default String getParameterName() {return getPredicate().getLocalName();}

}
