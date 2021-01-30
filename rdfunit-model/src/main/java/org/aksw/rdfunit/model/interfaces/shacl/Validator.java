package org.aksw.rdfunit.model.interfaces.shacl;


import java.util.Optional;
import java.util.Set;
import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.rdf.model.Literal;


public interface Validator extends Element {

  Optional<Literal> getDefaultMessage();

  String getSparqlQuery();

  Set<PrefixDeclaration> getPrefixDeclarations();

}
