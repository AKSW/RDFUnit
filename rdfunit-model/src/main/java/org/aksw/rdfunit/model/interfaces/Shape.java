package org.aksw.rdfunit.model.interfaces;

import com.google.common.base.Optional;
import org.aksw.rdfunit.enums.RLOGLevel;

import java.util.Collection;

/**
 * A SHACL Shape
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 12:18 AM
 * @version $Id: $Id
 */
public interface Shape extends Element{

    /**
     * <p>getScope.</p>
     *
     * @return a {@link com.google.common.base.Optional} object.
     */
    Optional<ShapeScope> getShapeScope();

    /**
     * <p>getLogLevel.</p>
     *
     * @return a {@link com.google.common.base.Optional} object.
     */
    Optional<RLOGLevel> getSeverityLevel();

    /**
     * <p>getPropertyConstraints.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<PropertyConstraint> getPropertyConstraints();
}
