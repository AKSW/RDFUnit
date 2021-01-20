package org.aksw.rdfunit.model.interfaces.shacl;

import java.util.Set;
import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.path.Path;

/**
 * @author Dimitris Kontokostas
 * @since 7/8/17
 */
public interface ShapePath extends Element {

  boolean isPredicatePath();

  String asSparqlPropertyPath();

  Path getJenaPath();

  Resource getPathAsRdf();

  Set<Resource> getUsedProperties();
}
