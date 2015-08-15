package org.aksw.rdfunit.io.writer;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>Abstract RDFHTMLResultsWriter class.</p>
 *
 * @author Dimitris Kontokostas
 *         Writes results in HTML format
 * @since 11/14/13 1:04 PM
 * @version $Id: $Id
 */
public abstract class RDFHTMLResultsWriter extends AbstractRDFWriter implements RDFWriter  {
    private final OutputStream outputStream;


    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public RDFHTMLResultsWriter(OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public RDFHTMLResultsWriter(String filename) {
        super();
        this.outputStream = RDFStreamWriter.getOutputStreamFromFilename(filename);
    }

    /** {@inheritDoc} */
    @Override
    public void write(QueryExecutionFactory qef) throws RDFWriterException {
        final Collection<String> testExecutionURIs = getTestExecutionURI(qef);

        try {
            // TODO not efficient StringBuilder.toString().getBytes()
            outputStream.write(getHeader().toString().getBytes("UTF8"));

            for (String te : testExecutionURIs) {
                outputStream.write(getTestExecutionStats(qef, te).toString().getBytes("UTF8"));
                outputStream.write(getTestExecutionResults(qef, te).toString().getBytes("UTF8"));
                // break; // For now print only one (this is the case at the moment)
            }

            outputStream.write(getFooter().toString().getBytes("UTF8"));
            outputStream.close();

        } catch (IOException e) {
            throw new RDFWriterException("Cannot write HTML", e);
        }
    }

    /**
     * <p>getResultsHeader.</p>
     *
     * @return a {@link java.lang.StringBuffer} object.
     */
    protected abstract StringBuffer getResultsHeader();

    /**
     * <p>getResultsList.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @param testExecutionURI a {@link java.lang.String} object.
     * @return a {@link java.lang.StringBuffer} object.
     * @throws org.aksw.rdfunit.io.writer.RDFWriterException if any.
     */
    protected abstract StringBuffer getResultsList(QueryExecutionFactory qef, String testExecutionURI)  throws RDFWriterException;

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

    private Collection<String> getTestExecutionURI(QueryExecutionFactory qef) throws RDFWriterException {
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
            throw new RDFWriterException(e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }
        return executionURIs;
    }

    private StringBuffer getTestExecutionStats(QueryExecutionFactory qef, String testExecution) throws RDFWriterException {
        StringBuffer stats = new StringBuffer();
        stats.append("<h2>TestExecution: ").append(testExecution).append("</h2>");
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
            throw new RDFWriterException(e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        stats.append("<dl class=\"dl-horizontal\">");
        stats.append("<dt>Dataset</dt><dd> ").append(source).append("</dd>");
        stats.append("<dt>Test suite</dt><dd>").append(used).append("</dd>");
        stats.append("<dt>Test execution started</dt><dd> ").append(startedAtTime).append("</dd>");
        stats.append("<dt>-ended</dt><dd> ").append(endedAtTime).append("</dd>");
        stats.append("<dt>Total test cases</dt><dd> ").append(testsRun).append("</dd>");
        stats.append("<dt>Succeeded</dt><dd> ").append(testsSuceedded).append("</dd>");
        stats.append("<dt>Failed</dt><dd> ").append(testsFailed).append("</dd>");
        stats.append("<dt>Timeout / Error </dt><dd> T:").append(testsTimeout).append(" / E: ").append(testsError).append("</dd>");
        stats.append("<dt>Violation instances</dt><dd> ").append(totalIndividualErrors).append("</dd>");
        stats.append("</dl>");
        return stats;
    }

    private StringBuffer getTestExecutionResults(QueryExecutionFactory qef, String testExecution) throws RDFWriterException {
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
