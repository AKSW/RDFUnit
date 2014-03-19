package org.aksw.rdfunit.exceptions;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/5/14 2:27 PM
 */
public class TestCaseException extends Exception {


    public TestCaseException() {
    }

    public TestCaseException(String message, Throwable e) {
        super(message, e);
    }

    public TestCaseException(String message) {
        super(message);
    }

    public TestCaseException(Throwable e) {
        super(e);
    }

    public TestCaseException(Exception e) {
        super(e);
    }
}
