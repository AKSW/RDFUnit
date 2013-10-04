package org.aksw.databugger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.patterns.PatternUtil;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.TestAutoGenerator;
import org.aksw.databugger.tests.TestUtil;
import org.aksw.databugger.tests.UnitTest;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Dimitris Kontokostas
 * Main Class
 * Created: 9/20/13 5:59 PM
 */
public class Databugger {
    private static Logger log = LoggerFactory.getLogger(Databugger.class);

    QueryExecutionFactory patternQueryFactory;

    private List<Pattern> patterns = new ArrayList<Pattern>();
    private List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();

    Databugger() {
        this.patternQueryFactory = loadPatterns("../data/patterns.ttl", "../data/testGenerators.ttl");
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



    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        DatabuggerUtils.fillPrefixService("../data/prefixes.ttl");
        DatabuggerUtils.fillSchemaService();

        Databugger databugger = new Databugger();

        DatasetSource dataset = DatabuggerUtils.getDBpediaENDataset();
        //DatasetSource dataset = DatabuggerUtils.getDBpediaNLDataset();
        //DatasetSource dataset = DatabuggerUtils.getDatosBneEsDataset();
        //DatasetSource dataset = DatabuggerUtils.getLCSHDataset();

        dataset.setBaseCacheFolder("../data/tests/");

        List<UnitTest> allTests = new ArrayList<UnitTest>();
        for (Source s : dataset.getReferencesSchemata()) {

            log.info("Generating tests for: " + s.getUri());
            // attempt to read from file
            File f = new File(s.getTestFile());
            if (f.exists()) {
                List<UnitTest> testsAutoCached = TestUtil.instantiateTestsFromFile(s.getTestFile());
                allTests.addAll(testsAutoCached);
            } else {
                List<UnitTest> testsAuto = TestUtil.instantiateTestsFromAG(databugger.getAutoGenerators(), s);
                allTests.addAll(testsAuto);
            }


            List<UnitTest> testsManuals = TestUtil.instantiateTestsFromFile(s.getTestFileManual());
            allTests.addAll(testsManuals);
            // write to file for backup
            //TestUtil.writeTestsToFile(tests,  s.getTestFile());
        }


        TestExecutor te = new TestExecutor(dataset, allTests, 0);
        // warning, caches intermediate results
        Model model = te.executeTestsCounts("../data/results/" + dataset.getPrefix() + ".results.ttl");


        try {
            File f = new File("../data/results/" + dataset.getPrefix() + ".results.ttl");
            f.getParentFile().mkdirs();

            model.setNsPrefixes(PrefixService.getPrefixMap());
            model.write(new FileOutputStream(f), "TURTLE");
        } catch (Exception e) {
            log.error("Cannot write tests to file: ");
        }
    }


}
