package org.aksw.rdfunit.model.interfaces;

import com.hp.hpl.jena.rdf.model.Property;

import java.util.Collection;

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
    Collection<PropertyConstraint> getPropertyConstraints();

    /**
     * return true if it is an inverse property constraint, false otherwise
     */
    boolean isInverse();
}
