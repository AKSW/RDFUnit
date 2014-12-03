package org.aksw.rdfunit.exceptions;

/**
 * <p>UndefinedSerializationException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 6/14/14 9:30 AM
 * @version $Id: $Id
 */
public class UndefinedSerializationException extends Exception {
    private final String serialization;

    /**
     * <p>Constructor for UndefinedSerializationException.</p>
     *
     * @param serialization a {@link java.lang.String} object.
     */
    public UndefinedSerializationException(String serialization) {
        super("Undefined serialization: " + serialization);
        this.serialization = serialization;
    }

    /**
     * <p>Constructor for UndefinedSerializationException.</p>
     *
     * @param serialization a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public UndefinedSerializationException(String serialization, Throwable throwable) {
        super("Undefined serialization: " + serialization, throwable);
        this.serialization = serialization;
    }

    /**
     * <p>Getter for the field <code>serialization</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSerialization() {
        return serialization;
    }
}
