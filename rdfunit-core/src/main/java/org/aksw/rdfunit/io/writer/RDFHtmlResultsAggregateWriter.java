package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.model.interfaces.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * <p>RDFHTMLResultsAggregateWriter class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class RDFHtmlResultsAggregateWriter extends RDFHtmlResultsStatusWriter {

    /**
     * <p>Constructor for RDFHTMLResultsAggregateWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public RDFHtmlResultsAggregateWriter(TestExecution testExecution, String filename) {
        super(testExecution, filename);
    }

    /**
     * <p>Constructor for RDFHTMLResultsAggregateWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public RDFHtmlResultsAggregateWriter(TestExecution testExecution, OutputStream outputStream) {
        super(testExecution, outputStream);
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Status</th><th>Level</th><th>Test Case</th><th>Errors</th><th>Prevalence</th></tr>\n");
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuffer getResultsList() {
        StringBuffer results = new StringBuffer();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</td><td><span title=\"%s\">%s</span></td><td>%s</td><td>%s</td></tr>\n";

        testExecution.getTestCaseResults().stream()
            .map(AggregatedTestCaseResult.class::cast)
            .forEach(result -> results.append(
                String.format(template,
                        getStatusClass(result.getStatus()),
                        "<a href=\"" + result.getStatus().getUri() + "\">" + result.getStatus().name() + "</a>",
                        "<a href=\"" + result.getSeverity().getUri() + "\">" + result.getSeverity().name() + "</a>",
                        result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"),
                        result.getMessage(),
                        result.getErrorCount(),
                        result.getPrevalenceCount().orElse(-1L)
                )));

        return results;

    }
}
