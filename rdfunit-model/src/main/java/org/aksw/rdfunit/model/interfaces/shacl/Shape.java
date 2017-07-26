package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.ShapeType;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.Element;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

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
    Optional<ShapePath> getPath();

    /**
     * Raw access to all values in this Shape
     */
    PropertyValuePairSet getPropertyValuePairSets();


    default Boolean isPropertyShape()  {
        return getPath().isPresent();
    }

    default Boolean isNodeShape()  {
        return !isPropertyShape();
    }

    default ShapeType getShapeType() {
        return (isPropertyShape()? ShapeType.PROPERTY_SHAPE : ShapeType.NODE_SHAPE);
    }

    default RLOGLevel getSeverity() {

        return getPropertyValuePairSets().getPropertyValues(SHACL.severity).stream()
            .filter(RDFNode::isResource)
            .map(RDFNode::asResource)
            .map( r -> RLOGLevel.resolve(r.getURI()) )
            .filter( s -> s != null)
            .findFirst()
            .orElse(RLOGLevel.ERROR);
    }

    default Optional<String> getMessage() {
        return getPropertyValuePairSets().getPropertyValues(SHACL.message).stream()
                .filter(RDFNode::isLiteral)
                .map(RDFNode::asLiteral)
                .map(Literal::getLexicalForm)
                .findFirst();
    }

    default boolean isDeactivated() {

        return getElement().hasLiteral(SHACL.deactivated, true);
    }

}
