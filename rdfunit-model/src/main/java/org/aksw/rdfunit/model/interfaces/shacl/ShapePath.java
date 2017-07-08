package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.model.interfaces.Element;

/**
 * @author Dimitris Kontokostas
 * @since 7/8/17
 */
public interface ShapePath extends Element {

    boolean isPredicatePath();
    String asSparqlPropertyPath();
    org.apache.jena.sparql.path.Path getJenaPath();
}
