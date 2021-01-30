package org.aksw.rdfunit.exceptions;

/**
 * @author Dimitris Kontokostas
 * @since 6/14/14 9:30 AM
 */
public class UndefinedSerializationException extends Exception {

  private final String serialization;

  public UndefinedSerializationException(String serialization) {
    super("Undefined serialization: " + serialization);
    this.serialization = serialization;
  }

  public UndefinedSerializationException(String serialization, Throwable throwable) {
    super("Undefined serialization: " + serialization, throwable);
    this.serialization = serialization;
  }

  public String getSerialization() {
    return serialization;
  }
}
