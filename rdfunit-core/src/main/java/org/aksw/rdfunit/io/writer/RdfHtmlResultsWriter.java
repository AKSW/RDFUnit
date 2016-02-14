package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;

import java.io.IOException;
import java.io.OutputStream;

/**
 * RDFHTMLResultsWriter are different front he other RDFUnit writers because they write a TestExecution
 * initialized in construction and not the model provided in write(Model)
 *
 * @since 11/14/13 1:04 PM
 * @version $Id: $Id
 */
public abstract class RdfHtmlResultsWriter implements RDFWriter  {
    protected final TestExecution testExecution;
    private final OutputStream outputStream;


    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public RdfHtmlResultsWriter(TestExecution testExecution, OutputStream outputStream) {
        super();
        this.testExecution = testExecution;
        this.outputStream = outputStream;
    }

    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public RdfHtmlResultsWriter(TestExecution testExecution, String filename) {
        this(testExecution, RDFStreamWriter.getOutputStreamFromFilename(filename));
    }

    /** {@inheritDoc} */
    @Override
    public void write(QueryExecutionFactory qef) throws RDFWriterException {

        try {
            // TODO not efficient StringBuilder.toString().getBytes()
            outputStream.write(getHeader().toString().getBytes("UTF8"));

            outputStream.write(getTestExecutionStats().toString().getBytes("UTF8"));
            outputStream.write(getTestExecutionResults().toString().getBytes("UTF8"));
                // break; // For now print only one (this is the case at the moment)

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

    protected abstract StringBuffer getResultsList() ;

    private StringBuffer getHeader() {
        StringBuffer header = new StringBuffer();
        header.append("<!DOCTYPE html>\n<html><head>\n");
        header.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"http://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.16.4/css/theme.default.css\" >\n" +
                "<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-1.11.0.min.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.16.4/jquery.tablesorter.min.js\"></script>\n" +
                "<script type=\"text/javascript\"> $(function() {$(\"#myTable\").tablesorter();});</script>" +
                "<title>RDFUnit validation results</title>");
        header.append("</head><body>\n");
        return header;
    }

    private StringBuffer getFooter() {
        return new StringBuffer("</body>\n</html>");
    }

    private StringBuffer getTestExecutionStats() throws RDFWriterException {
        StringBuffer stats = new StringBuffer();
        stats.append("<h2>TestExecution: ").append(testExecution.getTestExecutionUri()).append("</h2>\n");
        //TODO for some reason, using the "testExecution" URI does not work :/

        DatasetOverviewResults dor = testExecution.getDatasetOverviewResults();

        stats.append("<dl class=\"dl-horizontal\">\n");
        stats.append("<dt>Dataset</dt><dd> ").append(testExecution.getTestedDatasetUri()).append("</dd>\n");
        //stats.append("<dt>Test suite</dt><dd>").append(used).append("</dd>");
        stats.append("<dt>Test execution started</dt><dd> ").append(dor.getStartTime()).append("</dd>\n");
        stats.append("<dt>-ended</dt><dd> ").append(dor.getEndTime()).append("</dd>\n");
        stats.append("<dt>Total test cases</dt><dd> ").append(dor.getTotalTests()).append("</dd>\n");
        stats.append("<dt>Succeeded</dt><dd> ").append(dor.getSuccessfulTests()).append("</dd>\n");
        stats.append("<dt>Failed</dt><dd> ").append(dor.getFailedTests()).append("</dd>\n");
        stats.append("<dt>Timeout / Error </dt><dd> T:").append(dor.getTimeoutTests()).append(" / E: ").append(dor.getErrorTests()).append("</dd>\n");
        stats.append("<dt>Violation instances</dt><dd> ").append(dor.getIndividualErrors()).append("</dd>\n");
        stats.append("</dl>\n");
        return stats;
    }

    private StringBuffer getTestExecutionResults() throws RDFWriterException {
        StringBuffer results = new StringBuffer();
        results.append("<h3>Results</h3>\n");
        results.append("<table id=\"myTable\" class=\"tablesorter tablesorter-default table\" summary=\"Detailed results\"><thead>\n");
        results.append(getResultsHeader());
        results.append("</thead><tbody>\n");
        results.append(getResultsList());
        results.append("</tbody></table>\n");
        return results;
    }
}
