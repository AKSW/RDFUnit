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


    QueryExecutionFactory patternQueryFactory;
    Source source;

    private List<Pattern> patterns = new ArrayList<Pattern>();
    private List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();
    private List<UnitTest> tests = new ArrayList<UnitTest>();

    Databugger(Source source) {
        this.source = source;
        this.patternQueryFactory = loadPatterns("../data/patterns.ttl", "../data/testGenerators.ttl", "../data/prefixes.ttl");
        this.patterns = getPatterns();
        this.autoGenerators = getAutoGenerators();

        // Update pattern service
        for (Pattern pattern : patterns ) {
            PatternService.addPattern(pattern.id, pattern);
        }

        tests = generateTestsFromAG(source);
    }


    public static void main(String[] args) throws Exception {


        Source source = new SchemaSource("http://dbpedia.org/ontology/", "http://mappings.dbpedia.org/server/ontology/dbpedia.owl");
        Source target = new DatasetSource("http://dbpedia.org", "http://dbpedia.org/sparql","http://dbpedia.org");


        Source source2 = new SchemaSource("http://dbpedia.org/ontology/asdfggg#gdfgdfg", "http://mappings.dbpedia.org/server/ontology/dbpedia.owl");
        source2.getRelativeFilename();


        Databugger databugger = new Databugger(source);
        databugger.writeTestsToFile("../data/tests/" + source.getRelativeFilename());



    }

    public QueryExecutionFactory loadPatterns(String patf, String genf, String pref) {
        Model patternModel = ModelFactory.createDefaultModel();
        try {
            patternModel.read(new FileInputStream(patf), null, "TURTLE");
            patternModel.read(new FileInputStream(genf), null, "TURTLE");
        } catch (Exception e) {
            // TODO handle exception
        }
        patternModel.setNsPrefixes(getNS(pref));
        return new QueryExecutionFactoryModel(patternModel);
    }

    private PrefixMapping getNS(String pref){

        Model prefixModel = ModelFactory.createDefaultModel();
        PrefixMapping prefixMapping = new PrefixMappingImpl();
        try {
            prefixModel.read(new FileInputStream(pref), null, "TURTLE");
        } catch (Exception e) {
            // TODO handle exception
        }

        prefixMapping.setNsPrefixes(prefixModel.getNsPrefixMap());
        return prefixMapping;
    }

    public List<Pattern> getPatterns(){
        return PatternUtil.instantiatePatternsFromModel(patternQueryFactory);
    }

    public List<TestAutoGenerator> getAutoGenerators(){
         return TestUtil.instantiateTestGeneratorsFromModel(patternQueryFactory);
    }

    public List<UnitTest> generateTestsFromAG(Source source){
        return  TestUtil.isntantiateTestsFromAG(autoGenerators,source);
    }

    public void writeTestsToFile(String filename) {
        Model model = ModelFactory.createDefaultModel();
        for (UnitTest t: tests)
            t.saveTestToModel(model);
        try {
            model.write(new FileOutputStream(filename),"TURTLE");
        } catch (Exception e) {
            // TODO handle exceptions
        }
    }
}
