package org.aksw.rdfunit.tests.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseGroup;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.aksw.rdfunit.vocabulary.SHACL;

/**
 * Takes a dataset source and executes the test queries against the endpoint.
 *
 * @author Dimitris Kontokostas
 * @since 9 /30/13 11:11 AM
 */
@Slf4j
public abstract class TestExecutor {

  /**
   * Used to transform TestCases to SPARQL Queries
   */
  protected final QueryGenerationFactory queryGenerationFactory;
  /**
   * Collection of subscribers in the current test execution
   */
  private final Collection<TestExecutorMonitor> progressMonitors = new ArrayList<>();
  /**
   * Used in {@code cancel()} to stop the current execution
   */
  private volatile boolean isCanceled = false;

  /**
   * Instantiates a new Test executor.
   *
   * @param queryGenerationFactory a {@link org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory}
   * object.
   */
  public TestExecutor(QueryGenerationFactory queryGenerationFactory) {
    this.queryGenerationFactory = queryGenerationFactory;
  }

  /**
   * The TestCaseExecutionType the executor was implemented for
   */
  abstract TestCaseExecutionType getExecutionType();

  /**
   * Cancel the current execution. After the current query that is executed, the test execution is
   * halted
   */
  public void cancel() {
    isCanceled = true;
  }

  /**
   * Execute a generic test case.
   *
   * @param testSource the source
   * @param testCase the generic test case
   * @return the test case results
   */
  @SneakyThrows(TestCaseExecutionException.class)
  protected Collection<TestCaseResult> executeGenericTest(TestSource testSource,
      GenericTestCase testCase) {
    if (TestCase.class.isAssignableFrom(testCase.getClass())) {
      return executeSingleTest(testSource, (TestCase) testCase);
    } else if (TestCaseGroup.class.isAssignableFrom(testCase.getClass())) {
      return evaluateTestCaseGroup(testSource, (TestCaseGroup) testCase);
    } else {
      throw new UnsupportedOperationException(
          "Test execution for type " + testCase.getClass().getSimpleName()
              + " is not supported or implemented.");
    }
  }

  private Collection<TestCaseResult> evaluateTestCaseGroup(TestSource testSource,
      TestCaseGroup testCase) {
    Collection<TestCaseResult> internalResults = testCase.getTestCases().stream()
        .flatMap(gt -> executeGenericTest(testSource, gt).stream())
        .collect(Collectors.toList());

    if (testCase.getLogicalOperator() != SHACL.LogicalConstraint.atomic &&
        this.getExecutionType() != TestCaseExecutionType.shaclLiteTestCaseResult &&
        this.getExecutionType() != TestCaseExecutionType.shaclTestCaseResult) {
      log.warn(
          "Logical constraints evaluation is not supported by the current execution type: " + this
              .getExecutionType());
      return Collections.emptyList();
    } else {
      return testCase.evaluateInternalResults(internalResults);
    }
  }

  /**
   * Executes a single, explicit test case.
   *
   * @param testSource the source
   * @param testCase the test case
   * @return the test case results
   * @throws org.aksw.rdfunit.exceptions.TestCaseExecutionException the test case execution
   * exception
   */
  protected abstract Collection<TestCaseResult> executeSingleTest(TestSource testSource,
      TestCase testCase) throws TestCaseExecutionException;

  /**
   * Test execution for a Source against a TestSuite
   *
   * @param testSource the source we want to test
   * @param testSuite the test suite we test the source against
   * @return true if all TC executed successfully, false otherwise
   */
  public boolean execute(TestSource testSource, TestSuite testSuite) {
    // used to hold the whole status of the execution
    boolean success = true;

    // reset to false for this execution
    isCanceled = false;

    /*notify start of testing */
    for (TestExecutorMonitor monitor : progressMonitors) {
      monitor.testingStarted(testSource, testSuite);
    }

    for (GenericTestCase testCase : testSuite.getTestCases()) {
      if (isCanceled) {
        break;
      }

      /*notify start of single test */
      for (TestExecutorMonitor monitor : progressMonitors) {
        monitor.singleTestStarted(testCase);
      }

      Collection<TestCaseResult> results = new ArrayList<>();
      TestCaseResultStatus status;

      // Test case execution and debug logging
      long executionTimeStartInMS = System.currentTimeMillis();
      log.debug("{} : started execution", testCase.getAbrTestURI());

      try {
        results = executeGenericTest(testSource, testCase);
      } catch (RuntimeException e) {
        log.error("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
      } catch (Exception e) {
        log.error("Unknown error while executing TC: " + testCase.getAbrTestURI(), e);
        status = TestCaseResultStatus.Error;
      }

      long executionTimeEndInMS = System.currentTimeMillis();
      log.debug("{} : execution completed in {}ms", testCase.getAbrTestURI(),
          (executionTimeEndInMS - executionTimeStartInMS));

      if (results.isEmpty()) {
        status = TestCaseResultStatus.Success;
      } else if (results.size() > 1) {
        status = TestCaseResultStatus.Fail;
      } else {
        status = TestCaseResultStatus.Error; // Default
        TestCaseResult r = RDFUnitUtils.getFirstItemInCollection(results).get();
        if (r instanceof StatusTestCaseResult) {
          status = ((StatusTestCaseResult) r).getStatus();
        } else {
          if (r instanceof ShaclLiteTestCaseResult) {
            status = TestCaseResultStatus.Fail;
          }
        }
      }

      // If at least one TC fails the whole TestSuite fails
      if (status != TestCaseResultStatus.Success) {
        success = false;
      }

      /*notify end of single test */
      for (TestExecutorMonitor monitor : progressMonitors) {
        monitor.singleTestExecuted(testCase, status, results);
      }

    } // End of TC execution for loop

    /*notify end of testing */
    progressMonitors.forEach(TestExecutorMonitor::testingFinished);

    return success;
  }

  /**
   * Add test executor monitor / subscriber.
   *
   * @param monitor the monitor
   */
  public void addTestExecutorMonitor(TestExecutorMonitor monitor) {

    if (!progressMonitors.contains(monitor)) {
      progressMonitors.add(monitor);
    }
  }

  /**
   * Remove a test executor monitor  / subscriber.
   *
   * @param monitor the monitor
   */
  public void removeTestExecutorMonitor(TestExecutorMonitor monitor) {
    progressMonitors.remove(monitor);
  }

  /**
   * Clears all test executor monitors.
   */
  public void clearTestExecutorMonitor() {
    progressMonitors.clear();
  }
}
