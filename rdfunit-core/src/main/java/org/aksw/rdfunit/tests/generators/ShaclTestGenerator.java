package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.shacl.ShaclModel;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */
@Slf4j
public class ShaclTestGenerator implements RdfUnitTestGenerator {

  @Override
  public Collection<? extends GenericTestCase> generate(SchemaSource source) {

    try {
      ShaclModel shaclModel = new ShaclModel(source.getModel());
      Set<GenericTestCase> tests = shaclModel.generateTestCases();
      log.info("{} generated {} SHACL-based tests", source.getUri(), tests.size());
      return tests;

    } catch (RdfReaderException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public Collection<? extends GenericTestCase> generate(TestSource source) {
    return ImmutableList.of();
  }
}
