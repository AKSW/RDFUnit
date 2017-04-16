package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.model.interfaces.Element;
import org.aksw.rdfunit.model.interfaces.PropertyConstraintGroup;

import java.util.Collection;
import java.util.List;

/**
 * A SHACL Shape
 * missing ATM: filters, sparql constraints, ...
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 12:18 AM
 * @version $Id: $Id
 */
public interface Shape extends Element {

    Collection<ShapeTarget> getTargets();

    List<PropertyConstraintGroup> getPropertyConstraintGroups();

}
