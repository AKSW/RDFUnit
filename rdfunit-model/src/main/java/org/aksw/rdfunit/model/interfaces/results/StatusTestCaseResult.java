package org.aksw.rdfunit.model.interfaces.results;

import org.aksw.rdfunit.enums.TestCaseResultStatus;


/**
 * @author Dimitris Kontokostas
 * @since 1 /6/14 3:26 PM
 * @version $Id: $Id
 */
public interface StatusTestCaseResult extends TestCaseResult {
    TestCaseResultStatus getStatus();

}
