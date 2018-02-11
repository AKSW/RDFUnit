package org.aksw.rdfunit.exceptions;

/**
 * @author Dimitris Kontokostas
 * @since 6/14/14 9:30 AM
 */
public class UndefinedSchemaException extends Exception {
    private final String prefix;

    public UndefinedSchemaException(String prefix) {
        super("Undefined prefix: " + prefix);
        this.prefix = prefix;
    }

    public UndefinedSchemaException(String prefix, Throwable throwable) {
        super("Undefined prefix: " + prefix, throwable);
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
