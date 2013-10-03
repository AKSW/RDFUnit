package org.aksw.databugger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.UnitTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public Model executeTestsCounts() {
        Model model = ModelFactory.createDefaultModel();

        for (UnitTest t : tests ) {

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
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "count"), ""+total)
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "prevalence"), ""+total)
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "source"), model.createResource(t.getSourceUri()))
                    .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "query"), model.createResource(t.getTestURI()));

            log.info("Returned " + total + " errors ( " + prevalence + " prevalence) for test: " + t.getTestURI());

            if (delay>0) {
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
        ResultSet  results = qe.execSelect();

        if ( results != null && results.hasNext()) {
            QuerySolution qs = results.next();

            result = qs.get(var).asLiteral().getInt();

        }
        qe.close();

        return result;

    }

}
