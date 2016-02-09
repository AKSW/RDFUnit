package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>Abstract RDFXMLResultsWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Writes results in JUnit XML format
 * @since 11/14/13 1:04 PM
 * @version $Id: $Id
 */
public abstract class RDFXMLResultsWriter extends AbstractRDFWriter implements RDFWriter  {
    private final OutputStream outputStream;

    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public RDFXMLResultsWriter(OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public RDFXMLResultsWriter(String filename) {
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
        header.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        return header;
    }

    private StringBuffer getFooter() {
        return new StringBuffer("</testsuite>");
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
    
    /**
     * Create <testsuite> element containing test execution stats
     * @param qef
     * @param testExecution
     * @return
     * @throws RDFWriterException
     */

    private StringBuffer getTestExecutionStats(QueryExecutionFactory qef, String testExecution) throws RDFWriterException {
        StringBuffer stats = new StringBuffer();
        stats.append("<testsuite name=\"").append(testExecution).append("\" ");
        //TODO for some reason, using the "testExecution" URI does not work :/
        String sparql =
                PrefixNSService.getSparqlPrefixDecl() +
                        " SELECT ?s ?p ?o WHERE { ?s ?p ?o ; a rut:TestExecution . } ";
        QueryExecution qe = null;

        String source = "";
        String testsRun = "-";
        String testsFailed = "-";
        String endedAtTime = "";
        String startedAtTime = "";

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
                    case "http://rdfunit.aksw.org/ns/core#testsFailed":
                        testsFailed = object;
                        break;
                    case "http://www.w3.org/ns/prov#endedAtTime":
                        endedAtTime = object;
                        break;
                    case "http://www.w3.org/ns/prov#startedAtTime":
                        startedAtTime = object;
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

        stats.append("timestamp=\"").append(endedAtTime).append("\" ");
        String length = testLength(startedAtTime, endedAtTime);
        if(length!=null) {
        	stats.append("time=\"").append(length).append("\" ");
        }
        stats.append("tests=\"").append(testsRun).append("\" ");
        stats.append("failures=\"").append(testsFailed).append("\" ");
        stats.append("package=\"").append(source).append("\"");
        stats.append(">\n");
        return stats;
    }
    
    private String testLength(String datetimeStart, String datetimeEnd) {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	Date start = null;
    	Date end = null;
    	try {
    		start = format.parse(datetimeStart);
    		end = format.parse(datetimeEnd);
    	} catch (ParseException e) {
    		return null;
    	}
    	long diff = end.getTime() - start.getTime();
    	return String.format("%02d:%02d:%02d", 
    		    TimeUnit.MILLISECONDS.toHours(diff),
    		    TimeUnit.MILLISECONDS.toMinutes(diff) - 
    		    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)),
    		    TimeUnit.MILLISECONDS.toSeconds(diff) - 
    		    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
    }

    private StringBuffer getTestExecutionResults(QueryExecutionFactory qef, String testExecution) throws RDFWriterException {
        StringBuffer results = new StringBuffer();
        results.append(getResultsList(qef, testExecution));
        return results;
    }
}
