package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;

/**
 * This composite generator is mainly used for trying out if there is a cached version of a test
 * generation available before trying to generate a new one from source with TAGs
 *
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */
public class FirstSuccessfulTestGenerator implements RdfUnitTestGenerator {

  private final ImmutableList<RdfUnitTestGenerator> generators;

  public FirstSuccessfulTestGenerator(ImmutableList<RdfUnitTestGenerator> generators) {
    this.generators = generators;
  }

  @Override
  public Collection<? extends GenericTestCase> generate(SchemaSource source) {
    return generators.stream()
        .map(g -> g.generate(source))
        .filter(l -> !l.isEmpty())
        .findFirst()
        .orElse(ImmutableList.of());

  }

  @Override
  public Collection<? extends GenericTestCase> generate(TestSource source) {
    return ImmutableList.of();
  }
}
