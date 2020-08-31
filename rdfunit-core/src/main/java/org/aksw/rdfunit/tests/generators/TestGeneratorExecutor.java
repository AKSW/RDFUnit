package org.aksw.rdfunit.tests.generators;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.generators.monitors.TestGeneratorExecutorMonitor;


/**
 * handles test generation form a schema or a cache
 *
 * @author Dimitris Kontokostas
 * @since 11/20/13 7:31 PM
 */
@Slf4j
public class TestGeneratorExecutor {

  private final boolean loadFromCache;
  private final boolean useManualTests;
  private final boolean useAutoTests;
  private final Collection<TestGeneratorExecutorMonitor> progressMonitors = new ArrayList<>();
  private volatile boolean isCanceled = false;

  public TestGeneratorExecutor() {
    this(true, true, true);
  }

  /**
   * TODO: loadFromCache does not make sense if useAutoTests is false
   */
  public TestGeneratorExecutor(boolean useAutoTests, boolean loadFromCache,
      boolean useManualTests) {
    this.useAutoTests = useAutoTests;
    this.loadFromCache = loadFromCache;
    this.useManualTests = useManualTests;

    // no auto && no manual tests do not make sense
    checkArgument(useAutoTests || useManualTests);

    // no auto && cache does not make sense TODO fix this
    checkArgument(useAutoTests || !loadFromCache);
  }

  public void cancel() {
    isCanceled = true;
  }


  public TestSuite generateTestSuite(String testFolder, TestSource dataset,
      Collection<TestGenerator> autoGenerators) {

    RdfUnitTestGenerator testGenerator = TestGeneratorFactory
        .create(autoGenerators, testFolder, useAutoTests, loadFromCache, useManualTests,
            progressMonitors);
    Collection<SchemaSource> sources = dataset.getReferencesSchemata();


    /*notify start of testing */
    for (TestGeneratorExecutorMonitor monitor : progressMonitors) {
      monitor.generationStarted(dataset, sources.size());
    }

    Collection<GenericTestCase> allTests = new ArrayList<>();
    for (SchemaSource s : sources) {
      if (isCanceled) {
        break;
      }

      allTests.addAll(testGenerator.generate(s));

    }

    //Find manual tests for dataset (if not canceled
    if (!isCanceled) {
      allTests.addAll(testGenerator.generate(dataset));
    }

    /*notify start of testing */
    progressMonitors.forEach(TestGeneratorExecutorMonitor::generationFinished);

    return new TestSuite(allTests);
  }

  public void addTestExecutorMonitor(TestGeneratorExecutorMonitor monitor) {

    if (!progressMonitors.contains(monitor)) {
      progressMonitors.add(monitor);
    }
  }

  public void removeTestExecutorMonitor(TestGeneratorExecutorMonitor monitor) {
    progressMonitors.remove(monitor);
  }
}
