package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.rdf.model.Literal;

import java.util.Optional;
import java.util.Set;


public interface Validator extends Element {

    Optional<Literal> getDefaultMessage();

    String getSparqlQuery();

    Set<PrefixDeclaration> getPrefixDeclarations();

}
