package org.aksw.databugger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.patterns.PatternUtil;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.databugger.sources.EnrichedSchemaSource;
import org.aksw.databugger.sources.SchemaService;
import org.aksw.databugger.sources.SchemaSource;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.TestAutoGenerator;
import org.aksw.databugger.tests.TestUtil;
import org.aksw.databugger.tests.UnitTest;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * User: Dimitris Kontokostas
 * Main Class
 * Created: 9/20/13 5:59 PM
 */
public class Databugger {
    private static Logger log = LoggerFactory.getLogger(Databugger.class);
    private static final Options cliOptions = new Options();
    static {
        cliOptions.addOption("h", "help", false, "show this help message");
        cliOptions.addOption("d", "dataset-uri", true,
                "the URI of the dataset (required)");
        cliOptions.addOption("e", "endpoint", true,
                "the endpoint to run the tests on (required)");
        cliOptions.addOption("g", "graph", true, "the graphs to use (separate multiple graphs with ',' (defaults to '')");
        cliOptions.addOption("s", "schemas", true,
                "the schemas used in the chosen graph " +
                "(comma separated prefixes according to http://lov.okfn.org/)");
        cliOptions.addOption("i", "schema-id", true,
                "an id for this dataset (no slashes allowed), e.g. dbpedia.org");
        cliOptions.addOption("p", "prefix", true,
                "the prefix of this dataset used for caching the schema " +
                "enrichment, e.g. dbo");
        cliOptions.addOption("f", "data-folder", true, "the location of the data folder (defaults to '../data/'");

    }

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

    public QueryExecutionFactory loadPatterns(String patf, String genf) {

        Model patternModel = ModelFactory.createDefaultModel();
        try {
            patternModel.read(new FileInputStream(patf), null, "TURTLE");
            patternModel.read(new FileInputStream(genf), null, "TURTLE");
        } catch (Exception e) {
            // TODO handle exception
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
                || !commandLine.hasOption("e") || !commandLine.hasOption("g")
                || !commandLine.hasOption("i")) {
            
            if (!commandLine.hasOption("h"))
                System.out.println("\nError: Required arguments are missing.");
            
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "databugger", cliOptions );
            System.exit(1);
        }

        String datasetUri = commandLine.getOptionValue("d");
        String endpointUriStr = commandLine.getOptionValue("e");
        String graphUriStrs = commandLine.getOptionValue("g","");
        List<String> schemaUriStrs = getUriStrs(commandLine.getOptionValue("s"));
        String enrichmentCachePrefix = commandLine.getOptionValue("p");
        String schemaId = commandLine.getOptionValue("i");
        String dataFolder = commandLine.getOptionValue("f", "../data/");
        /* </cliStuff> */

        //TODO change PROPDEP to PVT

        PropertyConfigurator.configure("log4j.properties");

        DatabuggerUtils.fillPrefixService(dataFolder + "prefixes.ttl");
        DatabuggerUtils.fillSchemaService();

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
        List<SchemaSource> sources = SchemaService.getSourceList(schemaUriStrs);

        List<String> graphUris = getUriStrs(graphUriStrs);
        System.out.println(graphUris);
        // FIXME: this is just a workaround since the handling of multiple
        // graphs isn't implemented, yet
        // <workaround>
        String graphUriStr;
        if (graphUris.size()==0) graphUriStr = "";
        else graphUriStr = graphUris.get(0);
        // </workaround>
        
        //Enriched Schema (cached in folder)
        if (enrichmentCachePrefix != null)
            sources.add(new EnrichedSchemaSource(enrichmentCachePrefix, datasetUri));
        
        // String prefix, String uri, String sparqlEndpoint, String sparqlGraph, List<SchemaSource> schemata
        DatasetSource dataset = new DatasetSource(schemaId, datasetUri,
                endpointUriStr, graphUriStr, sources);		
        /* </cliStuff> */

        dataset.setBaseCacheFolder(dataFolder + "tests/");

        List<UnitTest> allTests = new ArrayList<UnitTest>();
        for (Source s : dataset.getReferencesSchemata()) {

            log.info("Generating tests for: " + s.getUri());
            // attempt to read from file
            File f = new File(s.getTestFile());
            if (f.exists()) {
                List<UnitTest> testsAutoCached = TestUtil.instantiateTestsFromFile(s.getTestFile());
                allTests.addAll(testsAutoCached);
                log.info(s.getUri() + " contains " + testsAutoCached.size() + " automatically created tests");
            } else {
                List<UnitTest> testsAuto = TestUtil.instantiateTestsFromAG(databugger.getAutoGenerators(), s);
                allTests.addAll(testsAuto);
                TestUtil.writeTestsToFile(testsAuto,  s.getTestFile());
                log.info(s.getUri() + " contains " + testsAuto.size() + " automatically created tests");
            }

            f = new File(s.getTestFileManual());
            if (f.exists()) {
                List<UnitTest> testsManuals = TestUtil.instantiateTestsFromFile(s.getTestFileManual());
                allTests.addAll(testsManuals);
                log.info(s.getUri() + " contains " + testsManuals.size() + " manually created tests");
            }
            // write to file for backup
        }

        File f = new File(dataset.getTestFileManual());
        if (f.exists()) {
            List<UnitTest> testsManuals = TestUtil.instantiateTestsFromFile(dataset.getTestFileManual());
            allTests.addAll(testsManuals);
            log.info(dataset.getUri() + " contains " + testsManuals.size() + " manually created tests");
        }

        TestExecutor te = new TestExecutor(dataset, allTests, 0);
        // warning, caches intermediate results
        Model model = te.executeTestsCounts("../data/results/" + dataset.getPrefix() + ".results.ttl");


        try {
            f = new File("../data/results/" + dataset.getPrefix() + ".results.ttl");
            f.getParentFile().mkdirs();

            model.setNsPrefixes(PrefixService.getPrefixMap());
            model.write(new FileOutputStream(f), "TURTLE");
        } catch (Exception e) {
            log.error("Cannot write tests to file: ");
        }


        // Calculate coverage


        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefixes(PrefixService.getPrefixMap());
        for (UnitTest ut: allTests) {
            m.add(ut.getUnitTestModel());
        }

        TestCoverageEvaluator tce = new TestCoverageEvaluator();
        tce.calculateCoverage(new QueryExecutionFactoryModel(m), dataset.getPrefix()+".property.count", dataset.getPrefix()+".class.count");
    }


}
