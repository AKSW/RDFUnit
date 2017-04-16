package org.aksw.rdfunit.model.impl.shacl;

import lombok.*;
import org.aksw.rdfunit.model.interfaces.PropertyConstraintGroup;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

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
    @Getter @NonNull @Singular private final List<ShapeTarget> targets;
    @Getter @NonNull @Singular private final List<PropertyConstraintGroup> propertyConstraintGroups;

}
