package org.aksw.rdfunit.model.writers.results;

import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.ElementWriter;
import org.aksw.rdfunit.vocabulary.PROV;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;


public final class TestExecutionWriter implements ElementWriter {

    private final TestExecution testExecution;

    private TestExecutionWriter(TestExecution testExecution) {
        this.testExecution = testExecution;
    }

    public static TestExecutionWriter create(TestExecution testExecution) {return new TestExecutionWriter(testExecution);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(testExecution, model);


        //Resource testSuiteResource = testSuite.serialize(getModel());


        resource.addProperty(RDF.type, PROV.Activity)
                .addProperty(RDF.type, RDFUNITv.TestExecution);

        //Test suite
        /*
        Resource testSuiteResource = testExecution.getTestSuite().serialize(model);

        testSuiteResource
                .addProperty(RDF.type, RDFUNITv.TestSuite)
                .addProperty(RDF.type, PROV.Collection);

        for (String tc: testExecution.getTestCasesUris()) {
            testSuiteResource.addProperty(PROV.hadMember, model.createResource(tc));
        }
        resource.addProperty(PROV.used, testSuiteResource);   */

        // metadata
        resource.addProperty(RDFUNITv.executionType, testExecution.getTestExecutionType().name())
                .addProperty(RDFUNITv.source, model.createResource(testExecution.getTestedDatasetUri()))
                .addProperty(PROV.wasStartedBy, model.createResource(testExecution.getStartedByAgent()));

                //dataset overview results
        resource.addProperty(PROV.startedAtTime,
                        ResourceFactory.createTypedLiteral(testExecution.getDatasetOverviewResults().getStartTime().toString(), XSDDatatype.XSDdateTime))
                .addProperty(PROV.endedAtTime,
                        ResourceFactory.createTypedLiteral(testExecution.getDatasetOverviewResults().getEndTime().toString(), XSDDatatype.XSDdateTime))
                .addProperty(RDFUNITv.testsRun,
                        ResourceFactory.createTypedLiteral("" + testExecution.getDatasetOverviewResults().getTotalTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(RDFUNITv.testsSuceedded,
                        ResourceFactory.createTypedLiteral("" + testExecution.getDatasetOverviewResults().getSuccessfulTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(RDFUNITv.testsFailed,
                        ResourceFactory.createTypedLiteral("" + testExecution.getDatasetOverviewResults().getFailedTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(RDFUNITv.testsTimeout,
                        ResourceFactory.createTypedLiteral("" + testExecution.getDatasetOverviewResults().getTimeoutTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(RDFUNITv.testsError,
                        ResourceFactory.createTypedLiteral("" + testExecution.getDatasetOverviewResults().getErrorTests(), XSDDatatype.XSDnonNegativeInteger))
                .addProperty(RDFUNITv.totalIndividualErrors,
                        ResourceFactory.createTypedLiteral("" + testExecution.getDatasetOverviewResults().getIndividualErrors(), XSDDatatype.XSDnonNegativeInteger))
                ;

        // Associate the constraints to the execution
        for (String src : testExecution.getSchemataUris()) {
            resource.addProperty( PROV.wasAssociatedWith, model.createResource(src));
        }

        // Write individual results
        for (TestCaseResult result : testExecution.getTestCaseResults()) {
            TestCaseResultWriter.create(result, testExecution.getTestExecutionUri()).write(model);
        }

        return resource;
    }
}
