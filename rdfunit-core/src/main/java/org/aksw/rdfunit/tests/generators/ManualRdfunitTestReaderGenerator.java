package org.aksw.rdfunit.tests.generators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.utils.TestUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Reads the manual tests that are defined in the test source
 */
@Slf4j
public final class ManualRdfunitTestReaderGenerator implements RdfUnitTestGenerator {


  public ManualRdfunitTestReaderGenerator() {
  }

  @Override
  public Collection<? extends GenericTestCase> generate(SchemaSource source) {
    return generateFromSource(source, source.getModel());
  }

  @Override
  public Collection<? extends GenericTestCase> generate(TestSource source) {
    return generateFromSource(source, ModelFactory.createDefaultModel());
  }

  private Collection<GenericTestCase> generateFromSource(Source source, Model sourceModel) {
    Set<GenericTestCase> tests = new HashSet<>();

    tests.addAll(TestUtils.instantiateTestsFromModel(sourceModel));

    log.info("{} identified {} manually written tests", source.getUri(), tests.size());
    return tests;

  }
}
