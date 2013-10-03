package org.aksw.databugger;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.UnitTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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

        int counter = 0;
        int testSize = tests.size();
        for (UnitTest t : tests) {

            counter++;
            if (testExists(model, t.getTestURI()))
                continue;

            int total = -1, prevalence = -1;

            try {
                total = getCountNumber(model, t.getSparqlAsCountQuery(), "total");
            } catch (Exception e) {
                //query failed total remains -1
            }

            try {
                prevalence = getCountNumber(model, t.getSparqlPrevalenceQuery(), "total");
            } catch (Exception e) {
                //query failed total remains -1
            }

            model.createResource()
                    .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "Result"))
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "count"), "" + total)
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "prevalence"), "" + total)
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "source"), model.createResource(source.getUri()))
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "query"), model.createResource(t.getTestURI()));

            log.info("Testing " + source.getPrefix() + "(" + counter + "/" + testSize + ") returned " + total + " errors ( " + prevalence + " prevalence) for test: " + t.getTestURI());

            if (counter % 20 == 0) {
                try {
                    model.write(new FileOutputStream(filename), "TURTLE");
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
        return model;
    }

    private int getCountNumber(Model model, Query query, String var) {

        int result = 0;
        QueryExecution qe = source.getExecutionFactory().createQueryExecution(query);
        ResultSet results = qe.execSelect();

        if (results != null && results.hasNext()) {
            QuerySolution qs = results.next();

            result = qs.get(var).asLiteral().getInt();

        }
        qe.close();

        return result;

    }

    private boolean testExists(Model model, String testURI) {

        boolean result = false;
        Query query = QueryFactory.create("select * where { ?s ?p <" + testURI + "> }");
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        if (results != null && results.hasNext()) {
            result = true;
        }
        qe.close();

        return result;
    }

}
