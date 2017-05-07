package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.enums.ShapeType;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.Element;

import java.util.Optional;

/**
 * A SHACL Shape
 * missing ATM: filter, sparql constraints, ...
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 12:18 AM
 * @version $Id: $Id
 */
public interface Shape extends Element {

    /** TODO convert to PropertyPath ... */
    Optional<String> getPath();

    default Boolean isPropertyShape()  {
        return getPath().isPresent();
    }

    default Boolean isNodeShape()  {
        return !isPropertyShape();
    }

    default ShapeType getShapeType() {
        return (isPropertyShape()? ShapeType.PROPERTY_SHAPE : ShapeType.NODE_SHAPE);
    }

    /**
     * Raw access to all values in this Shape
     */
    PropertyValuePairSet getPropertyValuePairSets();

}
