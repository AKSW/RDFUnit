package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;

/**
 * A source can be various things like a dataset, a vocabulary or an application
 *
 * @author Dimitris Kontokostas
 */
public interface Source {

  String getPrefix();

  String getUri();

  TestAppliesTo getSourceType();
}