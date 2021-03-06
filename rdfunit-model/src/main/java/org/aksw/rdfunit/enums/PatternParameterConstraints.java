package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Enumerates the different parameter constrains
 *
 * @author Dimitris Kontokostas
 * @since 9/25/13 10:35 AM
 */
public enum PatternParameterConstraints {

  /**
   * The parameter can a resource
   */
  Resource,

  /**
   * The parameter is restricted to a property
   */
  Property,

  /**
   * The parameter is restricted to a Class
   */
  Class,

  /**
   * The parameter can be an operator
   */
  Operator,

  /**
   * The parameter can be anything, even a free variable (?var)
   */
  None;

  /**
   * Holds the prefix to resolve this enum
   */
  private static final String SCHEMA_PREFIX = "rut";

  /**
   * Resolves a full URI/IRI to an enum
   *
   * @param value the URI/IRI we want to resolve
   * @return the equivalent enum type or @None as default
   */
  public static PatternParameterConstraints resolve(String value) {

    String qName = PrefixNSService.getLocalName(value, SCHEMA_PREFIX);
    for (PatternParameterConstraints constraint : values()) {
      if (qName.equals(constraint.name())) {
        return constraint;
      }
    }
    return None;
  }

  public String getUri() {
    return PrefixNSService.getNSFromPrefix(SCHEMA_PREFIX) + name();
  }

  @Override
  public String toString() {
    return getUri();
  }
}
