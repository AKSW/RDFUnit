package org.aksw.databugger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
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

    public Model executeTests() {
        Model model = ModelFactory.createDefaultModel();

        for (UnitTest t : tests ) {


            int total = 0, prevalence = 0;

            QueryExecution qe = null;
            ResultSet results = null;

            //query
            try {
                qe = source.getExecutionFactory().createQueryExecution(t.getSparqlQueryAsCount());
                results = qe.execSelect();
            } catch (Exception e) {
                total = -1;
            }

            if ( results != null && results.hasNext()) {
                QuerySolution qs = results.next();

                total = qs.get("total").asLiteral().getInt();

            }
            //prevalence
            try {
                qe = source.getExecutionFactory().createQueryExecution(t.getSparqlPrevalence());
                results = qe.execSelect();
            } catch (Exception e) {
                prevalence = -1;
            }

            if ( results != null && results.hasNext()) {
                QuerySolution qs = results.next();

                prevalence = qs.get("total").asLiteral().getInt();

            }

            Resource resource = model.createResource()
                    .addProperty(RDF.type, model.createResource("tddo:Result"))
                    .addProperty(model.createProperty("tddo:count"), ""+total)
                    .addProperty(model.createProperty("tddo:prevalence"), ""+total)
                    .addProperty(model.createProperty("tddo:query"), model.createResource(t.getTestURI()));

            log.info("Returned " + total + " errors ( " + prevalence + "prevalence) for test: " + t.getTestURI());

            if (qe != null) qe.close();

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

}
