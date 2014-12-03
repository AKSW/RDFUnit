package org.aksw.rdfunit.io.writer;

/**
 * <p>RDFWriterException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Exceptions for writer that cannot write()
 * @since 11/14/13 8:35 AM
 * @version $Id: $Id
 */
public class RDFWriterException extends Exception {


    /**
     * <p>Constructor for RDFWriterException.</p>
     */
    public RDFWriterException() {
        super();
    }

    /**
     * <p>Constructor for RDFWriterException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param e a {@link java.lang.Throwable} object.
     */
    public RDFWriterException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * <p>Constructor for RDFWriterException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public RDFWriterException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for RDFWriterException.</p>
     *
     * @param e a {@link java.lang.Throwable} object.
     */
    public RDFWriterException(Throwable e) {
        super(e);
    }

    /**
     * <p>Constructor for RDFWriterException.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public RDFWriterException(Exception e) {
        super(e);
    }
}
