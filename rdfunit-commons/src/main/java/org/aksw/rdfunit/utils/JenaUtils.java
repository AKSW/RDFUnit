package org.aksw.rdfunit.utils;

import java.net.URI;
import java.util.UUID;
import org.aksw.rdfunit.commons.RdfUnitModelFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

public final class JenaUtils {

  private static final String DEFAULT_PREFIX = "urn:uuid:";

  private JenaUtils() {
  }

  public static String getUniqueIri(String prefix) {
    return prefix + UUID.randomUUID().toString();
  }

  public static String getUniqueIri() {
    return getUniqueIri(JenaUtils.DEFAULT_PREFIX);
  }

  public static Model readModel(URI uri) {

    Model model = RdfUnitModelFactory.createDefaultModel();
    RDFDataMgr.read(model, uri.toString());
    return model;
  }
}
