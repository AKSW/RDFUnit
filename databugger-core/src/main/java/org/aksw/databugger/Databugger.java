package org.aksw.databugger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternParameter;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.patterns.PatternUtil;
import org.aksw.databugger.sources.SchemaSource;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.TestAutoGenerator;
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
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Main Class
 * Created: 9/20/13 5:59 PM
 */
public class Databugger {

    public static void main(String[] args) throws Exception {

        Model patternModel = ModelFactory.createDefaultModel();
        patternModel.read(new FileInputStream("../data/patterns.ttl"), "", "Turtle");

        for (Pattern pattern : PatternUtil.instantiatePatternsFromModel(patternModel) ) {
            PatternService.addPattern(pattern.id, pattern);
        }


        //generateAllPatterns();

        // Create a query execution over DBpedia
        QueryExecutionFactory qef = new QueryExecutionFactoryHttp("http://dbpedia.org/sparql", "http://dbpedia.org");

// Add delay in order to be nice to the remote server (delay in milli seconds)
        qef = new QueryExecutionFactoryDelay(qef, 7000);

// Set up a cache
// Cache entries are valid for 1 day
        long timeToLive = 24l * 60l * 60l * 1000l;

// This creates a 'cache' folder, with a database file named 'sparql.db'
// Technical note: the cacheBackend's purpose is to only deal with streams,
// whereas the frontend interfaces with higher level classes - i.e. ResultSet and Model
        CacheCoreEx cacheBackend = CacheCoreH2.create("sparql", timeToLive, true);
        CacheEx cacheFrontend = new CacheExImpl(cacheBackend);
        qef = new QueryExecutionFactoryCacheEx(qef, cacheFrontend);

// Add pagination
        qef = new QueryExecutionFactoryPaginated(qef, 900);

// Create a QueryExecution object from a query string ...

        Source source = new SchemaSource("http:dbpedia.org/ontology/", "http://mappings.dbpedia.org/server/ontology/dbpedia.owl");

        String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        prefixes += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
        prefixes += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
        prefixes += "\n";
        prefixes += "\n";

        String tmpSparql = prefixes + "select distinct ?P1 ('+' as ?OP) ?T1 where {\n" +
                " ?P1 rdfs:range ?T1 . " +
                " ?T1 a owl:Class" +
                "} LIMIT 3";

        TestAutoGenerator tag = new TestAutoGenerator(tmpSparql, "RDFSRANGE");
        List<UnitTest> tests = tag.generate((QueryExecutionFactoryBackQuery) qef, source);

        for (UnitTest t : tests) {
            qef.createQueryExecution(t.sparqlPrevalence);
        }

    }

    // TODO automate & move this functionality out of here
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
