package org.aksw.rdfunit.io.writer;

/**
 * <p>RDFWriterException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Exceptions for writer that cannot write()
 * @since 11/14/13 8:35 AM
 * @version $Id: $Id
 */
public class RdfWriterException extends Exception {


    /**
     * <p>Constructor for RDFWriterException.</p>
     */
    public RdfWriterException() {
        super();
    }

    /**
     * <p>Constructor for RDFWriterException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param e a {@link java.lang.Throwable} object.
     */
    public RdfWriterException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * <p>Constructor for RDFWriterException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public RdfWriterException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for RDFWriterException.</p>
     *
     * @param e a {@link java.lang.Throwable} object.
     */
    public RdfWriterException(Throwable e) {
        super(e);
    }

    /**
     * <p>Constructor for RDFWriterException.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public RdfWriterException(Exception e) {
        super(e);
    }
}
