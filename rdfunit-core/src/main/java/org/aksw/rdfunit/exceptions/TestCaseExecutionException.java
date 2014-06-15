package org.aksw.rdfunit.exceptions;

import org.aksw.rdfunit.enums.TestCaseResultStatus;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/5/14 2:27 PM
 */
public class TestCaseExecutionException extends Exception {

    private final TestCaseResultStatus status;

    public TestCaseExecutionException(TestCaseResultStatus status, Throwable e) {
        super(e);
        this.status = status;
    }

    public TestCaseResultStatus getStatus() {
        return status;
    }
}
