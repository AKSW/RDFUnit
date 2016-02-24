package org.aksw.rdfunit.io.writer;

import com.google.common.html.HtmlEscapers;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

public class RdfHtmlResultsStatusWriter extends RdfHtmlResultsWriter {

    public RdfHtmlResultsStatusWriter(TestExecution testExecution, OutputStream outputStream) {
        super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Status</th><th>Level</th><th>Test Case</th><th>Description</th></tr>\n");
    }

    @Override
    protected StringBuffer getResultsList()  {
        StringBuffer htmlString = new StringBuffer();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n";

        testExecution.getTestCaseResults().stream()
                .map(StatusTestCaseResult.class::cast)
                .forEach(result -> printResult(htmlString, template, result));

        return htmlString;
    }

    private StringBuffer printResult(StringBuffer htmlString, String template, StatusTestCaseResult result) {
        return htmlString.append(
            String.format(template,
                getStatusClass(result.getStatus()),
                "<a href=\"" + result.getStatus().getUri() + "\">" + result.getStatus().name() + "</a>",
                "<a href=\"" + result.getSeverity().getUri() + "\">" + result.getSeverity().name() + "</a>",
                result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"),
                HtmlEscapers.htmlEscaper().escape(result.getMessage())
        ));
    }

    /**
     * return a css class
     */
    String getStatusClass(TestCaseResultStatus status) {

        switch (status) {
            case Success:
                return "success";
            case Fail:
                return "danger";
            case Timeout:
                return "warning";
            case Error:
                return "warning";
            default:
                return "";
        }
    }

}
