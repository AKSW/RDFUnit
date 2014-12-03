package org.aksw.rdfunit.exceptions;

/**
 * <p>BindingException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/5/14 2:27 PM
 * @version $Id: $Id
 */
public class BindingException extends Exception {


    /**
     * <p>Constructor for BindingException.</p>
     */
    public BindingException() {
        super();
    }

    /**
     * <p>Constructor for BindingException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param e a {@link java.lang.Throwable} object.
     */
    public BindingException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * <p>Constructor for BindingException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public BindingException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for BindingException.</p>
     *
     * @param e a {@link java.lang.Throwable} object.
     */
    public BindingException(Throwable e) {
        super(e);
    }

    /**
     * <p>Constructor for BindingException.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public BindingException(Exception e) {
        super(e);
    }
}
