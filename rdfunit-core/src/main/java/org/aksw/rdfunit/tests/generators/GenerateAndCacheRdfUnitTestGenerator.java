package org.aksw.rdfunit.tests.generators;

import java.util.ArrayList;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.writer.RdfFileWriter;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.sources.CacheUtils;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.utils.TestUtils;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */

@Slf4j
public class GenerateAndCacheRdfUnitTestGenerator implements RdfUnitTestGenerator {

  private final TagRdfUnitTestGenerator tagRdfUnitTestGenerator;
  private final String testFolder;


  public GenerateAndCacheRdfUnitTestGenerator(TagRdfUnitTestGenerator tagRdfUnitTestGenerator,
      String testFolder) {
    this.tagRdfUnitTestGenerator = tagRdfUnitTestGenerator;
    this.testFolder = testFolder;
  }


  @Override
  public Collection<? extends GenericTestCase> generate(TestSource source) {
    Collection<GenericTestCase> tests = new ArrayList<>(tagRdfUnitTestGenerator.generate(source));
    cacheTests(tests, source);
    return tests;
  }

  @Override
  public Collection<? extends GenericTestCase> generate(SchemaSource source) {
    Collection<GenericTestCase> tests = new ArrayList<>(tagRdfUnitTestGenerator.generate(source));
    cacheTests(tests, source);
    return tests;
  }

  private void cacheTests(Collection<GenericTestCase> tests, Source source) {
    if (!tests.isEmpty()) {
      String filename = CacheUtils.getSourceAutoTestFile(testFolder, source);
      TestUtils.writeTestsToFile(tests, new RdfFileWriter(filename));
      log.info("{} tests written to {}", tests.size(), filename);
    }
  }
}
