package org.aksw.rdfunit.io.writer;

/**
 * Exceptions for writer that cannot write()
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:35 AM
 */
public class RdfWriterException extends Exception {


  public RdfWriterException() {
    super();
  }

  public RdfWriterException(String message, Throwable e) {
    super(message, e);
  }

  public RdfWriterException(String message) {
    super(message);
  }

  public RdfWriterException(Throwable e) {
    super(e);
  }

  public RdfWriterException(Exception e) {
    super(e);
  }
}
