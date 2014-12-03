package org.aksw.rdfunit.exceptions;

import org.aksw.rdfunit.enums.TestCaseResultStatus;

/**
 * <p>TestCaseExecutionException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/5/14 2:27 PM
 * @version $Id: $Id
 */
public class TestCaseExecutionException extends Exception {

    private final TestCaseResultStatus status;

    /**
     * <p>Constructor for TestCaseExecutionException.</p>
     *
     * @param status a {@link org.aksw.rdfunit.enums.TestCaseResultStatus} object.
     * @param message a {@link java.lang.String} object.
     */
    public TestCaseExecutionException(TestCaseResultStatus status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * <p>Constructor for TestCaseExecutionException.</p>
     *
     * @param status a {@link org.aksw.rdfunit.enums.TestCaseResultStatus} object.
     * @param e a {@link java.lang.Throwable} object.
     */
    public TestCaseExecutionException(TestCaseResultStatus status, Throwable e) {
        super(e);
        this.status = status;
    }

    /**
     * <p>Constructor for TestCaseExecutionException.</p>
     *
     * @param status a {@link org.aksw.rdfunit.enums.TestCaseResultStatus} object.
     * @param message a {@link java.lang.String} object.
     * @param e a {@link java.lang.Throwable} object.
     */
    public TestCaseExecutionException(TestCaseResultStatus status, String message, Throwable e) {
        super(message, e);
        this.status = status;
    }

    /**
     * <p>Getter for the field <code>status</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.TestCaseResultStatus} object.
     */
    public TestCaseResultStatus getStatus() {
        return status;
    }
}
