package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.apache.jena.rdf.model.Property;

import java.util.Set;

/**
 * @author Dimitris Kontokostas
 * @since 8/21/15 12:18 AM
 * @version $Id: $Id
 */
public interface PropertyConstraintGroup extends Element{

    /**
     * The property involved in this property constraint group
     */
    Property getProperty();

    /**
     * The list of property constraints
     */
    Set<PropertyConstraint> getPropertyConstraints();

    /**
     * Raw access to all values in this PropertyGroup
     */
    PropertyValuePairSet getPropertyValuePairSet();

    /**
     * return true if it is an inverse property constraint, false otherwise
     */
    boolean isInverse();

    default String getPropertyFilter() {
        if (isInverse())  {
            return " ?value <" + getProperty().getURI() + "> ?this .";
        }
        else return " ?this <" + getProperty().getURI() + "> ?value .";
    }
}
