package org.aksw.rdfunit.model.interfaces;

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
public interface Shape extends Element{

    Collection<ShapeScope> getScopes();

    List<PropertyConstraintGroup> getPropertyConstraintGroups();

}
