package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

public class RDFHtmlResultsStatusWriter extends RDFHtmlResultsWriter {

    public RDFHtmlResultsStatusWriter(TestExecution testExecution, String filename) {
        super(testExecution, filename);
    }

    public RDFHtmlResultsStatusWriter(TestExecution testExecution, OutputStream outputStream) {
        super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Status</th><th>Level</th><th>Test Case</th><th>Description</th></tr>\n");
    }

    @Override
    protected StringBuffer getResultsList()  {
        StringBuffer results = new StringBuffer();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n";

        testExecution.getTestCaseResults().stream()
                .map(StatusTestCaseResult.class::cast)
                .forEach(result -> results.append(
                    String.format(template,
                        getStatusClass(result.getStatus()),
                        "<a href=\"" + result.getStatus().getUri() + "\">" + result.getStatus().name() + "</a>",
                        "<a href=\"" + result.getSeverity().getUri() + "\">" + result.getSeverity().name() + "</a>",
                        result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"),
                        result.getMessage()
                )));

        return results;
    }

    /**
     * return a css class
     */
    protected String getStatusClass(TestCaseResultStatus status) {

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
