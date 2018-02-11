package org.aksw.rdfunit.validate;

/**
 * @author Dimitris Kontokostas
 * @since 6/14/14 9:30 AM
 */
public class ParameterException extends Exception {

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
