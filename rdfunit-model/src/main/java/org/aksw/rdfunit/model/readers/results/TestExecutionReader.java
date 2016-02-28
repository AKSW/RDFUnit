package org.aksw.rdfunit.model.readers.results;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.impl.results.TestExecutionImpl;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.PROV;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class TestExecutionReader implements ElementReader<TestExecution> {

    private TestExecutionReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link TestExecutionReader} object.
     */
    public static TestExecutionReader create() { return new TestExecutionReader();}

    /** {@inheritDoc} */
    @Override
    public TestExecution read(Resource resource) {
        checkNotNull(resource);

        TestExecutionImpl.Builder testExecutionBuilder = new TestExecutionImpl.Builder()
                .setElement(resource);

        TestCaseExecutionType executionType = null;

        // RDFUNITv.executionType
        for (Statement smt : resource.listProperties(RDFUNITv.executionType).toList()) {
            executionType = TestCaseExecutionType.valueOf(smt.getObject().asLiteral().getLexicalForm());
            testExecutionBuilder.setTestCaseExecutionType(executionType);
        }
        checkNotNull(executionType);

        // RDFUNITv.source
        for (Statement smt : resource.listProperties(RDFUNITv.source).toList()) {
            testExecutionBuilder.setTestedDatasetUri(smt.getObject().asResource().getURI());
        }

        // prov:wasAssociatedWith
        for (Statement smt : resource.listProperties(PROV.wasAssociatedWith).toList()) {
            testExecutionBuilder.setSchema(smt.getObject().asResource().getURI());
        }

        // prov:wasStartedBy
        for (Statement smt : resource.listProperties(PROV.wasStartedBy).toList()) {
            testExecutionBuilder.setStartedByAgent(smt.getObject().asResource().getURI());
        }

        /*
        // prov:used
        Resource testSuiteResource = null;
        for (Statement smt : resource.listProperties(PROV.used).toList()) {
            testSuiteResource = smt.getObject().asResource();
            testExecutionBuilder.setTestSuiteUri(testSuiteResource.getURI());

        }
        checkNotNull(testSuiteResource);

        // TestSuite testCaseUris;
        for (Statement smt : testSuiteResource.listProperties(PROV.hadMember).toList()) {
            testExecutionBuilder.setTestCaseUri(smt.getObject().asResource().getURI());
        }  */


        // overview results
        testExecutionBuilder.setDatasetOverviewResults(getDatasetOverviewResults(resource));

        //results
        testExecutionBuilder.setResults(getResults(resource, executionType));

        return testExecutionBuilder.build();
    }

    private DatasetOverviewResults getDatasetOverviewResults(Resource resource) {
        DatasetOverviewResults overviewResults = new DatasetOverviewResults();
        // RDFUNITv.testsError
        for (Statement smt : resource.listProperties(RDFUNITv.testsError).toList()) {
            overviewResults.setErrorTests(smt.getObject().asLiteral().getLong());
        }
        // RDFUNITv.testsFailed
        for (Statement smt : resource.listProperties(RDFUNITv.testsFailed).toList()) {
            overviewResults.setFailedTests(smt.getObject().asLiteral().getLong());
        }

        // RDFUNITv.testsRun
        for (Statement smt : resource.listProperties(RDFUNITv.testsRun).toList()) {
            overviewResults.setTotalTests(smt.getObject().asLiteral().getLong());
        }

        // RDFUNITv.testsSuceedded
        for (Statement smt : resource.listProperties(RDFUNITv.testsSuceedded).toList()) {
            overviewResults.setSuccessfulTests(smt.getObject().asLiteral().getLong());
        }

        // RDFUNITv.testsTimeout
        for (Statement smt : resource.listProperties(RDFUNITv.testsTimeout).toList()) {
            overviewResults.setTimeoutTests(smt.getObject().asLiteral().getLong());
        }

        // RDFUNITv.totalIndividualErrors
        for (Statement smt : resource.listProperties(RDFUNITv.totalIndividualErrors).toList()) {
            overviewResults.setIndividualErrors(smt.getObject().asLiteral().getLong());
        }

        // prov:endedAtTime
        for (Statement smt : resource.listProperties(PROV.endedAtTime).toList()) {
            XSDDateTime dateTime = (XSDDateTime) XSDDatatype.XSDdateTime.parse(smt.getObject().asLiteral().getLexicalForm());
            overviewResults.setEndTime(dateTime);
        }

        // prov:startedAtTime
        for (Statement smt : resource.listProperties(PROV.startedAtTime).toList()) {
            XSDDateTime dateTime = (XSDDateTime) XSDDatatype.XSDdateTime.parse(smt.getObject().asLiteral().getLexicalForm());
            overviewResults.setStartTime(dateTime);
        }
        return overviewResults;
    }

    private Collection<TestCaseResult> getResults(Resource resource, TestCaseExecutionType executionType) {
        Resource typeToSearch = null;
        ElementReader<? extends TestCaseResult> reader = null;
        // Results
        switch (executionType) {
            case statusTestCaseResult:
                typeToSearch = RDFUNITv.StatusTestCaseResult;
                reader = StatusTestCaseResultReader.create();
                break;
            case aggregatedTestCaseResult:
                typeToSearch = RDFUNITv.AggregatedTestResult;
                reader = AggregatedTestCaseResultReader.create();
                break;
            case extendedTestCaseResult:
                typeToSearch = RDFUNITv.ExtendedTestCaseResult;
                reader = ExtendedTestCaseResultReader.create();
                break;
            case rlogTestCaseResult:
                typeToSearch = RDFUNITv.RLOGTestCaseResult;
                reader = RLOGTestCaseResultReader.create();
                break;
            case shaclFullTestCaseResult:
                typeToSearch = SHACL.ValidationResult;
                reader = ShaclTestCaseResultReader.create();
                break;
            case shaclSimpleTestCaseResult:
                typeToSearch = SHACL.ValidationResult;
                reader = ShaclSimpleTestCaseResultReader.create();
                break;
            default:
                throw new IllegalArgumentException("unsupported execution type: " + executionType.toString());
        }
        checkNotNull(typeToSearch);
        checkNotNull(reader);


        Collection<TestCaseResult> results = new ArrayList<>();
        for (Resource r: resource.getModel().listResourcesWithProperty(RDF.type, typeToSearch).toList()) {
            if (r.hasProperty(PROV.wasGeneratedBy, resource)) {
                results.add(reader.read(r));
            }
        }

        return results;
    }

}
