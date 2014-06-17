package org.aksw.rdfunit.enums;

/**
 * User: Dimitris Kontokostas
 * Defines the different types of results / execution we support
 * Created: 2/2/14 3:37 PM
 */
public enum TestCaseExecutionType {
    /**
     * Runs the test cases and reports only one of @TestCaseResultStatus
     */
    statusTestCaseResult,

    /**
     * Extendes the @statusTestCaseResult and provides the count and prevalence for every test case
     */
    aggregatedTestCaseResult,

    /**
     * Reports at the violation instance level. For every erroneous resource
     * generates an RLog Entry
     */
    rlogTestCaseResult,

    /**
     * Extendes the @rlogTestCaseResult by providing richer information about each resource
     */
    extendedTestCaseResult
}
