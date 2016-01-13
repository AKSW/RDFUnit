package org.aksw.rdfunit.model.impl.results;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;


public class TestExecutionImpl implements TestExecution {

    private final Resource element;
    private final DatasetOverviewResults datasetOverviewResults;

    private final String testedDatasetUri;
    private final String testSuiteUri;
    private final TestCaseExecutionType testCaseExecutionType;

    private final String startedByAgent;

    private final Collection<TestCaseResult> results;
    private final Collection<String> schemata;

    private TestExecutionImpl(Builder builder) {
        this.element = checkNotNull(builder.element);
        this.datasetOverviewResults = checkNotNull(builder.datasetOverviewResults);
        this.testedDatasetUri = checkNotNull(builder.testedDatasetUri);
        this.testSuiteUri = checkNotNull(builder.testSuiteUri);
        this.testCaseExecutionType = checkNotNull(builder.testCaseExecutionType);
        this.startedByAgent = checkNotNull(builder.startedByAgent);
        this.results = ImmutableList.copyOf(checkNotNull(builder.results));
        this.schemata= ImmutableList.copyOf(checkNotNull(builder.schemata));
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

    @Override
    public DatasetOverviewResults getDatasetOverviewResults() {
        return datasetOverviewResults;
    }

    @Override
    public String getTestedDatasetUri() {
        return testedDatasetUri;
    }

    @Override
    public String getTestSuiteUri() {
        return testSuiteUri;
    }

    @Override
    public TestCaseExecutionType getTestExecutionType() {
        return testCaseExecutionType;
    }

    @Override
    public String getStartedByAgent() {
        return startedByAgent;
    }

    @Override
    public Resource getResource() {
        return element;
    }

    public static class Builder {
        private Resource element;
        private DatasetOverviewResults datasetOverviewResults;
        private String executionUUID;
        private String testedDatasetUri;
        private String testSuiteUri;
        private TestCaseExecutionType testCaseExecutionType;
        private String startedByAgent = "http://localhost/";
        private Collection<TestCaseResult> results;
        private Collection<String> schemata;

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

        public Builder setTestSuiteUri(String testSuiteUri) {
            this.testSuiteUri = testSuiteUri;
            return this;
        }

        public Builder setTestCaseExecutionType(TestCaseExecutionType testCaseExecutionType) {
            this.testCaseExecutionType = testCaseExecutionType;
            return this;
        }

        public Builder setStartedByAgent(String startedByAgent) {
            this.startedByAgent = startedByAgent;
            return this;
        }

        public Builder setResults(Collection<TestCaseResult> results) {
            this.results = results;
            return this;
        }

        public Builder setSchemata(Collection<String> schemata) {
            this.schemata = schemata;
            return this;
        }

        public TestExecutionImpl build() {
            return new TestExecutionImpl(this);
        }
    }
}
