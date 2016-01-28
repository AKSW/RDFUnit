package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Resource;
import lombok.*;
import org.aksw.rdfunit.model.interfaces.PropertyConstraintGroup;
import org.aksw.rdfunit.model.interfaces.Shape;
import org.aksw.rdfunit.model.interfaces.ShapeScope;

import java.util.Collection;

/**
 * A simple Shape implementation
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:07 PM
 * @version $Id: $Id
 */
@Builder
@Value
public class ShapeImpl implements Shape {

    @Getter @NonNull private final Resource element;
    @Getter @NonNull @Singular private final Collection<ShapeScope> scopes;
    @Getter @NonNull @Singular private final Collection<PropertyConstraintGroup> propertyConstraintGroups;

}
