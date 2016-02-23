package org.aksw.rdfunit.model.impl.results;

import com.google.common.collect.ImmutableList;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


public class TestExecutionImpl implements TestExecution {

    private final Resource element;
    private final DatasetOverviewResults datasetOverviewResults;

    private final String testedDatasetUri;
    //private final TestSuite testSuite;
    private final TestCaseExecutionType testCaseExecutionType;

    private final String startedByAgent;

    private final Collection<TestCaseResult> results;
    private final Collection<String> schemata;
    //private final Set<String> testCaseUris;

    private TestExecutionImpl(Builder builder) {
        this.element = checkNotNull(builder.element, "Element is needed in TestExecution");
        this.testedDatasetUri = checkNotNull(builder.testedDatasetUri, "Tested dataset URI is needed in TestExecution");
        //this.testSuite = checkNotNull(builder.testSuite, "TestSuite URI is needed in TestExecution");
        this.testCaseExecutionType = checkNotNull(builder.testCaseExecutionType, "Test execution type is needed in TestExecution");
        this.startedByAgent = checkNotNull(builder.startedByAgent, "Agent starting the execution is needed in TestExecution");

        this.schemata= ImmutableList.copyOf(checkNotNull(builder.schemata, "Used schemata are needed in TestExecution"));
        this.results = ImmutableList.copyOf(checkNotNull(builder.results, "Results are needed in TestExecution"));
        //this.testCaseUris = ImmutableSet.copyOf(checkNotNull(builder.testCaseUris));

        this.datasetOverviewResults = checkNotNull(builder.datasetOverviewResults, "Overview results are needed in TestExecution");

        // TODO sometimes teh following fails, not important for now
        //checkState(testCaseUris.size() == datasetOverviewResults.getTotalTests(), "Number of tests run (" + testCaseUris.size() + ") is not the same with the number of tests in the TestSuite: " + datasetOverviewResults.getTotalTests());
    }

    @Override
    public String getTestExecutionUri() {
        return element.getURI();
    }


    @Override
    public Collection<TestCaseResult> getTestCaseResults() {
        return results;
    }

    @Override
    public Collection<String> getSchemataUris() {
        return schemata;
    }

    //@Override
    //public Collection<String> getTestCasesUris() {
    //    return testCaseUris;
    //}

    @Override
    public DatasetOverviewResults getDatasetOverviewResults() {
        return datasetOverviewResults;
    }

    @Override
    public String getTestedDatasetUri() {
        return testedDatasetUri;
    }

    /*@Override
    public TestSuite getTestSuite() {
        return testSuite;
    }   */

    @Override
    public TestCaseExecutionType getTestExecutionType() {
        return testCaseExecutionType;
    }

    @Override
    public String getStartedByAgent() {
        return startedByAgent;
    }

    @Override
    public Resource getElement() {
        return element;
    }

    public static class Builder {
        private Resource element;
        private DatasetOverviewResults datasetOverviewResults;
        private String executionUUID;
        private String testedDatasetUri;
        //private TestSuite testSuite;
        private TestCaseExecutionType testCaseExecutionType;
        private String startedByAgent = "http://localhost/";
        private Set<TestCaseResult> results = new HashSet<>();
        private Set<String> schemata = new HashSet<>();
        private Set<String> testCaseUris = new HashSet<>();

        public Builder setElement(Resource element) {
            this.element = element;
            return this;
        }

        public Builder setDatasetOverviewResults(DatasetOverviewResults datasetOverviewResults) {
            this.datasetOverviewResults = datasetOverviewResults;
            return this;
        }

        public Builder setExecutionUUID(String executionUUID) {
            this.executionUUID = executionUUID;
            return this;
        }

        public Builder setTestedDatasetUri(String testedDatasetUri) {
            this.testedDatasetUri = testedDatasetUri;
            return this;
        }

        /*public Builder setTestSuite(TestSuite testSuite) {
            this.testSuite = testSuite;
            return this;
        } */

        public Builder setTestCaseExecutionType(TestCaseExecutionType testCaseExecutionType) {
            this.testCaseExecutionType = testCaseExecutionType;
            return this;
        }

        public Builder setStartedByAgent(String startedByAgent) {
            this.startedByAgent = startedByAgent;
            return this;
        }

        public Builder setResults(Collection<TestCaseResult> results) {
            this.results.addAll(results);
            return this;
        }

        public Builder setSchemata(Collection<String> schemata) {
            this.schemata.addAll(schemata);
            return this;
        }

        public Builder setSchema(String schema) {
            this.schemata.add(schema);
            return this;
        }

        public Builder setTestCaseUris(Collection<String> testCaseUris) {
            this.testCaseUris.addAll(testCaseUris);
            return this;
        }

        public Builder setTestCaseUri(String testCaseUri) {
            this.testCaseUris.add(testCaseUri);
            return this;
        }

        public TestExecutionImpl build() {
            return new TestExecutionImpl(this);
        }
    }
}
