package org.aksw.databugger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.exceptions.TripleReaderException;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.patterns.PatternUtil;
import org.aksw.databugger.sources.*;
import org.aksw.databugger.tests.TestAutoGenerator;
import org.aksw.databugger.tests.TestUtil;
import org.aksw.databugger.tests.UnitTest;
import org.aksw.databugger.tripleReaders.TripleReaderFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.commons.cli.*;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Main Class
 * Created: 9/20/13 5:59 PM
 */
public class Databugger {
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
        cliOptions.addOption("f", "data-folder", true, "the location of the data folder (defaults to '../data/'");

    }

    private static Logger log = LoggerFactory.getLogger(Databugger.class);
    QueryExecutionFactory patternQueryFactory;
    private List<Pattern> patterns = new ArrayList<Pattern>();
    private List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();

    Databugger(String dataFolder) {
        this.patternQueryFactory = loadPatterns(dataFolder + "patterns.ttl", dataFolder + "testGenerators.ttl");
        this.patterns = getPatterns();

        // Update pattern service
        for (Pattern pattern : patterns) {
            PatternService.addPattern(pattern.getId(), pattern);
        }

        this.autoGenerators = getAutoGenerators();


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
            datasetUri = datasetUri.substring(0,datasetUri.length()-1);
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

        Databugger databugger = new Databugger(dataFolder);
         /*
        // Generates all tests from LOV
        for (Source s: SchemaService.getSourceListAll()) {
            s.setBaseCacheFolder("../data/tests/");
            File f = new File(s.getTestFile());
            if (!f.exists()) {
                List<UnitTest> testsAuto = TestUtil.instantiateTestsFromAG(databugger.getAutoGenerators(), s);
                TestUtil.writeTestsToFile(testsAuto,  s.getTestFile());
            }
        }
        // */

        /* <cliStuff> */
        List<SchemaSource> sources = SchemaService.getSourceList(testFolder, schemaUriStrs);


        //Enriched Schema (cached in folder)
        if (enrichedDatasetPrefix != null)
            sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(testFolder, enrichedDatasetPrefix, datasetUri));

        // String prefix, String uri, String sparqlEndpoint, String sparqlGraph, List<SchemaSource> schemata
        DatasetSource dataset = new DatasetSource(datasetUri.replace("http://", ""), datasetUri,
                endpointUriStr, graphUriStrs, sources);
        /* </cliStuff> */

        dataset.setBaseCacheFolder(testFolder);

        List<UnitTest> allTests = new ArrayList<UnitTest>();
        for (Source s : dataset.getReferencesSchemata()) {

            log.info("Generating tests for: " + s.getUri());

            // attempt to read from file
            try {
                List<UnitTest> testsAutoCached = TestUtil.instantiateTestsFromModel(
                        TripleReaderFactory.createTripleFileReader(s.getTestFile()).read());
                allTests.addAll(testsAutoCached);
                log.info(s.getUri() + " contains " + testsAutoCached.size() + " automatically created tests (loaded from cache)");

            } catch (TripleReaderException e){
                // cannot read from file  / generate
                List<UnitTest> testsAuto = TestUtil.instantiateTestsFromAG(databugger.getAutoGenerators(), s);
                allTests.addAll(testsAuto);
                TestUtil.writeTestsToFile(testsAuto, s.getTestFile());
                log.info(s.getUri() + " contains " + testsAuto.size() + " automatically created tests");
            }

            try {
                List<UnitTest> testsManuals = TestUtil.instantiateTestsFromModel(
                        TripleReaderFactory.createTripleFileReader(s.getTestFileManual()).read());
                allTests.addAll(testsManuals);
                log.info(s.getUri() + " contains " + testsManuals.size() + " manually created tests");
            } catch (TripleReaderException e){
                // Do nothing, Manual tests do not exist
            }
            // write to file for backup
        }

        try {
            List<UnitTest> testsManuals = TestUtil.instantiateTestsFromModel(
                    TripleReaderFactory.createTripleFileReader(dataset.getTestFileManual()).read());
            allTests.addAll(testsManuals);
            log.info(dataset.getUri() + " contains " + testsManuals.size() + " manually created tests");
        } catch (TripleReaderException e){
            // Do nothing, Manual tests do not exist
        }

        TestExecutor te = new TestExecutor(dataset, allTests, 0);
        // warning, caches intermediate results
        Model model = te.executeTestsCounts("../data/results/" + dataset.getPrefix() + ".results.ttl");


        try {
            File f = new File("../data/results/" + dataset.getPrefix() + ".results.ttl");
            f.getParentFile().mkdirs();

            model.setNsPrefixes(PrefixService.getPrefixMap());
            DatabuggerUtils.writeModelToFile(model, "TURTLE", f, true);
        } catch (Exception e) {
            log.error("Cannot write tests to file: ");
        }


        // Calculate coverage
        if (calculateCoverage) {
            Model m = ModelFactory.createDefaultModel();
            m.setNsPrefixes(PrefixService.getPrefixMap());
            for (UnitTest ut : allTests) {
                m.add(ut.getUnitTestModel());
            }

            TestCoverageEvaluator tce = new TestCoverageEvaluator();
            tce.calculateCoverage(new QueryExecutionFactoryModel(m), dataset.getPrefix() + ".property.count", dataset.getPrefix() + ".class.count");
        }
    }

    public QueryExecutionFactory loadPatterns(String patf, String genf) {

        Model patternModel = ModelFactory.createDefaultModel();
        try {
            patternModel.read(new FileInputStream(patf), null, "TURTLE");
            patternModel.read(new FileInputStream(genf), null, "TURTLE");
        } catch (Exception e) {
            log.error("patterns and generators files were not found in data folder");
            System.exit(1);
        }
        patternModel.setNsPrefixes(PrefixService.getPrefixMap());
        return new QueryExecutionFactoryModel(patternModel);
    }

    public List<Pattern> getPatterns() {
        return PatternUtil.instantiatePatternsFromModel(patternQueryFactory);
    }

    public List<TestAutoGenerator> getAutoGenerators() {
        return TestUtil.instantiateTestGeneratorsFromModel(patternQueryFactory);
    }


}
