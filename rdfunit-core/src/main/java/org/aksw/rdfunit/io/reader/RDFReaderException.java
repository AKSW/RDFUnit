package org.aksw.rdfunit.io.reader;

/**
 * @author Dimitris Kontokostas
 *         Exceptions for readers that cannot read()
 * @since 11/14/13 8:35 AM
 */
public class RDFReaderException extends Exception {


    public RDFReaderException() {
        super();
    }

    public RDFReaderException(String message, Throwable e) {
        super(message, e);
    }

    public RDFReaderException(String message) {
        super(message);
    }

    public RDFReaderException(Throwable e) {
        super(e);
    }

    public RDFReaderException(Exception e) {
        super(e);
    }
}