package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.rdf.model.Literal;

public interface SparqlConstraint {

  Shape getShape();

  Literal getMessage();

  Validator getValidator();

  TestCase getTestCase();

}
