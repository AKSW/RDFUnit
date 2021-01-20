package org.aksw.rdfunit.io.reader;

/**
 * Exceptions for readers that cannot read()
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:35 AM
 */
public class RdfReaderException extends Exception {


  public RdfReaderException() {
    super();
  }

  public RdfReaderException(String message, Throwable e) {
    super(message, e);
  }

  public RdfReaderException(String message) {
    super(message);
  }

  public RdfReaderException(Throwable e) {
    super(e);
  }

  public RdfReaderException(Exception e) {
    super(e);
  }
}
