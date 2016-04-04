package org.aksw.rdfunit.validate.integration;

import lombok.Getter;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.generators.ShaclTestGenerator;
import org.aksw.rdfunit.tests.generators.TestGeneratorTCInstantiator;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;

import java.util.Collections;

import static org.aksw.rdfunit.io.reader.RdfReaderFactory.createResourceReader;
import static org.aksw.rdfunit.sources.SchemaSourceFactory.createSchemaSourceSimple;
import static org.junit.Assert.assertEquals;

public class IntegrationTestHelper {

    @Getter private static final String resourcePrefix = "/org/aksw/rdfunit/validate/data/";

    @Getter(lazy = true) private final static TestSuite shaclTestSuite = createTestSuiteWithShacl(resourcePrefix + "shacl/shacl.constraints.ttl");
    @Getter(lazy = true) private final static SchemaSource shaclSchemaSource = createSchemaSourceSimple(resourcePrefix + "shacl/shacl.constraints.ttl");

    @Getter(lazy = true) private final static TestSuite owlTestSuite = IntegrationTestHelper.createTestSuiteWithGenerators(new RDFUnit(), resourcePrefix + "owl/ontology.ttl");
    @Getter(lazy = true) private final static SchemaSource owlSchemaSource = createSchemaSourceSimple(resourcePrefix + "owl/ontology.ttl");

    @Getter(lazy = true) private final static TestSuite rsTestSuite = IntegrationTestHelper.createTestSuiteWithGenerators(new RDFUnit(), resourcePrefix + "rs/rs_constraints.ttl");
    @Getter(lazy = true) private final static SchemaSource rsSchemaSource = createSchemaSourceSimple(resourcePrefix + "rs/rs_constraints.ttl");

    @Getter(lazy = true) private final static TestSuite dspTestSuite = IntegrationTestHelper.createTestSuiteWithGenerators(new RDFUnit(), resourcePrefix + "dsp/dsp_constraints.ttl");
    @Getter(lazy = true) private final static SchemaSource dspSchemaSource = createSchemaSourceSimple(resourcePrefix + "dsp/dsp_constraints.ttl");

    public static TestSuite createTestSuiteWithGenerators(RDFUnit rdfUnit, String schemaSource) {
        try {
            rdfUnit.init();
        } catch (RdfReaderException e) {
            throw new IllegalStateException(e);
        }
        RdfReader ontologyDSPReader = createResourceReader(schemaSource);
        SchemaSource ontologyDSPSource = createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", ontologyDSPReader);


        return new TestSuite(
                new TestGeneratorTCInstantiator(rdfUnit.getAutoGenerators(), ontologyDSPSource).generate());
    }

    public static TestSuite createTestSuiteWithShacl(String schemaSource) {
        RdfReader ontologyShaclReader = null;
        try {
            ontologyShaclReader = new RdfModelReader(createResourceReader(schemaSource).read());
        } catch (RdfReaderException e) {
            throw new IllegalArgumentException(e);
        }
        SchemaSource ontologyShaclSource = createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", ontologyShaclReader);
        return new TestSuite(new ShaclTestGenerator().generate(ontologyShaclSource));
    }

    public static void testMap(String testSource, int expectedErrors, TestSuite testSuite, SchemaSource schemaSource) throws RdfReaderException {


        // create new dataset for current entry
        final TestSource modelSource = new TestSourceBuilder()
                .setImMemSingle()
                .setPrefixUri("test", testSource)
                .setInMemReader(new RdfModelReader(createResourceReader(testSource).read()))
                .setReferenceSchemata(Collections.singletonList(schemaSource))
                .build();

        // Test all execution types
        long failedTestCases = -1;
        for (TestCaseExecutionType executionType : TestCaseExecutionType.values()) {


            TestExecution execution = RDFUnitStaticValidator.validate(executionType, modelSource, testSuite);
            DatasetOverviewResults overviewResults = execution.getDatasetOverviewResults();

            // For status results we don't get violation instances
            if (!executionType.equals(TestCaseExecutionType.statusTestCaseResult)) {
                assertEquals(executionType + ": Errors not as expected in " + testSource, expectedErrors, overviewResults.getIndividualErrors());
            }

            if (failedTestCases == -1) {
                failedTestCases = overviewResults.getFailedTests();
            } else {
                assertEquals(executionType + ": Failed test cases not as expected in " + testSource, failedTestCases, overviewResults.getFailedTests());
            }

            assertEquals(executionType + ": There should be no failed test cases in " + testSource, 0, overviewResults.getErrorTests());
        }


    }
}