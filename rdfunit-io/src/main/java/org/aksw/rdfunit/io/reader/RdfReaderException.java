package org.aksw.rdfunit.io.reader;

/**
 * <p>RDFReaderException class.</p>
 *
 * @author Dimitris Kontokostas
 *         Exceptions for readers that cannot read()
 * @since 11/14/13 8:35 AM
 * @version $Id: $Id
 */
public class RdfReaderException extends Exception {


    /**
     * <p>Constructor for RDFReaderException.</p>
     */
    public RdfReaderException() {
        super();
    }

    /**
     * <p>Constructor for RDFReaderException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param e a {@link java.lang.Throwable} object.
     */
    public RdfReaderException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * <p>Constructor for RDFReaderException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public RdfReaderException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for RDFReaderException.</p>
     *
     * @param e a {@link java.lang.Throwable} object.
     */
    public RdfReaderException(Throwable e) {
        super(e);
    }

    /**
     * <p>Constructor for RDFReaderException.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public RdfReaderException(Exception e) {
        super(e);
    }
}
