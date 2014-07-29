package org.aksw.rdfunit.exceptions;

/**
 * @author Dimitris Kontokostas
 *         Exceptions for writer that cannot write()
 * @since 11/14/13 8:35 AM
 */
public class TripleWriterException extends Exception {


    public TripleWriterException() {
        super();
    }

    public TripleWriterException(String message, Throwable e) {
        super(message, e);
    }

    public TripleWriterException(String message) {
        super(message);
    }

    public TripleWriterException(Throwable e) {
        super(e);
    }

    public TripleWriterException(Exception e) {
        super(e);
    }
}