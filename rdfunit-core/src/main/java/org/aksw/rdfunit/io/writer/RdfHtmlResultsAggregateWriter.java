package org.aksw.rdfunit.io.writer;

import com.google.common.html.HtmlEscapers;
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
public class RdfHtmlResultsAggregateWriter extends RdfHtmlResultsStatusWriter {


    public RdfHtmlResultsAggregateWriter(TestExecution testExecution, OutputStream outputStream) {
        super(testExecution, outputStream);
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuilder getResultsHeader() {
        return new StringBuilder("<tr><th>Status</th><th>Level</th><th>Test Case</th><th>Errors</th><th>Prevalence</th></tr>\n");
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuilder getResultsList() {
        StringBuilder htmlString = new StringBuilder();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</td><td><span title=\"%s\">%s</span></td><td>%s</td><td>%s</td></tr>\n";

        testExecution.getTestCaseResults().stream()
            .map(AggregatedTestCaseResult.class::cast)
            .forEach(result -> printResult(htmlString, template, result));

        return htmlString;

    }

    private StringBuilder printResult(StringBuilder htmlString, String template, AggregatedTestCaseResult result) {
        return htmlString.append(
            String.format(template,
                    getStatusClass(result.getStatus()),
                    "<a href=\"" + result.getStatus().getUri() + "\">" + result.getStatus().name() + "</a>",
                    "<a href=\"" + result.getSeverity().getUri() + "\">" + result.getSeverity().name() + "</a>",
                    result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"),
                    HtmlEscapers.htmlEscaper().escape(result.getMessage()),
                    result.getErrorCount(),
                    result.getPrevalenceCount().orElse(-1L)
            ));
    }
}
