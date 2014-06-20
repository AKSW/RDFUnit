package org.aksw.rdfunit.exceptions;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/5/14 2:27 PM
 */
public class BindingException extends Exception {


    public BindingException() {
    }

    public BindingException(String message, Throwable e) {
        super(message, e);
    }

    public BindingException(String message) {
        super(message);
    }

    public BindingException(Throwable e) {
        super(e);
    }

    public BindingException(Exception e) {
        super(e);
    }
}
