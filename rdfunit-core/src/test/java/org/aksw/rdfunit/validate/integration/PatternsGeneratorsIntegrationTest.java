package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.generators.ShaclTestGenerator;
import org.aksw.rdfunit.tests.generators.TestGeneratorTCInstantiator;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PatternsGeneratorsIntegrationTest {

    private static final String resourcePrefix = "/org/aksw/rdfunit/validate/data/";
    private final RDFUnit rdfUnit = new RDFUnit();

    @Before
    public void setUp() throws Exception {
        // Init RDFUnit
        rdfUnit.init();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOWL() throws Exception {
        Map<String, Integer> testsWithErrorsOWL = new HashMap<>();

        testsWithErrorsOWL.put("owl/OWLDISJC_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/OWLDISJC_Wrong.ttl", 6);
        testsWithErrorsOWL.put("owl/RDFSRANGE_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/RDFSRANGE_Wrong.ttl", 1);
        testsWithErrorsOWL.put("owl/RDFSRANGE-MISS_Wrong.ttl", 1);
        testsWithErrorsOWL.put("owl/RDFSRANGED_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/RDFSRANGED_Wrong.ttl", 2);
        testsWithErrorsOWL.put("owl/INVFUNC_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/INVFUNC_Wrong.ttl", 2);
        testsWithErrorsOWL.put("owl/OWLCARDT_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/OWLCARDT_Wrong_Exact.ttl", 6);
        testsWithErrorsOWL.put("owl/OWLCARDT_Wrong_Min.ttl", 2);
        testsWithErrorsOWL.put("owl/OWLCARDT_Wrong_Max.ttl", 2);
        testsWithErrorsOWL.put("owl/OWLQCARDT_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/OWLQCARDT_Wrong_Exact.ttl", 6);
        testsWithErrorsOWL.put("owl/OWLQCARDT_Wrong_Min.ttl", 2);
        testsWithErrorsOWL.put("owl/OWLQCARDT_Wrong_Max.ttl", 2);
        testsWithErrorsOWL.put("owl/RDFLANGSTRING_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/RDFLANGSTRING_Wrong.ttl", 2);
        testsWithErrorsOWL.put("owl/RDFSRANG_LIT_Correct.ttl", 0);
        testsWithErrorsOWL.put("owl/RDFSRANG_LIT_Wrong.ttl", 3);

        RdfReader ontologyOWLReader = RdfReaderFactory.createResourceReader(resourcePrefix + "owl/ontology.ttl");
        SchemaSource ontologyOWLSource = SchemaSourceFactory.createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", ontologyOWLReader);

        TestSuite testSuite = new TestSuite(
                new TestGeneratorTCInstantiator(rdfUnit.getAutoGenerators(), ontologyOWLSource).generate());

        testMap(testsWithErrorsOWL, testSuite, ontologyOWLSource);
    }

    @Test
    public void testRS() throws Exception {
        Map<String, Integer> testsWithErrorsRS = new HashMap<>();

        testsWithErrorsRS.put("rs/valueType_Correct.ttl", 0);
        testsWithErrorsRS.put("rs/valueType_Wrong.ttl", 11);
        testsWithErrorsRS.put("rs/occurs_Correct.ttl", 0);
        testsWithErrorsRS.put("rs/occurs_Wrong.ttl", 4);
        testsWithErrorsRS.put("rs/range_Correct.ttl", 0);
        testsWithErrorsRS.put("rs/range_Wrong.ttl", 4);
        testsWithErrorsRS.put("rs/allowedValue_Correct.ttl", 0);
        testsWithErrorsRS.put("rs/allowedValue_Wrong.ttl", 9);


        RdfReader ontologyRSReader = RdfReaderFactory.createResourceReader(resourcePrefix + "rs/rs_constraints.ttl");
        SchemaSource ontologyRSSource = SchemaSourceFactory.createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", ontologyRSReader);

        TestSuite testSuite = new TestSuite(
                new TestGeneratorTCInstantiator(rdfUnit.getAutoGenerators(), ontologyRSSource).generate());

        testMap(testsWithErrorsRS, testSuite, ontologyRSSource);
    }

    @Test
    public void testDSP() throws Exception {

        Map<String, Integer> testsWithErrorsDSP = new HashMap<>();

        testsWithErrorsDSP.put("dsp/standalone_class_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/standalone_class_Wrong.ttl", 1);
        testsWithErrorsDSP.put("dsp/property_cardinality_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/property_cardinality_Wrong.ttl", 5);
        testsWithErrorsDSP.put("dsp/valueClass_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/valueClass_Wrong.ttl", 1);
        testsWithErrorsDSP.put("dsp/valueClass-miss_Wrong.ttl", 1);
        testsWithErrorsDSP.put("dsp/languageOccurrence_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/languageOccurrence_Wrong.ttl", 2);
        testsWithErrorsDSP.put("dsp/language_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/language_Wrong.ttl", 3);
        testsWithErrorsDSP.put("dsp/isLiteral_Wrong.ttl", 1);
        testsWithErrorsDSP.put("dsp/literal_Correct.ttl", 0);
        testsWithErrorsDSP.put("dsp/literal_Wrong.ttl", 4);

        RdfReader ontologyDSPReader = RdfReaderFactory.createResourceReader(resourcePrefix + "dsp/dsp_constraints.ttl");
        SchemaSource ontologyDSPSource = SchemaSourceFactory.createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", ontologyDSPReader);


        TestSuite testSuite = new TestSuite(
                new TestGeneratorTCInstantiator(rdfUnit.getAutoGenerators(), ontologyDSPSource).generate());

        testMap(testsWithErrorsDSP, testSuite, ontologyDSPSource);
    }

    @Test
    public void testSHACL() throws Exception {

        Map<String, Integer> testsWithErrorsShacl = new HashMap<>();

        testsWithErrorsShacl.put("shacl/sh.class-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.class-wrong.ttl", 2);
        testsWithErrorsShacl.put("shacl/sh.directType-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.directType-wrong.ttl", 2);
        testsWithErrorsShacl.put("shacl/sh.datatype-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.datatype-wrong.ttl", 2);

        testsWithErrorsShacl.put("shacl/sh.equals-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.equals-wrong.ttl", 4);
        testsWithErrorsShacl.put("shacl/sh.notEquals-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.notEquals-wrong.ttl", 4);

        testsWithErrorsShacl.put("shacl/sh.hasValue-In-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.hasValue-In-wrong.ttl", 4);

        testsWithErrorsShacl.put("shacl/sh.min.maxCount-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.min.maxCount-wrong.ttl", 4); //TODO should be 5

        testsWithErrorsShacl.put("shacl/sh.min.maxLength-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.min.maxLength-wrong.ttl", 2);
        testsWithErrorsShacl.put("shacl/sh.min.maxInclusive-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.min.maxInclusive-wrong.ttl", 2);
        testsWithErrorsShacl.put("shacl/sh.min.maxExclusive-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.min.maxExclusive-wrong.ttl", 2);

        testsWithErrorsShacl.put("shacl/sh.pattern-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.pattern-wrong.ttl", 3);


        //testsWithErrorsShacl.put("shacl/sh.nodeType-correct.ttl", 0);
        testsWithErrorsShacl.put("shacl/sh.nodeType-wrong-IRI.ttl", 2);
        testsWithErrorsShacl.put("shacl/sh.nodeType-wrong-Literal.ttl", 2);
        testsWithErrorsShacl.put("shacl/sh.nodeType-wrong-BlankNode.ttl", 2);


        RdfReader ontologyShaclReader = new RdfModelReader(RdfReaderFactory.createResourceReader(resourcePrefix + "shacl/shacl.constraints.ttl").read());
        SchemaSource ontologyShaclSource = SchemaSourceFactory.createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", ontologyShaclReader);


        TestSuite testSuite = new TestSuite(
                new ShaclTestGenerator().generate(ontologyShaclSource));

        testMap(testsWithErrorsShacl, testSuite, ontologyShaclSource);
    }

    private void testMap(Map<String, Integer> datasets, TestSuite testSuite, SchemaSource ontologySource) throws RdfReaderException {

        for (Map.Entry<String, Integer> entry : datasets.entrySet()) {
            String resource = resourcePrefix + entry.getKey();
            int errors = entry.getValue();

            // Test all execution types
            long failedTestCases = -1;
            for (TestCaseExecutionType executionType : TestCaseExecutionType.values()) {

                // create new dataset for current entry
                final TestSource modelSource = new TestSourceBuilder()
                        .setImMemSingle()
                        .setPrefixUri("test", resource)
                        .setInMemReader(new RdfModelReader(RdfReaderFactory.createResourceReader( resource).read()))
                        .setReferenceSchemata(ontologySource)
                        .build();

                TestExecution execution = RDFUnitStaticValidator.validate(executionType, modelSource, testSuite);
                DatasetOverviewResults overviewResults = execution.getDatasetOverviewResults();

                // For status results we don't get violation instances
                if (!executionType.equals(TestCaseExecutionType.statusTestCaseResult)) {
                    assertEquals(executionType + ": Errors not as expected for " + resource, errors, overviewResults.getIndividualErrors());
                }

                if (failedTestCases == -1) {
                    failedTestCases = overviewResults.getFailedTests();
                } else {
                    assertEquals(executionType + ": Failed test cases not as expected for " + resource, failedTestCases, overviewResults.getFailedTests());
                }

                assertEquals(executionType + ": There should be no failed test cases for " + resource, 0, overviewResults.getErrorTests());
            }

        }
    }
}