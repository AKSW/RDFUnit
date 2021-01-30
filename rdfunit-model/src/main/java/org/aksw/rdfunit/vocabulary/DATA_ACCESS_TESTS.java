package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Core SHACL Vocabulary
 *
 * @author Markus Ackermann
 * @since 7/14/17 4:17 PM
 */
@SuppressWarnings("ClassWithTooManyFields")
public final class DATA_ACCESS_TESTS {

  //namespace
  /**
   * Constant <code>namespace="http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#"</code>
   */
  public static final String namespace = "http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#";

  //classes
  public static final Resource Manifest = resource("Manifest");

  //properties
  public static final Property include = property("include");
  public static final Property entries = property("entries");
  public static final Property action = property("action");
  public static final Property result = property("result");
  public static final Property status = property("status");

  private DATA_ACCESS_TESTS() {
  }

  private static Resource resource(String local) {
    return ResourceFactory.createResource(namespace + local);
  }

  private static Property property(String local) {
    return ResourceFactory.createProperty(namespace, local);
  }
}
