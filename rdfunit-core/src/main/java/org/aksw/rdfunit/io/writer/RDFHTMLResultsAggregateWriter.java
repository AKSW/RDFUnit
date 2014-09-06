package org.aksw.rdfunit.io.writer;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 4/23/14 8:55 AM
 */
public class RDFHTMLResultsAggregateWriter extends RDFHTMLResultsStatusWriter {

    public RDFHTMLResultsAggregateWriter(String filename) {
        super(filename);
    }

    public RDFHTMLResultsAggregateWriter(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Status</th><th>Level</th><th>Test Case</th><th>Errors</th><th>Prevalence</th></tr>");
    }

    @Override
    protected StringBuffer getResultsList(QueryExecutionFactory qef, String testExecutionURI) {
        StringBuffer results = new StringBuffer();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</td><td><span title=\"%s\">%s</span></td><td>%s</td><td>%s</td></tr>";

        String sparql = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?resultStatus ?level ?testcase ?description ?resultCount ?resultPrevalence WHERE {" +
                " ?s a rut:AggregatedTestResult ; " +
                "    rut:resultStatus ?resultStatus ; " +
                "    rut:testCaseLogLevel ?level ; " +
                "    rut:testCase ?testcase ;" +
                "    dcterms:description ?description ;" +
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
                String description = qs.get("description").toString();
                String resultCount = qs.get("resultCount").asLiteral().getValue().toString();
                String resultPrevalence = qs.get("resultPrevalence").asLiteral().getValue().toString();
                String level = qs.get("level").toString();

                String statusShort = resultStatus.replace(PrefixNSService.getNSFromPrefix("rut") + "ResultStatus", "");
                String levelShort = PrefixNSService.getLocalName(level, "rlog");
                String rowClass = getStatusClass(statusShort);

                String row = String.format(template,
                        rowClass,
                        "<a href=\"" + resultStatus + "\">" + statusShort + "</a>",
                        "<a href=\"" + level + "\">" + levelShort + "</a>",
                        testcase.replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"),
                        description,
                        resultCount,
                        resultPrevalence);
                results.append(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return results;
    }
}
