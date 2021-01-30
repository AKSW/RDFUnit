package org.aksw.rdfunit.model.interfaces.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;

/**
 * An abstract Test Case Result.
 *
 * @author Dimitris Kontokostas
 * @since 1 /2/14 3:44 PM
 */
public interface TestCaseResult extends Element {

  Resource getTestCaseUri();

  RLOGLevel getSeverity();

  String getMessage();

  XSDDateTime getTimestamp();

}