package org.aksw.rdfunit.tests.generators;

import java.util.ArrayList;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.writer.RdfFileWriter;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.sources.CacheUtils;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.utils.TestUtils;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */

@Slf4j
public class GenerateAndCacheRdfUnitTestGenerator implements RdfUnitTestGenerator {

  private final RdfUnitTestGenerator rdfUnitTestGenerator;
  private final String testFolder;

  public GenerateAndCacheRdfUnitTestGenerator(RdfUnitTestGenerator rdfUnitTestGenerator, String testFolder) {
    this.rdfUnitTestGenerator = rdfUnitTestGenerator;
    this.testFolder = testFolder;
  }

  @Override
  public Collection<? extends GenericTestCase> generate(TestSource source) {
    Collection<GenericTestCase> tests = new ArrayList<>(rdfUnitTestGenerator.generate(source));
    if (!tests.isEmpty()) {
      TestUtils.writeTestsToFile(tests,
          new RdfFileWriter(CacheUtils.getSourceAutoTestFile(testFolder, source)));
    }
    return tests;
  }

  @Override
  public Collection<? extends GenericTestCase> generate(SchemaSource source) {
    Collection<GenericTestCase> tests = new ArrayList<>(rdfUnitTestGenerator.generate(source));
    if (!tests.isEmpty()) {
      String filename = CacheUtils.getSourceAutoTestFile(testFolder, source);
      TestUtils.writeTestsToFile(tests, new RdfFileWriter(filename));
      log.info("tests written to {}", filename);
    }
    return tests;
  }
}