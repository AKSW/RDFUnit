package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * <p>JunitXMLResultsAggregateWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class JunitXmlResultsAggregateWriter extends JunitXmlResultsStatusWriter {

    public JunitXmlResultsAggregateWriter(TestExecution testExecution, OutputStream outputStream) {
    	super(testExecution, outputStream);
    }

    @Override
    protected StringBuilder getResultsList() {
        StringBuilder results = new StringBuilder();
        String template = "\t<testcase name=\"%s\" classname=\""+testExecution.getTestExecutionUri()+"\">\n";

        testExecution.getTestCaseResults().stream()
                .map(AggregatedTestCaseResult.class::cast)
                .forEach( result -> printResult(results, template, result));

        return results;
    }

    private void printResult(StringBuilder results, String template, AggregatedTestCaseResult result) {
        String testcaseElement = String.format(template,
                result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"));
        results.append(testcaseElement);

        if(result.getStatus().equals(TestCaseResultStatus.Fail)) {
            results
                    .append("\t\t<failure message=\"").append(result.getMessage()).append("\" type=\"").append(result.getSeverity().name()).append("\"/>\n")
                    .append("\t\t<system-out>Errors:").append(result.getErrorCount()).append(" Prevalence:").append(result.getPrevalenceCount().orElse(-1L)).append("</system-out>\n");
        } else if(result.getStatus().equals(TestCaseResultStatus.Error)||result.getStatus().name().equals("Timeout")) {
            results.append("\t\t<error message=\"").append(result.getMessage()).append("\" type=\"").append(result.getStatus().name()).append("\"/>\n");
        }
        results.append("\t</testcase>\n");
    }
}
