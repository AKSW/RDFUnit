package org.aksw.rdfunit.model.interfaces.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.Element;

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