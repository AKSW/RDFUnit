package org.aksw.rdfunit.validate.cli;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.coverage.TestCoverageEvaluator;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.io.*;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.utils.ValidateUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/19/13 10:49 AM
 */
public class ValidateCLI {
    private static final Logger log = LoggerFactory.getLogger(ValidateCLI.class);


    public static void main(String[] args) throws Exception {

        CommandLine commandLine = ValidateUtils.parseArguments(args);

        if (commandLine.hasOption("h")) {
            displayHelpAndExit();
        }


        String dataFolder = commandLine.getOptionValue("f", "../data/");
        RDFUnitUtils.fillSchemaServiceFromLOV();
        //TODO hack until we fix this, configuration tries to laod schemas so they must be initialized before
        RDFUnitUtils.fillSchemaServiceFromFile(dataFolder + "schemaDecl.csv");
        //RDFUnitUtils.fillSchemaServiceFromFile(configuration.getDataFolder() + "schemaDecl.csv");

        RDFUnitConfiguration configuration = null;
        try {
            configuration = ValidateUtils.getConfigurationFromArguments(commandLine);
        } catch (ParameterException e) {
            String message = e.getMessage();
            if (message != null) {
                displayHelpAndExit(message);
            } else {
                displayHelpAndExit();
            }
        }
        assert (configuration != null);

        if (!RDFUnitUtils.fileExists(configuration.getDataFolder())) {
            log.error("Path : " + configuration.getDataFolder() + " does not exists, use -f argument");
            System.exit(1);
        }

        // First try to load the modified patterns, if exists, and then try the resource
        RDFReader patternReader_data = new RDFStreamReader(configuration.getDataFolder() + "patterns.ttl");
        RDFReader patternReader_resource = RDFUnitUtils.getPatternsFromResource();
        RDFReader patternReader = new RDFFirstSuccessReader(Arrays.asList(patternReader_data, patternReader_resource));

        // Similar to patterns
        RDFReader testGeneratorReader_data = new RDFStreamReader(configuration.getDataFolder() + "testAutoGenerators.ttl");
        RDFReader testGeneratorReader_resource = RDFUnitUtils.getAutoGeneratorsFromResource();
        RDFReader testGeneratorReader = new RDFFirstSuccessReader(Arrays.asList(testGeneratorReader_data, testGeneratorReader_resource));

        RDFUnit rdfunit = new RDFUnit();
        try {
            rdfunit.initPatternsAndGenerators(patternReader, testGeneratorReader);
        } catch (TripleReaderException e) {
            displayHelpAndExit("Cannot read patterns and/or pattern generators");
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


        final Source dataset = configuration.getTestSource();
        /* </cliStuff> */

        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(
                configuration.isAutoTestsEnabled(),
                configuration.isTestCacheEnabled(),
                configuration.isManualTestsEnabled());
        TestSuite testSuite = testGeneratorExecutor.generateTestSuite(configuration.getTestFolder(), dataset, rdfunit.getAutoGenerators());


        TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(configuration.getTestCaseExecutionType());
        if (testExecutor == null) {
            displayHelpAndExit("Cannot initialize test executor. Exiting");
        }
        SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor();
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        // warning, caches intermediate results
        testExecutor.execute(dataset, testSuite, 0);


        // Write results to RDFWriter ()
        String filename = "../data/results/" + dataset.getPrefix() + "." + configuration.getTestCaseExecutionType().toString();

        ArrayList<RDFWriter> outputWriters = new ArrayList<>();
        for (SerializationFormat serializationFormat : configuration.getOutputFormats()) {
            outputWriters.add(RDFWriterFactory.createWriterFromFormat(filename, serializationFormat, configuration.getTestCaseExecutionType()));
        }

        RDFWriter resultWriter = new RDFMultipleWriter(outputWriters);
        try {
            resultWriter.write(testExecutorMonitor.getModel());
            log.info("Results stored in: " + filename + ".*");
        } catch (TripleWriterException e) {
            log.error("Cannot write tests to file: " + e.getMessage());
        }


        // Calculate coverage
        if (configuration.isCalculateCoverageEnabled()) {
            Model model = ModelFactory.createDefaultModel();
            PrefixNSService.setNSPrefixesInModel(model);
            for (TestCase ut : testSuite.getTestCases()) {
                model.add(ut.getUnitTestModel());
            }

            TestCoverageEvaluator tce = new TestCoverageEvaluator();
            tce.calculateCoverage(new QueryExecutionFactoryModel(model), dataset.getPrefix() + ".property.count", dataset.getPrefix() + ".class.count");
        }
    }

    private static void displayHelpAndExit(String errorMessage) {
        log.error(errorMessage);
        displayHelpAndExit();
    }

    private static void displayHelpAndExit() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("rdfunit", ValidateUtils.getCliOptions());
        System.exit(1);
    }
}
