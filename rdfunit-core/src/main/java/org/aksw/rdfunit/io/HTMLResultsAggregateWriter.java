package org.aksw.rdfunit.io;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.services.PrefixService;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 4/23/14 8:55 AM
 */
public class HTMLResultsAggregateWriter extends HTMLResultsWriter {

    public HTMLResultsAggregateWriter(String filename) {
        super(filename);
    }

    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Status</th><th>Test Case</th><th>Errors</th><th>Prevalence</th></tr>");
    }

    @Override
    protected StringBuffer getResultsList(QueryExecutionFactory qef, String testExecutionURI) {
        StringBuffer results = new StringBuffer();
        String template = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";

        String sparql = RDFUnitUtils.getAllPrefixes() +
                " SELECT DISTINCT ?resultStatus ?testcase ?resultCount ?resultPrevalence WHERE {" +
                " ?s a rut:AggregatedTestResult ; " +
                "    rut:resultStatus ?resultStatus ; " +
                "    rut:testCase ?testcase ;" +
                "    rut:resultCount ?resultCount ; " +
                "    rut:resultPrevalence ?resultPrevalence ; " +
                //"    prov:wasGeneratedBy <" + testExecutionURI + "> " +
                "} ";

        QueryExecution qe = null;

        try {
            qe = qef.createQueryExecution(sparql);
            ResultSet rs = qe.execSelect();

            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String resultStatus = qs.get("resultStatus").toString();
                String testcase = qs.get("testcase").toString();
                String resultCount = qs.get("resultCount").asLiteral().getValue().toString();
                String resultPrevalence = qs.get("resultPrevalence").asLiteral().getValue().toString();
                String row = String.format(template,
                        resultStatus.replace(PrefixService.getPrefix("rut"),"rut:"),
                        testcase.replace(PrefixService.getPrefix("rutt"),"rutt:"),
                        resultCount,
                        resultPrevalence);
                results.append(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qe != null)
                qe.close();
        }

        return results;
    }
}
