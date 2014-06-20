package org.aksw.rdfunit.io;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Writes results in HTML format
 * @since 11/14/13 1:04 PM
 */
public abstract class HTMLResultsWriter extends DataWriter {
    private final OutputStream outputStream;


    public HTMLResultsWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public HTMLResultsWriter(String filename) {
        this.outputStream = RDFStreamWriter.getOutputStreamFromFilename(filename);
    }

    @Override
    public void write(QueryExecutionFactory qef) throws TripleWriterException {
        final Collection<String> testExecutionURIs = getTestExecutionURI(qef);

        try {
            // TODO not efficient StringBuilder.toString().getBytes()
            outputStream.write(getHeader().toString().getBytes());

            for (String te : testExecutionURIs) {
                outputStream.write(getTestExecutionStats(qef, te).toString().getBytes());
                outputStream.write(getTestExecutionResults(qef, te).toString().getBytes());
                // break; // For now print only one (this is the case at the moment)
            }

            outputStream.write(getFooter().toString().getBytes());
            outputStream.close();

        } catch (IOException e) {
            throw new TripleWriterException("Cannot write HTML", e);
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
        ArrayList<String> executionURIs = new ArrayList<>();
        String sparql =
                PrefixNSService.getSparqlPrefixDecl() +
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
            if (qe != null) {
                qe.close();
            }
        }
        return executionURIs;
    }

    private StringBuffer getTestExecutionStats(QueryExecutionFactory qef, String testExecution) {
        StringBuffer stats = new StringBuffer();
        stats.append("<h2>TestExecution: " + testExecution + "</h2>");
        //TODO for some reason, using the "testExecution" URI does not work :/
        String sparql =
                PrefixNSService.getSparqlPrefixDecl() +
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
                String object = n.toString();
                if (n.isLiteral()) {
                    object = n.asLiteral().getValue().toString();
                }
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
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        stats.append("<dl class=\"dl-horizontal\">");
        stats.append("<dt>Dataset</dt><dd> " + source + "</dd>");
        stats.append("<dt>Test suite</dt><dd>" + used + "</dd>");
        stats.append("<dt>Test execution started</dt><dd> " + startedAtTime + "</dd>");
        stats.append("<dt>-ended</dt><dd> " + endedAtTime + "</dd>");
        stats.append("<dt>Total test cases</dt><dd> " + testsRun + "</dd>");
        stats.append("<dt>Succeeded</dt><dd> " + testsSuceedded + "</dd>");
        stats.append("<dt>Failed</dt><dd> " + testsFailed + "</dd>");
        stats.append("<dt>Timeout / Error </dt><dd> T:" + testsTimeout + " / E: " + testsError + "</dd>");
        stats.append("<dt>Violation instances</dt><dd> " + totalIndividualErrors + "</dd>");
        stats.append("</dl>");
        return stats;
    }

    private StringBuffer getTestExecutionResults(QueryExecutionFactory qef, String testExecution) {
        StringBuffer results = new StringBuffer();
        results.append("<h3>Results</h3>");
        results.append("<table id=\"myTable\" class=\"tablesorter tablesorter-default table\"><thead>");
        results.append(getResultsHeader());
        results.append("</thead><tbody>");
        results.append(getResultsList(qef, testExecution));
        results.append("</tbody></table>");
        return results;
    }
}
