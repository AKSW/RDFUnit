package org.aksw.rdfunit.model.interfaces.results;

import org.aksw.rdfunit.model.interfaces.Element;

import java.util.Collection;

/**
 * Describes an annotation on a Shape or RDFUnit tests, patterns and generators
 *
 * @author Dimitris Kontokostas
 * @since 8 /15/15 4:26 PM
 * @version $Id: $Id
 */
public interface TestExecution extends Element {

    String getTestExecutionUri();

    Collection<TestCaseResult> getTestCaseResults();
}
