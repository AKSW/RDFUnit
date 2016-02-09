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
public abstract class RDFHtmlResultsWriter extends AbstractRDFWriter implements RDFWriter  {
    protected final TestExecution testExecution;
    private final OutputStream outputStream;


    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public RDFHtmlResultsWriter(TestExecution testExecution, OutputStream outputStream) {
        super();
        this.testExecution = testExecution;
        this.outputStream = outputStream;
    }

    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public RDFHtmlResultsWriter(TestExecution testExecution, String filename) {
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

    private StringBuffer getTestExecutionStats() throws RDFWriterException {
        StringBuffer stats = new StringBuffer();
        stats.append("<h2>TestExecution: ").append(testExecution.getTestExecutionUri()).append("</h2>");
        //TODO for some reason, using the "testExecution" URI does not work :/

        DatasetOverviewResults dor = testExecution.getDatasetOverviewResults();

        stats.append("<dl class=\"dl-horizontal\">");
        stats.append("<dt>Dataset</dt><dd> ").append(testExecution.getTestedDatasetUri()).append("</dd>");
        //stats.append("<dt>Test suite</dt><dd>").append(used).append("</dd>");
        stats.append("<dt>Test execution started</dt><dd> ").append(dor.getStartTime()).append("</dd>");
        stats.append("<dt>-ended</dt><dd> ").append(dor.getEndTime()).append("</dd>");
        stats.append("<dt>Total test cases</dt><dd> ").append(dor.getTotalTests()).append("</dd>");
        stats.append("<dt>Succeeded</dt><dd> ").append(dor.getSuccessfulTests()).append("</dd>");
        stats.append("<dt>Failed</dt><dd> ").append(dor.getFailedTests()).append("</dd>");
        stats.append("<dt>Timeout / Error </dt><dd> T:").append(dor.getTimeoutTests()).append(" / E: ").append(dor.getErrorTests()).append("</dd>");
        stats.append("<dt>Violation instances</dt><dd> ").append(dor.getIndividualErrors()).append("</dd>");
        stats.append("</dl>");
        return stats;
    }

    private StringBuffer getTestExecutionResults() throws RDFWriterException {
        StringBuffer results = new StringBuffer();
        results.append("<h3>Results</h3>");
        results.append("<table id=\"myTable\" class=\"tablesorter tablesorter-default table\"><thead>");
        results.append(getResultsHeader());
        results.append("</thead><tbody>");
        results.append(getResultsList());
        results.append("</tbody></table>");
        return results;
    }
}
