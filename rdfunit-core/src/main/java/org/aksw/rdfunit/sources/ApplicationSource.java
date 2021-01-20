package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;

/**
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:57 PM
 */
public class ApplicationSource implements Source {


  private final SourceConfig sourceConfig;

  ApplicationSource(String prefix, String uri) {
    this.sourceConfig = new SourceConfig(prefix, uri);
  }


  @Override
  public String getPrefix() {
    return sourceConfig.getPrefix();
  }


  @Override
  public String getUri() {
    return sourceConfig.getUri();
  }


  @Override
  public TestAppliesTo getSourceType() {
    return TestAppliesTo.Application;
  }


}
