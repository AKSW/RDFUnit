package org.aksw.rdfunit.io.writer;

import com.google.common.html.HtmlEscapers;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * @author Dimitris Kontokostas
 * @since 4/23/14 8:55 AM
 */
public class RdfHtmlResultsShaclWriter extends AbstractRdfHtmlResultsWriter {


    public RdfHtmlResultsShaclWriter(TestExecution testExecution, OutputStream outputStream) {
        super(testExecution, outputStream);
    }


    @Override
    protected StringBuilder getResultsHeader() {
        return new StringBuilder("<tr><th>Level</th><th>Message</th><th>Resource</th><th>Test Case</th></tr>\n");
    }


    @Override
    protected StringBuilder getResultsList() {
        StringBuilder htmlString = new StringBuilder();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</td><td><a href=\"%s\">%s</a></td><td>%s</td></tr>\n";

        testExecution.getTestCaseResults().stream()
                .map(ShaclLiteTestCaseResult.class::cast)
                .forEach(result -> printResult(htmlString, template, result));
        return htmlString;
    }

    private StringBuilder printResult(StringBuilder htmlString, String template, ShaclLiteTestCaseResult result) {
        return htmlString.append(
                String.format(template,
                        getStatusClass(result.getSeverity()),
                        "<a href=\"" + result.getSeverity().getUri() + "\">" + result.getSeverity().name() + "</a>",
                        HtmlEscapers.htmlEscaper().escape(result.getMessage()),
                        result.getFailingNode(), result.getFailingNode(), // <a href=%s>%s</a>
                        result.getTestCaseUri().toString().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"))
        );
    }

    /**
     * return a css class
     */
    protected static String getStatusClass(RLOGLevel level) {
        String rowClass = "";
        switch (level) {
            case WARN:
                rowClass = "warning";
                break;
            case ERROR:
                rowClass = "danger";
                break;
            case INFO:
                rowClass = "info";
                break;
            default:
                break;
        }
        return rowClass;
    }
}
