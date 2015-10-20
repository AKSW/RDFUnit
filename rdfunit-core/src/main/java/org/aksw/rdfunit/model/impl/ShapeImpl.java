package org.aksw.rdfunit.model.impl;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.PropertyConstraint;
import org.aksw.rdfunit.model.interfaces.Shape;
import org.aksw.rdfunit.model.interfaces.ShapeScope;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:07 PM
 */
public class ShapeImpl implements Shape {

    private final Resource resource;
    private final ShapeScope shapeScope;
    private final RLOGLevel severity;
    private final Collection<PropertyConstraint> propertyContraints;

    public ShapeImpl(Resource resource, ShapeScope shapeScope, RLOGLevel severity, Collection<PropertyConstraint> propertyContraints) {
        this.resource = resource;
        this.shapeScope = shapeScope;
        this.severity = severity;
        this.propertyContraints = propertyContraints;
    }

    @Override
    public Optional<ShapeScope> getShapeScope() {
        return Optional.fromNullable(shapeScope);
    }

    @Override
    public Optional<RLOGLevel> getSeverityLevel() {
        return Optional.fromNullable(severity);
    }

    @Override
    public Collection<PropertyConstraint> getPropertyConstraints() {
        return propertyContraints;
    }

    @Override
    public Resource getResource() {
        return resource;
    }
}
