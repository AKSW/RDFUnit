package org.aksw.rdfunit.model.impl;

import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.PropertyConstraint;
import org.aksw.rdfunit.model.interfaces.PropertyConstraintGroup;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author Dimitris Kontokostas
 * @since 11/2/2016 12:34 μμ
 */
@Value
@Builder
public class PropertyConstraintGroupImpl implements PropertyConstraintGroup{
    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final Property property;
    @Getter @NonNull private final PropertyValuePairSet propertyValuePairSet;
    @Getter @NonNull @Singular private final ImmutableSet<PropertyConstraint> propertyConstraints;
    private final boolean isInverse;

    @Override
    public boolean isInverse() { return isInverse;}
}
