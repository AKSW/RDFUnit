package org.aksw.rdfunit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.coverage.TestCoverageEvaluator;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
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
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.executors.TestGeneratorExecutor;
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
public class ValidateCLI {
    private static final Logger log = LoggerFactory.getLogger(ValidateCLI.class);

    private static final Options cliOptions = new Options();

    static {
        cliOptions.addOption("h", "help", false, "show this help message");
        cliOptions.addOption("d", "dataset-uri", true,
                "the URI of the dataset (required)");
        cliOptions.addOption("e", "endpoint", true,
                "the endpoint to run the tests on (If no endpoint is provided RDFUnit will try to dereference the dataset-uri)");
        cliOptions.addOption("g", "graph", true, "the graphs to use (separate multiple graphs with ',' (no whitespaces) (defaults to '')");
        cliOptions.addOption("U", "uri", true, "the uri to use for dereferencing if not the same with `dataset`");
        cliOptions.addOption("s", "schemas", true,
                "the schemas used in the chosen graph " +
                        "(comma separated prefixes without whitespaces according to http://lov.okfn.org/)"
        );
        cliOptions.addOption("p", "enriched-prefix", true,
                "the prefix of this dataset used for caching the schema enrichment, e.g. dbo");
        cliOptions.addOption("ntc", "no-test-cache", false, "Do not load cached automatically generated test cases, regenerate them (Cached test cases are loaded by default)");
        cliOptions.addOption("nmt", "no-manual-tests", false, "Do not load any manually defined test cases (Manual test cases are loaded by default)");
        cliOptions.addOption("r", "result-level", true, "Specify the result level for the error reporting. One of status, aggregate, rlog, extended (default is aggregate).");
        cliOptions.addOption("l", "logging-level", true, "Not supported at the moment! will filter test cases based on logging level (notice, warn, error, etc).");
        cliOptions.addOption("c", "test-coverage", false, "Calculate test-coverage scores");
        cliOptions.addOption("f", "data-folder", true, "the location of the data folder (defaults to '../data/' or '~/.rdfunit). " +
                "If none exists, bundled versions will be loaded.'");

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
                displayHelpAndExit("Error: Required arguments are missing.");
            else
                displayHelpAndExit();
        }
        if (commandLine.hasOption("e") && commandLine.hasOption("U")) {
            displayHelpAndExit("Error: You have to select either an Endpoint or a Dump URI.");
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
        if (commandLine.hasOption("r")) {
            String rl = commandLine.getOptionValue("r", "aggregate");
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

        if (commandLine.hasOption("l")) {
            displayHelpAndExit("Option -l was changed to -r, -l is reserved for --logging-level (notice, warn, error)");
        }

        boolean calculateCoverage = commandLine.hasOption("c");
        /* </cliStuff> */


        if (!RDFUnitUtils.fileExists(dataFolder)) {
            log.error("Path : " + dataFolder + " does not exists, use -f argument");
            System.exit(1);
        }

        RDFUnitUtils.fillSchemaServiceFromLOV();
        RDFUnitUtils.fillSchemaServiceFromFile(dataFolder + "schemaDecl.csv");


        // First try to load the modified patterns, if exists, and then try the resource
        DataReader patternReader_data = new RDFStreamReader(dataFolder + "patterns.ttl");
        DataReader patternReader_resource = RDFUnitUtils.getPatternsFromResource();
        DataReader patternReader = new DataFirstSuccessReader(Arrays.asList(patternReader_data, patternReader_resource));

        // Similar to patterns
        DataReader testGeneratorReader_data = new RDFStreamReader(dataFolder + "testAutoGenerators.ttl");
        DataReader testGeneratorReader_resource = RDFUnitUtils.getAutoGeneratorsFromResource();
        DataReader testGeneratorReader = new DataFirstSuccessReader(Arrays.asList(testGeneratorReader_data, testGeneratorReader_resource));

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
        } else {
            testContext = new RDFUnitConfiguration(datasetUri,
                    endpointUriStr, graphUriStrs, sources);
        }

        final Source dataset = testContext.getDatasetSource();
        /* </cliStuff> */

        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(useTestCache, useManualTestCases);
        TestSuite testSuite = testGeneratorExecutor.generateTestSuite(testFolder, dataset, rdfunit.getAutoGenerators());
        final TestCaseExecutionType resulLevelInner = resultLevel;

        TestExecutor testExecutor = TestExecutor.initExecutorFactory(resultLevel);
        if (testExecutor == null) {
            log.error("Cannot initialize test executor. Exiting");
            System.exit(1);
        }
        SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor();
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        // warning, caches intermediate results
        testExecutor.execute(dataset, testSuite, 0);

        // Write results to DataWriter (ttl & html)
        try {
            String filename = "../data/results/" + dataset.getPrefix() + "." + resulLevelInner.toString();
            DataWriter rdf = new RDFFileWriter(filename + ".ttl");
            DataWriter html = HTMLResultsWriter.create(resulLevelInner, filename + ".html");
            DataWriter resultWriter = new DataMultipleWriter(Arrays.asList(rdf, html));

            resultWriter.write(testExecutorMonitor.getModel());
            log.info("Results stored in: " + filename + " ttl / html");
        } catch (TripleWriterException e) {
            log.error("Cannot write tests to file: " + e.getMessage());
        }


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

    private static void displayHelpAndExit(String errorMessage) {
        log.error(errorMessage);
        displayHelpAndExit();
    }

    private static void displayHelpAndExit() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("rdfunit", cliOptions);
        System.exit(1);
    }
}
