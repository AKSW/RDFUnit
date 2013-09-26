package org.aksw.databugger.tests;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.Utils;
import org.aksw.databugger.sources.Source;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.io.File;
import java.io.FileOutputStream;
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
                        " SELECT ?generator ?desc ?query ?patternID WHERE { " +
                        " ?generator a tddo:TestGenerator ; " +
                        "  dcterms:description ?desc ; " +
                        "  tddo:generatorSPARQL ?query ; " +
                        "  tddo:basedOnPattern ?sparqlPattern . " +
                        " ?sparqlPattern dcterms:identifier ?patternID ." +
                        "} ";

        QueryExecution qe = queryFactory.createQueryExecution(sparqlSelect);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();

            String generator = qs.get("generator").toString();
            String description = qs.get("desc").toString();
            String query = qs.get("query").toString();
            String patternID = qs.get("patternID").toString();

            TestAutoGenerator tag = new TestAutoGenerator(generator, description, query,patternID);
            if (tag.isValid())
                autoGenerators.add(tag);
            else {
                //TODO logger
            }
        }
        qe.close();

        return autoGenerators;

    }

    public static List<UnitTest> isntantiateTestsFromAG(List<TestAutoGenerator> autoGenerators, Source source) {
        List<UnitTest> tests = new ArrayList<UnitTest>();

        for (TestAutoGenerator tag: autoGenerators ) {
            tests.addAll( tag.generate(source));
        }

        return tests;

    }

    public static void writeTestsToFile(List<UnitTest> tests, String filename) {
        Model model = ModelFactory.createDefaultModel();
        for (UnitTest t: tests)
            t.saveTestToModel(model);
        try {
            File f = new File(filename);
            f.getParentFile().mkdirs();

            model.write(new FileOutputStream(filename),"TURTLE");
        } catch (Exception e) {
            // TODO handle exceptions
        }
    }
}
