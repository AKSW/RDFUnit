package org.aksw.rdfunit.exceptions;

/**
 * <p>TestCaseInstantiationException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/5/14 2:27 PM
 * @version $Id: $Id
 */
public class TestCaseInstantiationException extends Exception {


    /**
     * <p>Constructor for TestCaseInstantiationException.</p>
     */
    public TestCaseInstantiationException() {
        super();
    }

    /**
     * <p>Constructor for TestCaseInstantiationException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param e a {@link java.lang.Throwable} object.
     */
    public TestCaseInstantiationException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * <p>Constructor for TestCaseInstantiationException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public TestCaseInstantiationException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for TestCaseInstantiationException.</p>
     *
     * @param e a {@link java.lang.Throwable} object.
     */
    public TestCaseInstantiationException(Throwable e) {
        super(e);
    }

    /**
     * <p>Constructor for TestCaseInstantiationException.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public TestCaseInstantiationException(Exception e) {
        super(e);
    }
}
