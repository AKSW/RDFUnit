package org.aksw.rdfunit.enums;

/**
 * <p>TestCaseExecutionType class.</p>
 *
 * @author Dimitris Kontokostas
 *         Defines the different types of results / execution we support
 * @since 2/2/14 3:37 PM
 * @version $Id: $Id
 */
public enum TestCaseExecutionType {
    /**
     * Runs the test cases and reports only one of @TestCaseResultStatus
     */
    statusTestCaseResult,

    /**
     * Extends the @statusTestCaseResult and provides the count and prevalence for every test case
     */
    aggregatedTestCaseResult,

    /**
     * Reports using the SHACL violation vocab but only severity, message and focusNode
     */
    shaclSimpleTestCaseResult,

    /**
     * Reports using the SHACL violation vocab with all annotations
     */
    shaclFullTestCaseResult,

    /**
     * Reports at the violation instance level. For every erroneous resource
     * generates an RLog Entry
     */
    @Deprecated
    rlogTestCaseResult,

    /**
     * Extends the @rlogTestCaseResult by providing richer information about each resource
     */
    @Deprecated
    extendedTestCaseResult
}
