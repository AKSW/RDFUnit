package org.aksw.databugger;

import com.hp.hpl.jena.query.*;
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
import org.aksw.jena_sparql_api.cache.core.QueryExecutionFactoryCacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheCoreEx;
import org.aksw.jena_sparql_api.cache.extra.CacheCoreH2;
import org.aksw.jena_sparql_api.cache.extra.CacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheExImpl;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryBackQuery;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;

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
                QueryExecution qe = target.queryFactory.createQueryExecution(Utils.getAllPrefixes() + t.sparqlPrevalence);
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

    private static void generateAllPatterns() {
          /*
        String id, desc, pattern, patternPrevalence;
        ArrayList<PatternParameter> parameters = new ArrayList<PatternParameter>();

        // RDFSDOMAIN Pattern

        id = "RDFSDOMAIN";
        desc = "Emulates the rdfs:domain constrain. " +
                "It can be extended by using specific values in V1 or making rdf:type " +
                "transitive using the '+' operator";
        pattern = "SELECT ?s where {\n" +
                "?s %%P1%% %%V1%% .\n" +
                "FILTER NOT EXISTS {?s rdf:type%%OP%% %%T1%%} .\n" +
                "}";
        patternPrevalence = "SELECT count(distinct ?s) AS ?count where {\n" +
                "?s %%P1%% %%V1%% .\n" +
                "}";
        parameters.clear();
        parameters.add(new PatternParameter("P1", 1, false, true, false, ""));
        parameters.add(new PatternParameter("V1", 2, false, false, false, ""));
        parameters.add(new PatternParameter("OP", 3, false, false, true, "{+|}"));
        parameters.add(new PatternParameter("T1", 4, true, false, false, ""));

        PatternService.addPattern(id, new Pattern(id, desc, pattern, patternPrevalence, "s", parameters));

        // RDFSRANGE

        id = "RDFSRANGE";
        desc = "Emulates the rdfs:range constrain. " +
                "It can be extended by using specific values in V1 or making rdf:type " +
                "transitive using the '+' operator";
        pattern = "SELECT ?c {\n" +
                "?s %%P1%% ?c.\n" +
                "FILTER NOT EXISTS {?c rdf:type%%OP%% %%T1%%}\n" +
                "}";
        patternPrevalence = "SELECT count(distinct ?c) AS ?count where {\n" +
                "?s %%P1%% ?c .\n" +
                "}";
        parameters.clear();
        parameters.add(new PatternParameter("P1", 1, false, true, false, ""));
        parameters.add(new PatternParameter("OP", 3, false, false, true, "{+|}"));
        parameters.add(new PatternParameter("T1", 4, true, false, false, ""));

        PatternService.addPattern(id, new Pattern(id, desc, pattern, patternPrevalence, "c", parameters));

          */
    }

}
