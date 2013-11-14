package org.aksw.databugger;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.UnitTest;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.List;

/**
 * Takes a dataset source and executes the test queries against the endpoint
 * Description
 * Created: 9/30/13 11:11 AM
 */
public class TestExecutor {
    private static Logger log = LoggerFactory.getLogger(TestExecutor.class);
    private final Source source;
    private final List<UnitTest> tests;
    private final int delay;

    public TestExecutor(Source source, List<UnitTest> tests, int delay) {
        this.source = source;
        this.tests = tests;
        this.delay = delay;
    }

    public Model executeTestsCounts(String filename) {
        Model model = ModelFactory.createDefaultModel();

        try {
            model.read(new FileInputStream(filename), null, "TURTLE");
        } catch (Exception e) {
            // TODO handle exception
        }
        QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);

        int counter = 0;
        int testSize = tests.size();
        for (UnitTest t : tests) {

            counter++;
            if (testExists(qef, t.getTestURI()))
                continue;

            int total = -1, prevalence = -1;

            try {
                prevalence = getCountNumber(source.getExecutionFactory(), t.getSparqlPrevalenceQuery(), "total");
            } catch (Exception e) {
                //query failed total remains -1
            }

            if (prevalence != 0) {
                // if prevalence !=0 calculate total
                try {
                    total = getCountNumber(source.getExecutionFactory(), t.getSparqlAsCountQuery(), "total");
                } catch (Exception e) {
                    //query failed total remains -1
                }
            } else
                // else total will be 0 anyway
                total = 0;

            model.createResource()
                    .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "Result"))
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "count"), "" + total)
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "prevalence"), "" + total)
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "source"), model.createResource(source.getUri()))
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "query"), model.createResource(t.getTestURI()));

            log.info("Testing " + source.getPrefix() + " (" + counter + "/" + testSize + ") returned " + total + " errors ( " + prevalence + " prevalence) for test: " + t.getTestURI());

            if (counter % 20 == 0) {
                try {
                    DatabuggerUtils.writeModelToFile(model, "TURTLE", filename, true);
                } catch (Exception e) {
                    log.error("Cannot write tests to file: ");
                }
            }


            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        generateStats(qef);
        return model;
    }

    private void generateStats(QueryExecutionFactory qef) {
        String result = DatabuggerUtils.getAllPrefixes() +
                " SELECT (count(?s) AS ?total) where {\n" +
                " ?s a tddo:Result.\n" +
                " ?s tddo:count ?errors .\n" +
                " FILTER (xsd:decimal(?errors) %%OPVAL%% ) }";
        int pass = getCountNumber(qef, result.replace("%%OPVAL%%", " =  0 "), "total");
        int fail = getCountNumber(qef, result.replace("%%OPVAL%%", " >  0 "), "total");
        int timeout = getCountNumber(qef, result.replace("%%OPVAL%%", " = -1 "), "total");
        int total = pass + fail + timeout;
        log.info("Total tests: " + total + " Pass: " + pass + " Fail: " + fail + " Timeout: " + timeout);

        String totalErrors = DatabuggerUtils.getAllPrefixes() +
                " SELECT (sum(xsd:decimal(?errors)) AS ?total) WHERE {\n" +
                " ?s a tddo:Result.\n" +
                " ?s tddo:count ?errors .\n" +
                " FILTER (xsd:decimal(?errors) > 0 )}";
        int errors = getCountNumber(qef, totalErrors, "total");
        log.info("Total Errors: " + errors);


    }

    private int getCountNumber(QueryExecutionFactory model, String query, String var) {
        return getCountNumber(model, QueryFactory.create(query), var);
    }

    private int getCountNumber(QueryExecutionFactory model, Query query, String var) {

        int result = 0;
        QueryExecution qe = model.createQueryExecution(query);
        ResultSet results = qe.execSelect();

        if (results != null && results.hasNext()) {
            QuerySolution qs = results.next();

            result = qs.get(var).asLiteral().getInt();

        }
        qe.close();

        return result;

    }

    private boolean testExists(QueryExecutionFactory qef, String testURI) {

        boolean result = false;
        QueryExecution qe = qef.createQueryExecution("select * where { ?s ?p <" + testURI + "> }");
        ResultSet results = qe.execSelect();

        if (results != null && results.hasNext()) {
            result = true;
        }
        qe.close();

        return result;
    }

}
