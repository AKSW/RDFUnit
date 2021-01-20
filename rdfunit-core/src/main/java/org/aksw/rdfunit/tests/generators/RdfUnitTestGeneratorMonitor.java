package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.generators.monitors.TestGeneratorExecutorMonitor;

public class RdfUnitTestGeneratorMonitor implements RdfUnitTestGenerator {

  private final RdfUnitTestGenerator delegate;
  private final Collection<TestGeneratorExecutorMonitor> monitors;
  private final TestGenerationType generationType;

  public RdfUnitTestGeneratorMonitor(RdfUnitTestGenerator delegate,
      Collection<TestGeneratorExecutorMonitor> monitors, TestGenerationType generationType) {
    this.delegate = delegate;
    this.monitors = ImmutableList.copyOf(monitors);
    this.generationType = generationType;
  }

  @Override
  public Collection<? extends GenericTestCase> generate(SchemaSource source) {
    monitors.forEach(m -> m.sourceGenerationStarted(source, generationType));
    Collection<? extends GenericTestCase> tests = delegate.generate(source);
    monitors.forEach(m -> m.sourceGenerationExecuted(source, generationType, tests.size()));
    return tests;
  }

  @Override
  public Collection<? extends GenericTestCase> generate(TestSource source) {
    monitors.forEach(m -> m.sourceGenerationStarted(source, generationType));
    Collection<? extends GenericTestCase> tests = delegate.generate(source);
    monitors.forEach(m -> m.sourceGenerationExecuted(source, generationType, tests.size()));
    return tests;
  }
}
