package org.aksw.rdfunit.model.interfaces.results;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.Element;

import java.util.Collection;

public interface TestExecution extends Element {

    String getTestExecutionUri();

    Collection<TestCaseResult> getTestCaseResults();
    Collection<String> getSchemataUris();
    //Collection<String> getTestCasesUris();

    DatasetOverviewResults getDatasetOverviewResults();

    String getTestedDatasetUri();
    //TestSuite getTestSuite();
    TestCaseExecutionType getTestExecutionType();

    String getStartedByAgent();
}
