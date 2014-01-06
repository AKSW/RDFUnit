package org.aksw.databugger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.aksw.databugger.coverage.TestCoverageEvaluator;
import org.aksw.databugger.exceptions.TripleReaderException;
import org.aksw.databugger.exceptions.TripleWriterException;
import org.aksw.databugger.io.TripleFileReader;
import org.aksw.databugger.io.TripleFileWriter;
import org.aksw.databugger.io.TripleReader;
import org.aksw.databugger.io.TripleWriter;
import org.aksw.databugger.services.PrefixService;
import org.aksw.databugger.services.SchemaService;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.databugger.sources.SchemaSource;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.sources.SourceFactory;
import org.aksw.databugger.tests.TestCase;
import org.aksw.databugger.tests.executors.TestExecutor;
import org.aksw.databugger.tests.executors.TestExecutorMonitor;
import org.aksw.databugger.tests.executors.TestGeneratorExecutor;
import org.aksw.databugger.tests.results.AggregatedTestCaseResult;
import org.aksw.databugger.tests.results.TestCaseResult;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.commons.cli.*;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/19/13 10:49 AM
 */
public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static final Options cliOptions = new Options();

    static {
        cliOptions.addOption("h", "help", false, "show this help message");
        cliOptions.addOption("d", "dataset-uri", true,
                "the URI of the dataset (required)");
        cliOptions.addOption("e", "endpoint", true,
                "the endpoint to run the tests on (required)");
        cliOptions.addOption("g", "graph", true, "the graphs to use (separate multiple graphs with ',' (no whitespaces) (defaults to '')");
        cliOptions.addOption("s", "schemas", true,
                "the schemas used in the chosen graph " +
                        "(comma separated prefixes without whitespaces according to http://lov.okfn.org/)");
        cliOptions.addOption("p", "enriched-prefix", true,
                "the prefix of this dataset used for caching the schema enrichment, e.g. dbo");
        cliOptions.addOption("c", "test-coverage", false, "Calculate test-coverage scores");
        cliOptions.addOption("f", "data-folder", true, "the location of the data folder (defaults to '../data/' or '~/.databugger'");

    }

    private static List<String> getUriStrs(String parameterStr) {
        List<String> uriStrs = new ArrayList<String>();
        if (parameterStr == null) return uriStrs;

        for (String uriStr : parameterStr.split(",")) {
            uriStrs.add(uriStr.trim());
        }

        return uriStrs;
    }

    public static void main(String[] args) throws Exception {

        /* <cliStuff> */
        CommandLineParser cliParser = new GnuParser();
        CommandLine commandLine = cliParser.parse(cliOptions, args);

        if (commandLine.hasOption("h") || !commandLine.hasOption("d")
                || !commandLine.hasOption("e") || !commandLine.hasOption("g")) {

            if (!commandLine.hasOption("h"))
                System.out.println("\nError: Required arguments are missing.");

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("databugger", cliOptions);
            System.exit(1);
        }

        String datasetUri = commandLine.getOptionValue("d");
        if (datasetUri.endsWith("/"))
            datasetUri = datasetUri.substring(0, datasetUri.length() - 1);
        String endpointUriStr = commandLine.getOptionValue("e");
        String graphUriStrs = commandLine.getOptionValue("g", "");
        List<String> schemaUriStrs = getUriStrs(commandLine.getOptionValue("s"));
        String enrichedDatasetPrefix = commandLine.getOptionValue("p");
        String dataFolder = commandLine.getOptionValue("f", "../data/");
        String testFolder = dataFolder + "tests/";

        boolean calculateCoverage = commandLine.hasOption("c");
        /* </cliStuff> */


        if (!DatabuggerUtils.fileExists(dataFolder)) {
            log.error("Path : " + dataFolder + " does not exists, use -f argument");
            System.exit(1);
        }

        //TODO change PROPDEP to PVT

        PropertyConfigurator.configure("log4j.properties");

        DatabuggerUtils.fillPrefixService(dataFolder + "prefixes.ttl");

        DatabuggerUtils.fillSchemaServiceFromLOV();
        DatabuggerUtils.fillSchemaServiceFromFile(dataFolder + "schemaDecl.csv");


        TripleReader patternReader = new TripleFileReader(dataFolder + "patterns.ttl");
        TripleReader testGeneratorReader = new TripleFileReader(dataFolder + "testAutoGenerators.ttl");
        Databugger databugger = new Databugger();
        try {
            databugger.initPatternsAndGenerators(patternReader, testGeneratorReader);
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
                List<TestCase> testsAuto = TestUtils.instantiateTestsFromAG(databugger.getAutoGenerators(), s);
                TestUtils.writeTestsToFile(testsAuto,  s.getTestFile());
            }
        }
        // */

        /* <cliStuff> */
        List<SchemaSource> sources = SchemaService.getSourceList(testFolder, schemaUriStrs);


        //Enriched Schema (cached in folder)
        if (enrichedDatasetPrefix != null)
            sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(testFolder, enrichedDatasetPrefix, datasetUri));

        // String prefix, String uri, String sparqlEndpoint, String sparqlGraph, List<SchemaSource> schemata
        DatabuggerConfiguration testContext = new DatabuggerConfiguration(datasetUri,
                endpointUriStr, graphUriStrs, sources);

        final DatasetSource dataset = testContext.getDatasetSource();
        /* </cliStuff> */

        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor();
        List<TestCase> allTests = testGeneratorExecutor.generateTests(testFolder, dataset, databugger.getAutoGenerators());


        TestExecutorMonitor testExecutorMonitor = new TestExecutorMonitor() {

            TripleWriter resultWriter;
            Source testedDataset;
            Model model;
            long counter;
            long totalTests;
            long success
                    ,
                    fail
                    ,
                    timeout
                    ,
                    error
                    ,
                    totalErrors;

            @Override
            public void testingStarted(Source dataset, long numberOfTests) {
                testedDataset = dataset;
                resultWriter = new TripleFileWriter("../data/results/" + dataset.getPrefix() + ".results.ttl");
                model = ModelFactory.createDefaultModel();
                model.setNsPrefixes(PrefixService.getPrefixMap());
                counter = success = fail = timeout = error = totalErrors = 0;
                totalTests = numberOfTests;

                log.info("Testing " + testedDataset.getUri());
            }

            @Override
            public void singleTestStarted(TestCase test) {
                counter++;
            }

            @Override
            public void singleTestExecuted(TestCase test, List<TestCaseResult> results) {
                for (TestCaseResult result : results) {
                    log.info("Test " + counter + "/" + totalTests + " returned " + result.toString());
                    result.writeToModel(model, testedDataset.getUri());

                    if (result instanceof AggregatedTestCaseResult) {
                        long currentErrors = ((AggregatedTestCaseResult) result).getErrorCount();
                        if (currentErrors == -2)
                            error++;
                        if (currentErrors == -1)
                            timeout++;
                        if (currentErrors == 0)
                            success++;
                        if (currentErrors > 0) {
                            fail++;
                            totalErrors += currentErrors;
                        }
                    }

                }

                // cache intermediate results
                if (counter % 10 == 0) {
                    try {

                        resultWriter.write(model);
                    } catch (TripleWriterException e) {
                        log.error("Cannot write tests to file: " + e.getMessage());
                    }
                }

            }

            @Override
            public void testingFinished() {
                log.info("Tests run: " + totalTests + ", Failed: " + fail + ", Timeout: " + timeout + ", Error: " + error + ". Individual Errors: " + totalErrors);
                try {
                    resultWriter.write(model);
                } catch (TripleWriterException e) {
                    log.error("Cannot write tests to file: " + e.getMessage());
                }
            }
        };

        TestExecutor testExecutor = new TestExecutor();
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);


        // warning, caches intermediate results
        testExecutor.executeTestsCounts(dataset, allTests, 0);


        // Calculate coverage
        if (calculateCoverage) {
            Model m = ModelFactory.createDefaultModel();
            m.setNsPrefixes(PrefixService.getPrefixMap());
            for (TestCase ut : allTests) {
                m.add(ut.getUnitTestModel());
            }

            TestCoverageEvaluator tce = new TestCoverageEvaluator();
            tce.calculateCoverage(new QueryExecutionFactoryModel(m), dataset.getPrefix() + ".property.count", dataset.getPrefix() + ".class.count");
        }
    }
}
