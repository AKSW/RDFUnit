package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;


public class RdfHtmlResultsRlogWriter extends RdfHtmlResultsWriter {

    public RdfHtmlResultsRlogWriter(TestExecution testExecution, OutputStream outputStream) {
        super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Level</th><th>Message</th><th>Resource</th><th>Test Case</th></tr>");
    }

    @Override
    protected StringBuffer getResultsList() {
        StringBuffer htmlString = new StringBuffer();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</td><td><a href=\"%s\">%s</a></td><td>%s</td></tr>";

        testExecution.getTestCaseResults().stream()
                .map(RLOGTestCaseResult.class::cast)
                .forEach(result -> printResult(htmlString, template, result));
        return htmlString;
    }

    private StringBuffer printResult(StringBuffer htmlString, String template, RLOGTestCaseResult result) {
        return htmlString.append(
                String.format(template,
                        RdfHtmlResultsShaclWriter.getStatusClass(result.getSeverity()),
                        "<a href=\"" + result.getSeverity().getUri() + "\">" + result.getSeverity().name() + "</a>",
                        result.getMessage(),
                        result.getFailingResource(), result.getFailingResource(), // <a href=%s>%s</a>
                        result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"))
                );
    }

}
