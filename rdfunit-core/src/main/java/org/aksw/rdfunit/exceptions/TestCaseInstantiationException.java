package org.aksw.rdfunit.exceptions;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/5/14 2:27 PM
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
