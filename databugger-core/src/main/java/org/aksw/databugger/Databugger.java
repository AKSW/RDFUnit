package org.aksw.databugger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.impl.PrefixMappingImpl;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.patterns.PatternUtil;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.databugger.sources.SchemaSource;
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

/**
 * User: Dimitris Kontokostas
 * Main Class
 * Created: 9/20/13 5:59 PM
 */
public class Databugger {
    private static Logger log = LoggerFactory.getLogger(Databugger.class);

    private PrefixMapping prefixes = new PrefixMappingImpl();
    QueryExecutionFactory patternQueryFactory;

    private List<Pattern> patterns = new ArrayList<Pattern>();
    private List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();

    Databugger() {
        this.patternQueryFactory = loadPatterns("../data/patterns.ttl", "../data/testGenerators.ttl", "../data/prefixes.ttl");
        this.patterns = getPatterns();

        // Update pattern service
        for (Pattern pattern : patterns ) {
            PatternService.addPattern(pattern.getId(), pattern);
        }

        this.autoGenerators = getAutoGenerators();


    }

    public QueryExecutionFactory loadPatterns(String patf, String genf, String pref) {

        setPrefixes(pref);

        Model patternModel = ModelFactory.createDefaultModel();
        try {
            patternModel.read(new FileInputStream(patf), null, "TURTLE");
            patternModel.read(new FileInputStream(genf), null, "TURTLE");
        } catch (Exception e) {
            // TODO handle exception
        }
        patternModel.setNsPrefixes(getPrefixes());
        return new QueryExecutionFactoryModel(patternModel);
    }

    private void setPrefixes(String pref){

        Model prefixModel = ModelFactory.createDefaultModel();
        try {
            prefixModel.read(new FileInputStream(pref), null, "TURTLE");
        } catch (Exception e) {
            // TODO handle exception
        }

        getPrefixes().setNsPrefixes(prefixModel.getNsPrefixMap());
    }

    public List<Pattern> getPatterns(){
        return PatternUtil.instantiatePatternsFromModel(patternQueryFactory);
    }

    public List<TestAutoGenerator> getAutoGenerators(){
         return TestUtil.instantiateTestGeneratorsFromModel(patternQueryFactory);
    }

    public List<UnitTest> generateTestsFromAG(Source source){
        return  TestUtil.instantiateTestsFromAG(autoGenerators, source);
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        Databugger databugger = new Databugger();

        Source dataset = new DatasetSource("http://dbpedia.org", "http://dbpedia.org/sparql", "http://dbpedia.org", null);
        dataset.setBaseCacheFolder("../data/tests/");

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        List<Source> sources = new ArrayList<Source>();
        //sources.add(new SchemaSource("http://dbpedia.org/ontology/", "http://mappings.dbpedia.org/server/ontology/dbpedia.owl"));
        sources.add(new SchemaSource("http://xmlns.com/foaf/0.1/"));
        sources.add(new SchemaSource("http://purl.org/dc/terms/"));
        sources.add(new SchemaSource("http://purl.org/dc/elements/1.1/"));
        sources.add(new SchemaSource("http://www.w3.org/2004/02/skos/core#"));
        //sources.add(new SchemaSource("http://www.georss.org/georss/"));
        sources.add(new SchemaSource("http://www.w3.org/2003/01/geo/wgs84_pos"));
        sources.add(new SchemaSource("http://www.w3.org/ns/prov"));
        /*
        sources.add(new EnrichedSchemaSource("http://dbpedia.org"));
                                                            */
        //sources.addAll(DatabuggerUtils.getSourcesFromLOV());

        List<UnitTest> allTests = new ArrayList<UnitTest>();
        for (Source s: sources) {
            s.setBaseCacheFolder("../data/tests/");
            log.info("Generating tests for: "+ s.getUri());
            List<UnitTest> tests = databugger.generateTestsFromAG(s);
            allTests.addAll(tests);
            // write to file for backup
            TestUtil.writeTestsToFile(tests, databugger.getPrefixes(), s.getTestFile());
        }

        TestExecutor te = new TestExecutor(dataset,allTests, 500);
        Model model = te.executeTestsCounts();


        try {
            File f = new File("results.ttl");
            f.getParentFile().mkdirs();

            model.setNsPrefixes(databugger.getPrefixes());
            model.write(new FileOutputStream(f),"TURTLE");
        } catch (Exception e) {
            log.error("Cannot write tests to file: ");
        }
    }

    public PrefixMapping getPrefixes() {
        return prefixes;
    }

}
