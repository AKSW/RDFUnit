package org.aksw.rdfunit;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.coverage.TestCoverageEvaluator;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.io.*;
import org.aksw.rdfunit.services.PrefixService;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.sources.SourceFactory;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.executors.TestGeneratorExecutor;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.StatusTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.apache.commons.cli.*;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/19/13 10:49 AM
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final Options cliOptions = new Options();

    static {
        cliOptions.addOption("h", "help", false, "show this help message");
        cliOptions.addOption("d", "dataset-uri", true,
                "the URI of the dataset (required)");
        cliOptions.addOption("e", "endpoint", true,
                "the endpoint to run the tests on (If no endpoint is provided RDFUnit will try to dereference the dataset-uri)");
        cliOptions.addOption("g", "graph", true, "the graphs to use (separate multiple graphs with ',' (no whitespaces) (defaults to '')");
        cliOptions.addOption("u", "uri", true, "the uri to use for dereferencing if not the same with `dataset`");
        cliOptions.addOption("s", "schemas", true,
                "the schemas used in the chosen graph " +
                        "(comma separated prefixes without whitespaces according to http://lov.okfn.org/)");
        cliOptions.addOption("p", "enriched-prefix", true,
                "the prefix of this dataset used for caching the schema enrichment, e.g. dbo");
        cliOptions.addOption("ntc", "no-test-cache", false, "Do not load cached automatically generated test cases, regenerate them (Cached test cases are loaded by default)");
        cliOptions.addOption("nmt", "no-manual-tests", false, "Do not load any manually defined test cases (Manual test cases are loaded by default)");
        cliOptions.addOption("l", "result-level", true, "Specify the result level for the error reporting. One of status, aggregate, rlog, extended (default is aggregate).");
        cliOptions.addOption("c", "test-coverage", false, "Calculate test-coverage scores");
        cliOptions.addOption("f", "data-folder", true, "the location of the data folder (defaults to '../data/' or '~/.rdfunit'");

    }

    private static java.util.Collection<String> getUriStrs(String parameterStr) {
        java.util.Collection<String> uriStrs = new ArrayList<String>();
        if (parameterStr == null) return uriStrs;

        for (String uriStr : parameterStr.split(",")) {
            if (!uriStr.trim().isEmpty())
                uriStrs.add(uriStr.trim());
        }

        return uriStrs;
    }

    public static void main(String[] args) throws Exception {

        PropertyConfigurator.configure("log4j.properties");

        /* <cliStuff> */
        CommandLineParser cliParser = new GnuParser();
        CommandLine commandLine = cliParser.parse(cliOptions, args);

        if (commandLine.hasOption("h") || !commandLine.hasOption("d")) {
            if (!commandLine.hasOption("h"))
                System.out.println("\nError: Required arguments are missing.");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("rdfunit", cliOptions);
            System.exit(1);
        }
        if (commandLine.hasOption("e") && commandLine.hasOption("u")) {
            System.out.println("\nError: You have to select either an Endpoint or a Dump URI.");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("rdfunit", cliOptions);
            System.exit(1);
        }

        //Dataset URI, important & required (used to associate manual dataset test cases)
        String datasetUri = commandLine.getOptionValue("d");
        if (datasetUri.endsWith("/"))
            datasetUri = datasetUri.substring(0, datasetUri.length() - 1);

        // Dump location for dump dereferencing (defaults to dataset uri)
        String dumpLocation = commandLine.getOptionValue("u");
        if (dumpLocation == null || dumpLocation.isEmpty())
            dumpLocation = datasetUri;

        //Endpoint initialization
        String endpointUriStr = commandLine.getOptionValue("e");
        java.util.Collection<String> graphUriStrs = getUriStrs(commandLine.getOptionValue("g", ""));

        //Get schema list
        java.util.Collection<String> schemaUriStrs = getUriStrs(commandLine.getOptionValue("s"));

        String enrichedDatasetPrefix = commandLine.getOptionValue("p");
        String dataFolder = commandLine.getOptionValue("f", "../data/");
        String testFolder = dataFolder + "tests/";
        boolean useTestCache = !commandLine.hasOption("ntc"); // for automatically generated test cases
        boolean useManualTestCases = !commandLine.hasOption("nmt"); //Use only automatic tests

        TestCaseExecutionType resultLevel = TestCaseExecutionType.aggregatedTestCaseResult;
        if (commandLine.hasOption("l")) {
            String rl = commandLine.getOptionValue("l", "aggregate");
            if (rl.equals("status"))
                resultLevel = TestCaseExecutionType.statusTestCaseResult;
            else if (rl.equals("aggregate"))
                resultLevel = TestCaseExecutionType.aggregatedTestCaseResult;
            else if (rl.equals("rlog"))
                resultLevel = TestCaseExecutionType.rlogTestCaseResult;
            else if (rl.equals("extended"))
                resultLevel = TestCaseExecutionType.extendedTestCaseResult;
            else
                log.warn("Option --result-level defined but not recognised. Using 'aggregate' by default.");
        }

        boolean calculateCoverage = commandLine.hasOption("c");
        /* </cliStuff> */


        if (!RDFUnitUtils.fileExists(dataFolder)) {
            log.error("Path : " + dataFolder + " does not exists, use -f argument");
            System.exit(1);
        }


        RDFUnitUtils.fillPrefixService(dataFolder + "prefixes.ttl");

        RDFUnitUtils.fillSchemaServiceFromLOV();
        RDFUnitUtils.fillSchemaServiceFromFile(dataFolder + "schemaDecl.csv");


        DataReader patternReader = new RDFFileReader(dataFolder + "patterns.ttl");
        DataReader testGeneratorReader = new RDFFileReader(dataFolder + "testAutoGenerators.ttl");
        RDFUnit rdfunit = new RDFUnit();
        try {
            rdfunit.initPatternsAndGenerators(patternReader, testGeneratorReader);
        } catch (TripleReaderException e) {
            log.error("Cannot read patterns and/or pattern generators");
            System.exit(1);
        }
         /*
        // Generates all tests from LOV
        for (Source s: SchemaService.getSourceListAll()) {
            s.setBaseCacheFolder("../data/tests/");
            File f = new File(s.getTestFile());
            if (!f.exists()) {
                List<TestCase> testsAuto = TestUtils.instantiateTestsFromAG(rdfunit.getAutoGenerators(), s);
                TestUtils.writeTestsToFile(testsAuto,  s.getTestFile());
            }
        }
        // */

        /* <cliStuff> */
        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(testFolder, schemaUriStrs);


        //Enriched Schema (cached in folder)
        if (enrichedDatasetPrefix != null)
            sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(testFolder, enrichedDatasetPrefix, datasetUri));

        // String prefix, String uri, String sparqlEndpoint, String sparqlGraph, List<SchemaSource> schemata
        RDFUnitConfiguration testContext = null;
        if (endpointUriStr == null || endpointUriStr.isEmpty()) {
            testContext = new RDFUnitConfiguration(datasetUri, dumpLocation, sources);
        }
        else {
            testContext = new RDFUnitConfiguration(datasetUri,
                endpointUriStr, graphUriStrs, sources);
        }

        final Source dataset = testContext.getDatasetSource();
        /* </cliStuff> */

        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(useTestCache, useManualTestCases);
        TestSuite testSuite = testGeneratorExecutor.generateTestSuite(testFolder, dataset, rdfunit.getAutoGenerators());
        final TestCaseExecutionType resulLevelInner = resultLevel;

        TestExecutorMonitor testExecutorMonitor = new TestExecutorMonitor() {

            String filename;
            DataWriter resultWriter;
            Source testedDataset;
            TestSuite testSuite;
            Model model;
            long counter = 0;
            long totalTests = 0;
            long success = 0;
            long fail = 0;
            long timeout = 0;
            long error = 0;
            long totalErrors = 0;

            long startTimeMillis = 0;
            long endTimeMillis = 0;

            final String executionUUID = JenaUUID.generate().asString();


            @Override
            public void testingStarted(Source dataset, TestSuite testSuite) {
                testedDataset = dataset;

                filename = "../data/results/" + dataset.getPrefix() + "." + resulLevelInner.toString();
                DataWriter rdf  = new RDFFileWriter(    filename + ".ttl");
                DataWriter html = HTMLResultsWriter.create(resulLevelInner, filename + ".html");
                resultWriter = new DataMultipleWriter(Arrays.asList(rdf, html));

                model = ModelFactory.createDefaultModel();
                model.setNsPrefixes(PrefixService.getPrefixMap());
                counter = success = fail = timeout = error = totalErrors = 0;
                totalTests = testSuite.size();
                this.testSuite = testSuite;

                log.info("Testing " + testedDataset.getUri());
            }

            @Override
            public void singleTestStarted(TestCase test) {
                counter++;
                startTimeMillis = System.currentTimeMillis();
            }

            @Override
            public void singleTestExecuted(TestCase test, TestCaseResultStatus status, java.util.Collection<TestCaseResult> results) {

                if (status.equals(TestCaseResultStatus.Error))
                    error++;
                if (status.equals(TestCaseResultStatus.Timeout))
                    timeout++;
                if (status.equals(TestCaseResultStatus.Success))
                    success++;
                if (status.equals(TestCaseResultStatus.Fail))
                    fail++;

                for (TestCaseResult result : results) {
                    result.serialize(model, executionUUID);
                }

                // in case we have 1 result but is not status
                boolean statusResult = false;

                if (results.size() == 1) {

                    //Get item
                    TestCaseResult result = RDFUnitUtils.getFirstItemInCollection(results);
                    assert (result != null);

                    if (result instanceof StatusTestCaseResult) {
                        statusResult = true;

                        log.info("Test " + counter + "/" + totalTests + " returned " + result.toString());



                        if (result instanceof AggregatedTestCaseResult) {
                            long errorCount = ((AggregatedTestCaseResult) result).getErrorCount();
                            if (errorCount > 0)
                                totalErrors += ((AggregatedTestCaseResult) result).getErrorCount();
                        }
                    }
                }

                if (!statusResult) {
                    // TODO RLOG+ results
                    totalErrors += results.size();

                }

                // cache intermediate results
                //if (counter % 10 == 0) {
                //    try {
                //        resultWriter.write(model);
                //    } catch (TripleWriterException e) {
                //        log.error("Cannot write tests: " + e.getMessage());
                //    }
                //}
            }

            @Override
            public void testingFinished() {
                endTimeMillis = System.currentTimeMillis();

                Resource testSuiteResource = testSuite.serialize(model);

                model.createResource(executionUUID)
                        .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("rut") + "TestExecution"))
                        .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("prov") + "Activity"))
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("prov"), "used"), testSuiteResource)
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("prov"), "startedAtTime"),
                                ResourceFactory.createTypedLiteral("" + startTimeMillis, XSDDatatype.XSDdateTime)) //TODO convert to datetime
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("prov"), "endedAtTime"),
                                ResourceFactory.createTypedLiteral("" + endTimeMillis, XSDDatatype.XSDdateTime)) //TODO convert to datetime
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "source"),
                                model.createResource(testedDataset.getUri()))
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsRun"),
                                ResourceFactory.createTypedLiteral("" + totalTests, XSDDatatype.XSDnonNegativeInteger))
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsSuceedded"),
                                ResourceFactory.createTypedLiteral("" + success, XSDDatatype.XSDnonNegativeInteger))
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsFailed"),
                                ResourceFactory.createTypedLiteral("" + fail, XSDDatatype.XSDnonNegativeInteger))
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsTimeout"),
                                ResourceFactory.createTypedLiteral("" + timeout, XSDDatatype.XSDnonNegativeInteger))
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "testsError"),
                                ResourceFactory.createTypedLiteral("" + error, XSDDatatype.XSDnonNegativeInteger))
                        .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "totalIndividualErrors"),
                                ResourceFactory.createTypedLiteral("" + totalErrors, XSDDatatype.XSDnonNegativeInteger));

                log.info("Tests run: " + totalTests + ", Failed: " + fail + ", Timeout: " + timeout + ", Error: " + error + ". Individual Errors: " + totalErrors);
                log.info("Results stored in: " + filename + " ttl / html");
                try {
                    resultWriter.write(model);
                } catch (TripleWriterException e) {
                    log.error("Cannot write tests to file: " + e.getMessage());
                }
            }
        };

        TestExecutor testExecutor = TestExecutor.initExecutorFactory(resultLevel);
        if (testExecutor == null) {
            log.error("Cannot initialize test executor. Exiting");
            System.exit(1);
        }
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);


        // warning, caches intermediate results
        testExecutor.execute(dataset, testSuite, 0);


        // Calculate coverage
        if (calculateCoverage) {
            Model m = ModelFactory.createDefaultModel();
            m.setNsPrefixes(PrefixService.getPrefixMap());
            for (TestCase ut : testSuite.getTestCases()) {
                m.add(ut.getUnitTestModel());
            }

            TestCoverageEvaluator tce = new TestCoverageEvaluator();
            tce.calculateCoverage(new QueryExecutionFactoryModel(m), dataset.getPrefix() + ".property.count", dataset.getPrefix() + ".class.count");
        }
    }
}
