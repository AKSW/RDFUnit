package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Describes where a test can apply to
 *
 * @author Dimitris Kontokostas
 * @since 9/25/13 9:06 AM
 */
public enum TestAppliesTo {
  /**
   * Schema: When the tests applies to an ontology / vocabulary
   */
  Schema,

  /**
   * EnrichedSchema: When the tests applies to an ontology / vocabulary enriched with external
   * (semi-)automatic approaches
   */
  EnrichedSchema,

  /**
   * Dataset: When the tests applies to a dataset only (i.e. a SPARQL Endpoint)
   */
  Dataset,

  /**
   * Application: When the tests are specific to an application only
   */
  Application;

  /**
   * Holder the prefix to resolve this enum
   */
  private static final String schemaPrefix = "rut";

  /**
   * Resolves a full URI/IRI to an enum
   *
   * @param value the URI/IRI we want to resolve
   * @return the equivalent enum type or null if it cannot resolve
   */
  public static TestAppliesTo resolve(String value) {

    String qName = PrefixNSService.getLocalName(value, schemaPrefix);
    for (TestAppliesTo appliesTo : values()) {
      if (qName.equals(appliesTo.name())) {
        return appliesTo;
      }
    }
    return null;
  }

  /**
   * @return a full URI/IRI as a String
   */
  public String getUri() {
    return PrefixNSService.getNSFromPrefix(schemaPrefix) + name();
  }

  @Override
  public String toString() {
    return getUri();
  }
}
