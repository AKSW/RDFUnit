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
    private static Logger log = LoggerFactory.getLogger(Source.class);

    QueryExecutionFactory patternQueryFactory;

    private List<Pattern> patterns = new ArrayList<Pattern>();
    private List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();

    Databugger() {
        this.patternQueryFactory = loadPatterns("../data/patterns.ttl", "../data/testGenerators.ttl", "../data/prefixes.ttl");
        this.patterns = getPatterns();
        this.autoGenerators = getAutoGenerators();

        // Update pattern service
        for (Pattern pattern : patterns ) {
            PatternService.addPattern(pattern.id, pattern);
        }
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

    public static void main(String[] args) throws Exception {

        Databugger databugger = new Databugger();

        List<Source> sources = new ArrayList<Source>();
        sources.add(new SchemaSource("http://dbpedia.org/ontology/", "http://mappings.dbpedia.org/server/ontology/dbpedia.owl"));
        sources.add(new SchemaSource("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#"));
        sources.addAll(Utils.getSourcesFromLOV());


        for (Source s: sources) {
            log.info("Generating tests for: "+s.uri);
            TestUtil.writeTestsToFile(databugger.generateTestsFromAG(s),"../data/tests/auto/" + s.getRelativeFilename());
        }

    }
}
