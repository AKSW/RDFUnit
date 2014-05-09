package org.aksw.rdfunit.io;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.services.PrefixService;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestSuite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Dimitris Kontokostas
 * Writes results in HTML format
 * Created: 11/14/13 1:04 PM
 */
public abstract class HTMLResultsWriter extends DataWriter {
    private final String filename;


    public HTMLResultsWriter(String filename) {
        this.filename = filename;
    }

    public static HTMLResultsWriter create(TestCaseExecutionType type, String filename) {
        switch (type) {
            case statusTestCaseResult:
                return new HTMLResultsStatusWriter(filename);
            case aggregatedTestCaseResult:
                return new HTMLResultsAggregateWriter(filename);
            case rlogTestCaseResult:
                return new HTMLResultsRlogWriter(filename);
            //case extendedTestCaseResult:
            default:
                return null;
        }
    }

    @Override
    public void write(QueryExecutionFactory qef) {
        final Collection<String> testExecutionURIs = getTestExecutionURI(qef);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            writer.append(getHeader());

            for (String te: testExecutionURIs){
                writer.append(getTestExecutionStats(qef, te));
                writer.append(getTestExecutionResults(qef, te));
               // break; // For now print only one (this is the case at the moment)
            }

            writer.append(getFooter());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract StringBuffer getResultsHeader();
    protected abstract StringBuffer getResultsList(QueryExecutionFactory qef, String testExecutionURI);

    private StringBuffer getHeader() {
        StringBuffer header = new StringBuffer();
        header.append("<!DOCTYPE html><html><head>\n");
        header.append("<link href=\"http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "<link href=\"http://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.16.4/css/theme.default.css\" rel=\"stylesheet\">\n" +
                "<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-1.11.0.min.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.16.4/jquery.tablesorter.min.js\"></script>\n" +
                "<script> $(function() {$(\"#myTable\").tablesorter();});</script>");
        header.append("</head><body>\n");
        return header;
    }

    private StringBuffer getFooter() {
        return new StringBuffer("</body></html>");
    }

    private Collection<String> getTestExecutionURI(QueryExecutionFactory qef) {
        ArrayList<String> executionURIs = new ArrayList<String>();
        String sparql =
                RDFUnitUtils.getAllPrefixes() +
                " SELECT DISTINCT ?testExecution WHERE { ?testExecution a rut:TestExecution } ";

        QueryExecution qe = null;

        try {
            qe = qef.createQueryExecution(sparql);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();
                String te = qs.get("testExecution").toString();
                executionURIs.add(te);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qe != null)
                qe.close();
        }
        return executionURIs;
    }

    private StringBuffer getTestExecutionStats(QueryExecutionFactory qef, String testExecution) {
        StringBuffer stats = new StringBuffer();
        stats.append("<h1>TestExecution: " + testExecution + "</h1>");
        //TODO for some reason, using the "testExecution" URI does not work :/
        String sparql =
                RDFUnitUtils.getAllPrefixes() +
                        " SELECT ?s ?p ?o WHERE { ?s ?p ?o ; a rut:TestExecution . } ";
        QueryExecution qe = null;

        String source = "";
        String testsRun = "-";
        String testsSuceedded = "-";
        String testsFailed = "-";
        String testsError = "-";
        String testsTimeout = "-";
        String totalIndividualErrors = "-";
        String endedAtTime = "";
        String startedAtTime = "";
        String used = "";

        try {
            qe = qef.createQueryExecution(sparql);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();
                String s = qs.get("s").toString();
                String property = qs.get("p").toString();
                RDFNode n = qs.get("o");
                String object =  n.toString();
                if (n.isLiteral())
                    object = n.asLiteral().getValue().toString();
                switch (property) {
                    case "http://rdfunit.aksw.org/ns/core#source":
                        source = object;
                        break;
                    case "http://rdfunit.aksw.org/ns/core#testsRun":
                        testsRun = object;
                        break;
                    case "http://rdfunit.aksw.org/ns/core#testsSuceedded":
                        testsSuceedded = object;
                        break;
                    case "http://rdfunit.aksw.org/ns/core#testsFailed":
                        testsFailed = object;
                        break;
                    case "http://rdfunit.aksw.org/ns/core#testsError":
                        testsError = object;
                        break;
                    case "http://rdfunit.aksw.org/ns/core#testsTimeout":
                        testsTimeout = object;
                        break;
                    case "http://rdfunit.aksw.org/ns/core#totalIndividualErrors":
                        totalIndividualErrors = object;
                        break;
                    case "http://www.w3.org/ns/prov#endedAtTime":
                        endedAtTime = object;
                        break;
                    case "http://www.w3.org/ns/prov#startedAtTime":
                        startedAtTime = object;
                        break;
                    case "http://www.w3.org/ns/prov#used":
                        used = object;
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qe != null)
                qe.close();
        }

        stats.append("<ul>");
        stats.append("<li>Dataset: " + source + "</li>");
        stats.append("<li>Test suite: " + used + "</li>");
        stats.append("<li>Test execution started: " + startedAtTime + " / ended:" + endedAtTime +  "</li>");
        stats.append("<li>Total test cases: " + testsRun + " / succeeded: " + testsSuceedded +  "</li>");
        stats.append("<li>Failed: " + testsFailed + " / Timeout: " + testsTimeout + " / Error: " + testsError + "</li>");
        stats.append("<li>Violation instances: " + totalIndividualErrors + "</li>");
        stats.append("</ul>");
        return stats;
    }

    private StringBuffer getTestExecutionResults(QueryExecutionFactory qef, String testExecution) {
        StringBuffer results = new StringBuffer();
        results.append("<h2>Results</h2>");
        results.append("<table id=\"myTable\" class=\"tablesorter tablesorter-default table\"><thead>");
        results.append(getResultsHeader());
        results.append("</thead><tbody>");
        results.append(getResultsList(qef,testExecution));
        results.append("</tbody></table>");
        return results;
    }
}
