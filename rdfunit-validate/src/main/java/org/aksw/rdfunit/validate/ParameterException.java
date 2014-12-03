package org.aksw.rdfunit.validate;

/**
 * <p>ParameterException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 6/14/14 9:30 AM
 * @version $Id: $Id
 */
public class ParameterException extends Exception {

    /**
     * <p>Constructor for ParameterException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public ParameterException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for ParameterException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public ParameterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
