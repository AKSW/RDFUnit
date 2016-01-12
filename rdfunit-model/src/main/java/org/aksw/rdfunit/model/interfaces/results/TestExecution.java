package org.aksw.rdfunit.model.interfaces.results;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.Element;
import org.aksw.rdfunit.model.interfaces.TestSuite;

import java.util.Collection;

public interface TestExecution extends Element {

    String getTestExecutionUri();

    Collection<TestCaseResult> getTestCaseResults();
    Collection<String> getSchemataUris();

    DatasetOverviewResults getDatasetOverviewResults();

    String getTestedDatasetUri();
    String getTestSuiteUri();
    TestCaseExecutionType getTestExecutionType();

    String getStartedByAgent();
}
