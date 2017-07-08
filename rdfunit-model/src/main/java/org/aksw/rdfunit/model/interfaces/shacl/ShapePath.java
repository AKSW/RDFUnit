package org.aksw.rdfunit.model.interfaces.shacl;

/**
 * @author Dimitris Kontokostas
 * @since 7/8/17
 */
public interface ShapePath {

    boolean isPredicatePath();
    String asSparqlPropertyPath();
    org.apache.jena.sparql.path.Path getJenaPath();
}
