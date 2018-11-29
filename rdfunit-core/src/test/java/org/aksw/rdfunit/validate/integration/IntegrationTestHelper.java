package org.aksw.rdfunit.validate.integration;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.generators.RdfUnitTestGenerator;
import org.aksw.rdfunit.tests.generators.ShaclTestGenerator;
import org.aksw.rdfunit.tests.generators.TagRdfUnitTestGenerator;
import org.aksw.rdfunit.tests.generators.TestGeneratorFactory;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.apache.jena.rdf.model.Model;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.aksw.rdfunit.io.reader.RdfReaderFactory.createResourceReader;
import static org.aksw.rdfunit.sources.SchemaSourceFactory.createSchemaSourceSimple;
import static org.assertj.core.api.Assertions.assertThat;


public class IntegrationTestHelper {

    @Getter private static final String resourcePrefix = "/org/aksw/rdfunit/validate/data/";

    @Getter(lazy = true) private final static TestSuite shaclTestSuite = createTestSuiteWithShacl(resourcePrefix + "shacl/shacl.constraints.ttl");
    @Getter(lazy = true) private final static SchemaSource shaclSchemaSource = createSchemaSourceSimple(resourcePrefix + "shacl/shacl.constraints.ttl");

    @Getter(lazy = true) private final static TestSuite xsdTestSuite = IntegrationTestHelper.createTestSuiteWithGenerators(RDFUnit.createWithXsdAutoGenerators(), resourcePrefix + "owl/ontology.ttl");

    @Getter(lazy = true) private final static TestSuite owlTestSuite = IntegrationTestHelper.createTestSuiteWithGenerators(RDFUnit.createWithAllGenerators(), resourcePrefix + "owl/ontology.ttl");
    @Getter(lazy = true) private final static SchemaSource owlSchemaSource = createSchemaSourceSimple(resourcePrefix + "owl/ontology.ttl");

    @Getter(lazy = true) private final static TestSuite rsTestSuite = IntegrationTestHelper.createTestSuiteWithGenerators(RDFUnit.createWithAllGenerators(), resourcePrefix + "rs/rs_constraints.ttl");
    @Getter(lazy = true) private final static SchemaSource rsSchemaSource = createSchemaSourceSimple(resourcePrefix + "rs/rs_constraints.ttl");

    @Getter(lazy = true) private final static TestSuite dspTestSuite = IntegrationTestHelper.createTestSuiteWithGenerators(RDFUnit.createWithAllGenerators(), resourcePrefix + "dsp/dsp_constraints.ttl");
    @Getter(lazy = true) private final static SchemaSource dspSchemaSource = createSchemaSourceSimple(resourcePrefix + "dsp/dsp_constraints.ttl");

    static{
        SchemaService.addSchemaDecl("xsd", "http://www.w3.org/2001/XMLSchema#", "../rdfunit-commons/src/main/resources/org/aksw/rdfunit/vocabularies/xsd.ttl");
    }

    public static TestSuite createTestSuiteWithGenerators(RDFUnit rdfUnit, String schemaSource) {
        try {
            rdfUnit.init();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
        RdfReader ontologyDSPReader = createResourceReader(schemaSource);
        SchemaSource ontologyDSPSource = createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", ontologyDSPReader);


        return new TestSuite(
                new TagRdfUnitTestGenerator(rdfUnit.getAutoGenerators()).generate(ontologyDSPSource));
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

                assertThat(overviewResults.getIndividualErrors())
                        .as("%s: Errors not as expected in %s\n see TestExecution %s", executionType, testSource, execution)
                        .isEqualTo(expectedErrors);
            }

            if (failedTestCases == -1) {
                failedTestCases = overviewResults.getFailedTests();
            } else {
                assertThat(overviewResults.getFailedTests())
                        .as("%s: Failed test cases not as expected in %s\n see TestExecution %s", executionType, testSource, execution)
                        .isEqualTo(failedTestCases);
            }

            assertThat(overviewResults.getErrorTests())
                    .as("%s: There should be no failed test cases in  %s", executionType, testSource)
                    .isEqualTo(0);
        }


    }

    @Test
    public void testEqualityAndHash() throws RdfReaderException {
        Model schemas = RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/validate/data/hybrid.ttl").read();
        RdfReader modelReader = new RdfModelReader(schemas);
        SchemaSource schemaSource = createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", modelReader);

        RDFUnit rdfunit = RDFUnit.createWithOwlAndShacl();
        rdfunit.init();
        RdfUnitTestGenerator testGenerator = TestGeneratorFactory.createAllNoCache(rdfunit.getAutoGenerators(), "./");

        Set<TestCase> ts1 = ImmutableSet.copyOf(testGenerator.generate(schemaSource));
        Set<TestCase> ts2 = ImmutableSet.copyOf(testGenerator.generate(schemaSource));

        assertThat(ts1)
                .hasSize(4)
                .hasSameSizeAs(ts2)
                .containsAll(ts2);

    }
}
