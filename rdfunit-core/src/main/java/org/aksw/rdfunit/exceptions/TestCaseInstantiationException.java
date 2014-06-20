package org.aksw.rdfunit.exceptions;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/5/14 2:27 PM
 */
public class TestCaseInstantiationException extends Exception {


    public TestCaseInstantiationException() {
    }

    public TestCaseInstantiationException(String message, Throwable e) {
        super(message, e);
    }

    public TestCaseInstantiationException(String message) {
        super(message);
    }

    public TestCaseInstantiationException(Throwable e) {
        super(e);
    }

    public TestCaseInstantiationException(Exception e) {
        super(e);
    }
}
