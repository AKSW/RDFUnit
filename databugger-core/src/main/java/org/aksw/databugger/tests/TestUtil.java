package org.aksw.databugger.tests;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.databugger.Utils;
import org.aksw.databugger.patterns.PatternParameter;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Various utility test functions for tests
 * Created: 9/24/13 10:59 AM
 */
public class TestUtil {
    public static List<TestAutoGenerator> instantiateTestGeneratorsFromModel(QueryExecutionFactory queryFactory) {
        List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();

        String sparqlSelect =  Utils.getAllPrefixes() +
                        " SELECT ?desc ?query ?patternID WHERE { " +
                        " ?generator a tddo:TestGenerator ; " +
                        "  dcterms:description ?desc ; " +
                        "  tddo:generatorSPARQL ?query ; " +
                        "  tddo:basedOnPattern ?pattern . " +
                        " ?pattern dcterms:identifier ?patternID ." +
                        "} ";

        QueryExecution qe = queryFactory.createQueryExecution(sparqlSelect);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();

            String description = qs.get("desc").toString();
            String query = qs.get("query").toString();
            String patternID = qs.get("patternID").toString();

            autoGenerators.add(new TestAutoGenerator(description, query,patternID));
        }
        qe.close();

        return autoGenerators;

    }
}
