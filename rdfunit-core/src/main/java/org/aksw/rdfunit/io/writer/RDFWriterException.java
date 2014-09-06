package org.aksw.rdfunit.io.writer;

/**
 * @author Dimitris Kontokostas
 *         Exceptions for writer that cannot write()
 * @since 11/14/13 8:35 AM
 */
public class RDFWriterException extends Exception {


    public RDFWriterException() {
        super();
    }

    public RDFWriterException(String message, Throwable e) {
        super(message, e);
    }

    public RDFWriterException(String message) {
        super(message);
    }

    public RDFWriterException(Throwable e) {
        super(e);
    }

    public RDFWriterException(Exception e) {
        super(e);
    }
}