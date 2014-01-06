package org.aksw.databugger.tests;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/6/14 8:33 AM
 */
public class TestSuite {
    private final List<TestCase> testCases;

    public TestSuite(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases.clear();
        this.testCases.addAll(testCases);
    }

    public int size() {
        return testCases.size();
    }
}
