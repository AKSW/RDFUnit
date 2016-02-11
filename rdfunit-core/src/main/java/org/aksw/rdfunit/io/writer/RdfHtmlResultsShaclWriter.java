package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.SimpleShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * <p>RDFHTMLResultsRlogWriter class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class RdfHtmlResultsShaclWriter extends RdfHtmlResultsWriter {


    /**
     * <p>Constructor for RDFHTMLResultsRlogWriter.</p>
     *
     * @param outputStream a {@link OutputStream} object.
     */
    public RdfHtmlResultsShaclWriter(TestExecution testExecution, OutputStream outputStream) {
        super(testExecution, outputStream);
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuffer getResultsHeader() {
        return new StringBuffer("<tr><th>Level</th><th>Message</th><th>Resource</th><th>Test Case</th></tr>\n");
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuffer getResultsList() {
        StringBuffer htmlString = new StringBuffer();
        String template = "<tr class=\"%s\"><td>%s</td><td>%s</td><td><a href=\"%s\">%s</a></td><td>%s</td></tr>\n";

        testExecution.getTestCaseResults().stream()
                .map(SimpleShaclTestCaseResult.class::cast)
                .forEach(result -> printResult(htmlString, template, result));
        return htmlString;
    }

    private StringBuffer printResult(StringBuffer htmlString, String template, SimpleShaclTestCaseResult result) {
        return htmlString.append(
                String.format(template,
                        getStatusClass(result.getSeverity()),
                        "<a href=\"" + result.getSeverity().getUri() + "\">" + result.getSeverity().name() + "</a>",
                        result.getMessage(),
                        result.getFailingResource(), result.getFailingResource(), // <a href=%s>%s</a>
                        result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"))
        );
    }

    /**
     * return a css class
     */
    static String getStatusClass(RLOGLevel level) {
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
