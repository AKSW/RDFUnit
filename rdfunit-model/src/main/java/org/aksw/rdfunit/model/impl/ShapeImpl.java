package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Resource;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.PropertyConstraint;
import org.aksw.rdfunit.model.interfaces.Shape;
import org.aksw.rdfunit.model.interfaces.ShapeScope;

import java.util.Collection;
import java.util.Optional;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:07 PM
 * @version $Id: $Id
 */
@ToString
@EqualsAndHashCode
public class ShapeImpl implements Shape {

    private final Resource element;
    private final ShapeScope shapeScope;
    private final RLOGLevel severity;
    private final Collection<PropertyConstraint> propertyContraints;

    /**
     * <p>Constructor for ShapeImpl.</p>
     *
     * @param element a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param shapeScope a {@link org.aksw.rdfunit.model.interfaces.ShapeScope} object.
     * @param severity a {@link org.aksw.rdfunit.enums.RLOGLevel} object.
     * @param propertyContraints a {@link java.util.Collection} object.
     */
    public ShapeImpl(Resource element, ShapeScope shapeScope, RLOGLevel severity, Collection<PropertyConstraint> propertyContraints) {
        this.element = element;
        this.shapeScope = shapeScope;
        this.severity = severity;
        this.propertyContraints = propertyContraints;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ShapeScope> getScope() {
        return Optional.ofNullable(shapeScope);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<RLOGLevel> getSeverityLevel() {
        return Optional.ofNullable(severity);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<PropertyConstraint> getPropertyConstraints() {
        return propertyContraints;
    }

    /** {@inheritDoc} */
    @Override
    public Resource getElement() {
        return element;
    }
}
