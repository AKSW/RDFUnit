package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;


public class RDFHTMLResultsRlogWriter extends RDFHTMLResultsWriter {

    public RDFHTMLResultsRlogWriter(TestExecution testExecution, String filename) {
        super(testExecution, filename);
    }

    public RDFHTMLResultsRlogWriter(TestExecution testExecution, OutputStream outputStream) {
        super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Level</th><th>Message</th><th>Resource</th><th>Test Case</th></tr>");
    }

    @Override
    protected StringBuffer getResultsList() throws RDFWriterException {
        StringBuffer results = new StringBuffer();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</ts><td><a href=\"%s\">%s</a></td><td>%s</td></tr>";

        testExecution.getTestCaseResults().stream()
                .map(RLOGTestCaseResult.class::cast)
                .forEach(result -> results.append(
                        String.format(template,
                                RDFHTMLResultsShaclWriter.getStatusClass(result.getSeverity()),
                                "<a href=\"" + result.getSeverity().getUri() + "\">" + result.getSeverity().name() + "</a>",
                                result.getMessage(),
                                result.getFailingResource(), result.getFailingResource(), // <a href=%s>%s</a>
                                result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"))
                        ));
        return results;
    }

    /**
     * return a css class
     */
    private String getStatusClass(RLOGLevel level) {
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
