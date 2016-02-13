package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.apache.jena.ext.com.google.common.collect.ImmutableSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;
import java.util.Set;

/**
 * Interface for SHACL Arguments
 *
 * @author Dimitris Kontokostas
 * @since 6 /17/15 3:15 PM
 * @version $Id: $Id
 */
public interface Argument extends Element{

    String getComment();

    boolean isOptional();

    Property getPredicate();

    Optional<Resource> getValueType();

    Optional<ValueKind> getValueKind();

    Optional<RDFNode> getDefaultValue();

    /**
     * Given a possible list of property/value pairs, returns true if the argument
     * can provide a bind
     */
    default boolean canBind(PropertyValuePairSet propertyValuePairSet) {
        return propertyValuePairSet.contains(getPredicate()) || getDefaultValue().isPresent();
    }

    /**
     * Given a possible list of property/value pairs, returns a bind (if applicable)
     * if there is a default value, the default value will be sent if no bind is found
     * multiple values are returned when e.g. we have a list
     */
    default Optional<RDFNode> getBindFromValues(PropertyValuePairSet propertyValuePairSet) {
        Set<RDFNode> values = propertyValuePairSet.getPropertyValues(getPredicate());
        if (values.isEmpty() && getDefaultValue().isPresent()) {
            values = ImmutableSet.of(getDefaultValue().get()) ;
        }
        if (values.size() >1) {
            throw new IllegalArgumentException("FIXME: multiple arguments given");
        }
        return values.stream().findFirst();
    }
}
