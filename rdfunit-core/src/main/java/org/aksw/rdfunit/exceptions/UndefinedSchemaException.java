package org.aksw.rdfunit.exceptions;

/**
 * <p>UndefinedSchemaException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 6/14/14 9:30 AM
 * @version $Id: $Id
 */
public class UndefinedSchemaException extends Exception {
    private final String prefix;

    /**
     * <p>Constructor for UndefinedSchemaException.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     */
    public UndefinedSchemaException(String prefix) {
        super("Undefined prefix: " + prefix);
        this.prefix = prefix;
    }

    /**
     * <p>Constructor for UndefinedSchemaException.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public UndefinedSchemaException(String prefix, Throwable throwable) {
        super("Undefined prefix: " + prefix, throwable);
        this.prefix = prefix;
    }

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrefix() {
        return prefix;
    }
}
