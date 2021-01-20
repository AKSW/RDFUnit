package org.aksw.rdfunit.webdemo.utils;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 6:51 PM
 */
public enum SchemaOption {
  AUTO_DATASET,           // normal dataset, automatic schema detection (should exclude OWL and RDFS tests)
  AUTO_ONTOLOGY,          // an ontology, will include OWL and RDFS tests
  SPECIFIC_URIS,          // a specific ontology to use
  CUSTOM_TEXT             // custom ontology, provided via UI
}
