package org.aksw.rdfunit.validate;

import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/13/14 4:35 PM
 */
public class ValidateUtils {
    private static final Logger log = LoggerFactory.getLogger(ValidateUtils.class);

    private static final Options cliOptions = generateCLIOptions();

    public static Options getCliOptions() {
        return cliOptions;
    }

    public static CommandLine parseArguments(String[] args) throws ParseException {
        CommandLineParser cliParser = new GnuParser();
        return cliParser.parse(getCliOptions(), args);
    }

    public static Options generateCLIOptions() {
        Options cliOptions = new Options();

        cliOptions.addOption("h", "help", false, "show this help message");
        cliOptions.addOption("d", "dataset-uri", true,
                "the URI of the dataset (required)");
        cliOptions.addOption("e", "endpoint", true,
                "the endpoint to run the tests on (If no endpoint is provided RDFUnit will try to dereference the dataset-uri)");
        cliOptions.addOption("g", "graph", true, "the graphs to use (separate multiple graphs with ',' (no whitespaces) (defaults to '')");
        cliOptions.addOption("u", "uri", true, "the uri to use for dereferencing if not the same with `dataset`");
        cliOptions.addOption("s", "schemas", true,
                "the schemas used in the chosen graph " +
                        "(comma separated prefixes without whitespaces according to http://lov.okfn.org/)"
        );
        cliOptions.addOption("o", "output-format", true, "the output format of the validation results: html (default), turtle, n3, ntriples, rdfxml, rdfxml-abbrev");
        cliOptions.addOption("p", "enriched-prefix", true,
                "the prefix of this dataset used for caching the schema enrichment, e.g. dbo");
        cliOptions.addOption("C", "no-test-cache", false, "Do not load cached automatically generated test cases, regenerate them (Cached test cases are loaded by default)");
        cliOptions.addOption("M", "no-manual-tests", false, "Do not load any manually defined test cases (Manual test cases are loaded by default)");
        cliOptions.addOption("r", "result-level", true, "Specify the result level for the error reporting. One of status, aggregate, rlog, extended (default is aggregate).");
        cliOptions.addOption("l", "logging-level", true, "Not supported at the moment! will filter test cases based on logging level (notice, warn, error, etc).");
        cliOptions.addOption("c", "test-coverage", false, "Calculate test-coverage scores");
        cliOptions.addOption("f", "data-folder", true, "the location of the data folder (defaults to '../data/' or '~/.rdfunit). " +
                "If none exists, bundled versions will be loaded.'");

        return cliOptions;
    }

    public static RDFUnitConfiguration getConfigurationFromArguments(CommandLine commandLine) throws ParameterException {

        if (!commandLine.hasOption("d") || !commandLine.hasOption("s")) {
            throw new ParameterException("Error: Required arguments are missing.");

        }
        if (commandLine.hasOption("e") && commandLine.hasOption("u")) {
            throw new ParameterException("Error: You have to select either an Endpoint or a Dump URI.");
        }

        if (commandLine.hasOption("l")) {
            throw new ParameterException("Option -l was changed to -r, -l is reserved for --logging-level (notice, warn, error)");
        }

        RDFUnitConfiguration configuration = null;

        String dataFolder = commandLine.getOptionValue("f", "../data/");

        //Dataset URI, important & required (used to associate manual dataset test cases)
        String datasetURI = commandLine.getOptionValue("d");
        if (datasetURI.endsWith("/"))
            datasetURI = datasetURI.substring(0, datasetURI.length() - 1);

        configuration = new RDFUnitConfiguration(datasetURI, dataFolder);


        // Dump location for dump dereferencing (defaults to dataset uri)
        String customDereferenceURI = commandLine.getOptionValue("u");
        if (customDereferenceURI != null && !customDereferenceURI.isEmpty()) {
            configuration.setCustomDereferenceURI(customDereferenceURI);
        }

        //Endpoint initialization
        String endpointURI = commandLine.getOptionValue("e");
        Collection<String> endpointGraphs = getUriStrs(commandLine.getOptionValue("g", ""));
        configuration.setEndpointConfiguration(endpointURI, endpointGraphs);

        try {
            //Get schema list
            Collection<String> schemaUriPrefixes = getUriStrs(commandLine.getOptionValue("s"));
            configuration.setSchemataFromPrefixes(schemaUriPrefixes);
        } catch (UndefinedSchemaException e) {
            throw new ParameterException(e.getMessage(), e);
        }

        //Get enriched schema
        String enrichedDatasetPrefix = commandLine.getOptionValue("p");
        configuration.setEnrichedSchema(enrichedDatasetPrefix);

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
        configuration.setResultLevelReporting(resultLevel);


        // Get output formats (with HTML as default)
        Collection<String> outputFormats = getUriStrs(commandLine.getOptionValue("o", "html"));
        try {
            configuration.setOutputFormatTypes(outputFormats);
        } catch (UndefinedSerializationException e) {
            throw new ParameterException(e.getMessage(), e);
        }


        // for automatically generated test cases
        boolean testCacheEnabled = !commandLine.hasOption("C");
        configuration.setTestCacheEnabled(testCacheEnabled);

        //Use only automatic tests
        boolean manualTestsEnabled = !commandLine.hasOption("M");
        configuration.setManualTestsEnabled(manualTestsEnabled);


        boolean calculateCoverage = commandLine.hasOption("c");
        configuration.setCalculateCoverageEnabled(calculateCoverage);

        return configuration;
    }


    private static Collection<String> getUriStrs(String parameterStr) {
        Collection<String> uriStrs = new ArrayList<String>();
        if (parameterStr == null) return uriStrs;

        for (String uriStr : parameterStr.split(",")) {
            if (!uriStr.trim().isEmpty())
                uriStrs.add(uriStr.trim());
        }

        return uriStrs;
    }

}
