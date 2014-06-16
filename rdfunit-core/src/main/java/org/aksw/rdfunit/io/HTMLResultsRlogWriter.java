package org.aksw.rdfunit.io;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.services.PrefixService;

import java.io.OutputStream;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 4/23/14 8:55 AM
 */
public class HTMLResultsRlogWriter extends HTMLResultsWriter {

    public HTMLResultsRlogWriter(String filename) {
        super(filename);
    }

    public HTMLResultsRlogWriter(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Level</th><th>Message</th><th>Resource</th><th>Test Case</th></tr>");
    }

    @Override
    protected StringBuffer getResultsList(QueryExecutionFactory qef, String testExecutionURI) {
        StringBuffer results = new StringBuffer();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</ts><td><a href=\"%s\">%s</a></td><td>%s</td></tr>";

        String sparql = PrefixService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?level ?message ?resource ?testcase WHERE {" +
                " ?s a rut:RLOGTestCaseResult ; " +
                "    rlog:level ?level ;" +
                "    rlog:message ?message ; " +
                "    rlog:resource ?resource ; " +
                "    rut:testCase ?testcase ; " +
                //"    prov:wasGeneratedBy <" + testExecutionURI + "> " +
                "} ";

        QueryExecution qe = null;

        try {
            qe = qef.createQueryExecution(sparql);
            ResultSet rs = qe.execSelect();

            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String level = qs.get("level").toString();
                String message = qs.get("message").toString();
                String resource = qs.get("resource").toString();
                String testcase = qs.get("testcase").toString();

                String levelShort = level.replace(PrefixService.getNSFromPrefix("rlog"), "");
                String rowClass = "";
                switch (levelShort) {
                    case "Warn":
                        rowClass = "warning";
                        break;
                    case "Error":
                        rowClass = "danger";
                        break;
                    case "Notice":
                        rowClass = "info";
                        break;
                    default:
                }
                String row = String.format(template,
                        rowClass,
                        "<a href=\"" + level + "\">" + levelShort + "</a>",
                        message,
                        resource, resource, // <a href=%s>%s</a>
                        testcase.replace(PrefixService.getNSFromPrefix("rutt"), "rutt:"));
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
