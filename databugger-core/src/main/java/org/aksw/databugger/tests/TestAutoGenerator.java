package org.aksw.databugger.tests;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternParameter;
import org.aksw.databugger.patterns.PatternService;
import org.aksw.databugger.sources.Source;
import org.aksw.jena_sparql_api.core.QueryExecutionFactoryBackQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Takes a pattern and a SPARQL query and based on the data
 * returned from that query we generate test cases
 * Created: 9/20/13 2:48 PM
 */
public class TestAutoGenerator {
    final public String query;
    final public String patternID;

    public TestAutoGenerator(String query, String patternID) {
        this.query = query;
        this.patternID = patternID;
    }

    public List<UnitTest> generate(QueryExecutionFactoryBackQuery bq, Source source) {
        List<UnitTest> tests = new ArrayList<UnitTest>();


        QueryExecution qe = bq.createQueryExecution(query);
        ResultSet rs = qe.execSelect();
        Pattern pattern = PatternService.getPattern(patternID);

        while (rs.hasNext()) {
            QuerySolution row = rs.next();

            List<String> bindings = new ArrayList<String>();
            for (PatternParameter p : pattern.parameters) {
                if (row.contains(p.id)) {
                    RDFNode n = row.get(p.id);
                    if (n.isResource())
                        bindings.add("<" + n.toString() + ">");
                    else
                        bindings.add(n.toString());
                } else {
                    break;
                }


            }

            try {
                tests.add(pattern.instantiate(bindings, source.uri, new TestAnnotation()));
            } catch (Exception e) {

            }

        }


        return tests;
    }
}
