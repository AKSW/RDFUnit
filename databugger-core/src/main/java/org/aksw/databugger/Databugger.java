package org.aksw.databugger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
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
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryBackQuery;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;

import java.io.FileInputStream;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Main Class
 * Created: 9/20/13 5:59 PM
 */
public class Databugger {

    public static void main(String[] args) throws Exception {

        Model prefixModel = ModelFactory.createDefaultModel();
        prefixModel.read(new FileInputStream("../data/prefixes.ttl"), null, "Turtle");

        PrefixMapping prefixMapping = new PrefixMappingImpl();
        prefixMapping.setNsPrefixes(prefixModel.getNsPrefixMap());




        Model patternModel = ModelFactory.createDefaultModel();
        patternModel.read(new FileInputStream("../data/patterns.ttl"), null, "Turtle");
        patternModel.read(new FileInputStream("../data/testGenerators.ttl"), null, "Turtle");
        patternModel.setNsPrefixes(prefixMapping);

        QueryExecutionFactoryBackQuery patternQueryFactory = new QueryExecutionFactoryModel(patternModel);


        for (Pattern pattern : PatternUtil.instantiatePatternsFromModel(patternQueryFactory) ) {
            PatternService.addPattern(pattern.id, pattern);
        }

        List<TestAutoGenerator> autoGenerators = TestUtil.instantiateTestGeneratorsFromModel(patternQueryFactory);

        Source source = new SchemaSource("http:dbpedia.org/ontology/", "http://mappings.dbpedia.org/server/ontology/dbpedia.owl");
        Source target = new DatasetSource("http://dbpedia.org", "http://dbpedia.org/sparql","http://dbpedia.org");

        for (TestAutoGenerator tag: autoGenerators ) {
            List<UnitTest> tests = tag.generate(source);
            for (UnitTest t : tests) {
                QueryExecution qe = target.getExecutionFactory().createQueryExecution(Utils.getAllPrefixes() + t.sparqlPrevalence);
                ResultSet results = qe.execSelect();
                while (results.hasNext()) {
                    QuerySolution qs = results.next();

                    String count = qs.get("total").toString();

                    System.out.println(count);
                }
                qe.close();
            }
        }

    }
}
