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
public abstract class AbstractRdfHtmlResultsWriter implements RdfWriter {
    protected final TestExecution testExecution;
    private final OutputStream outputStream;


    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public AbstractRdfHtmlResultsWriter(TestExecution testExecution, OutputStream outputStream) {
        super();
        this.testExecution = testExecution;
        this.outputStream = outputStream;
    }

    /**
     * <p>Constructor for RDFHTMLResultsWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public AbstractRdfHtmlResultsWriter(TestExecution testExecution, String filename) {
        this(testExecution, RdfStreamWriter.getOutputStreamFromFilename(filename));
    }

    /** {@inheritDoc} */
    @Override
    public void write(QueryExecutionFactory qef) throws RdfWriterException {

        try {
            // TODO not efficient StringBuilder.toString().getBytes()
            final String utf8 = "UTF8";
            outputStream.write(getHeader().toString().getBytes(utf8));

            outputStream.write(getTestExecutionStats().toString().getBytes(utf8));
            outputStream.write(getTestExecutionResults().toString().getBytes(utf8));
                // break; // For now print only one (this is the case at the moment)

            outputStream.write(getFooter().toString().getBytes(utf8));
            outputStream.close();

        } catch (IOException e) {
            throw new RdfWriterException("Cannot write HTML", e);
        }
    }


    protected abstract StringBuilder getResultsHeader();

    protected abstract StringBuilder getResultsList() ;

    private StringBuilder getHeader() {
        StringBuilder header = new StringBuilder();
        header
                .append("<!DOCTYPE html>\n<html><head>\n")
                .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\">\n")
                .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"http://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.16.4/css/theme.default.css\" >\n")
                .append("<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-1.11.0.min.js\"></script>\n")
                .append("<script type=\"text/javascript\" src=\"http://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.16.4/jquery.tablesorter.min.js\"></script>\n")
                .append("<script type=\"text/javascript\"> $(function() {$(\"#myTable\").tablesorter();});</script>")
                .append("<title>RDFUnit validation results</title>")
                .append("</head><body>\n");
        return header;
    }

    private StringBuilder getFooter() {
        return new StringBuilder("</body>\n</html>");
    }

    private StringBuilder getTestExecutionStats() throws RdfWriterException {
        StringBuilder stats = new StringBuilder();
        stats.append("<h2>TestExecution: ").append(testExecution.getTestExecutionUri()).append("</h2>\n");
        //TODO for some reason, using the "testExecution" URI does not work :/

        DatasetOverviewResults dor = testExecution.getDatasetOverviewResults();

        stats
                .append("<dl class=\"dl-horizontal\">\n")
                .append("<dt>Dataset</dt><dd> ").append(testExecution.getTestedDatasetUri()).append("</dd>\n")
                //.append("<dt>Test suite</dt><dd>").append(used).append("</dd>");
                .append("<dt>Test execution started</dt><dd> ").append(dor.getStartTime()).append("</dd>\n")
                .append("<dt>-ended</dt><dd> ").append(dor.getEndTime()).append("</dd>\n")
                .append("<dt>Total test cases</dt><dd> ").append(dor.getTotalTests()).append("</dd>\n")
                .append("<dt>Succeeded</dt><dd> ").append(dor.getSuccessfulTests()).append("</dd>\n")
                .append("<dt>Failed</dt><dd> ").append(dor.getFailedTests()).append("</dd>\n")
                .append("<dt>Timeout / Error </dt><dd> T:").append(dor.getTimeoutTests()).append(" / E: ").append(dor.getErrorTests()).append("</dd>\n")
                .append("<dt>Violation instances</dt><dd> ").append(dor.getIndividualErrors()).append("</dd>\n")
                .append("</dl>\n");
        return stats;
    }

    private StringBuilder getTestExecutionResults() throws RdfWriterException {
        StringBuilder results = new StringBuilder();
        results
                .append("<h3>Results</h3>\n")
                .append("<table id=\"myTable\" class=\"tablesorter tablesorter-default table\" summary=\"Detailed results\"><thead>\n")
                .append(getResultsHeader())
                .append("</thead><tbody>\n")
                .append(getResultsList())
                .append("</tbody></table>\n");
        return results;
    }
}
