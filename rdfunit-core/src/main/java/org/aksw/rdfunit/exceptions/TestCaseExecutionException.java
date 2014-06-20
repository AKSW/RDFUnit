package org.aksw.rdfunit.exceptions;

import org.aksw.rdfunit.enums.TestCaseResultStatus;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/5/14 2:27 PM
 */
public class TestCaseExecutionException extends Exception {

    private final TestCaseResultStatus status;

    public TestCaseExecutionException(TestCaseResultStatus status, String message) {
        super(message);
        this.status = status;
    }

    public TestCaseExecutionException(TestCaseResultStatus status, Throwable e) {
        super(e);
        this.status = status;
    }

    public TestCaseExecutionException(TestCaseResultStatus status, String message, Throwable e) {
        super(message, e);
        this.status = status;
    }

    public TestCaseResultStatus getStatus() {
        return status;
    }
}
