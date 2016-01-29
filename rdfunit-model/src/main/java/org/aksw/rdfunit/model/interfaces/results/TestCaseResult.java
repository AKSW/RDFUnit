package org.aksw.rdfunit.model.interfaces.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.datatypes.xsd.XSDDateTime;

/**
 * An abstract Test Case Result.
 *
 * @author Dimitris Kontokostas
 * @since 1 /2/14 3:44 PM
 * @version $Id: $Id
 */
public interface TestCaseResult extends Element {

    String getTestCaseUri();
    RLOGLevel getSeverity();
    String getMessage();
    XSDDateTime getTimestamp();

}