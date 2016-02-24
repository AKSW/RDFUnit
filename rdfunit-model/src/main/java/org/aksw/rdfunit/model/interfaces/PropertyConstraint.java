package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Set;

/**
 * a simple core shacl property constraint
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 12:18 AM
 * @version $Id: $Id
 */
public interface PropertyConstraint {

    /**
     * Gets the constraint group that contains this constraint
     */
    //PropertyConstraintGroup getPropertyConstraintGroup();

    //**
    // * a shortcut to get the constraining property
    // */
    //default Property getProperty() {
    //    return getPropertyConstraintGroup().getProperty();
    //}

    /**
     * Returns the constraining facet e.g. sh:minCount
     */
    Property getFacetProperty();

    /**
     * The values associated with the constraining facet. might be many e.g. sh:in
     */
    Set<RDFNode> getFacetValues();

    /**
     * additional parameters e.g. sh:flags from sh:pattern
     */
    Set<PropertyValuePair> getAdditionalArguments();

    TestCase getTestCase(boolean forInverseProperty);

    boolean usesValidatorFunction();

}
