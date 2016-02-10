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


    public JunitXmlResultsAggregateWriter(TestExecution testExecution, String filename) {
    	super(testExecution, filename);
    }

    public JunitXmlResultsAggregateWriter(TestExecution testExecution, OutputStream outputStream) {
    	super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsList() {
        StringBuffer results = new StringBuffer();
        String template = "\t<testcase name=\"%s\" classname=\""+testExecution.getTestExecutionUri()+"\">\n";

        testExecution.getTestCaseResults().stream()
                .map(AggregatedTestCaseResult.class::cast)
                .forEach( result -> {
                    String testcaseElement = String.format(template,
                            result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"));
                    results.append(testcaseElement);

                    if(result.getStatus().equals(TestCaseResultStatus.Fail)) {
                        results.append("\t\t<failure message=\""+result.getMessage()+"\" type=\""+result.getSeverity().name()+"\"/>\n");
                        results.append("\t\t<system-out>Errors:"+result.getErrorCount()+" Prevalence:"+result.getPrevalenceCount().orElse(-1L)+"</system-out>\n");
                    } else if(result.getStatus().equals(TestCaseResultStatus.Error)||result.getStatus().name().equals("Timeout")) {
                        results.append("\t\t<error message=\""+result.getMessage()+"\" type=\""+result.getStatus().name()+"\"/>\n");
                    }
                    results.append("\t</testcase>\n");
                });
        return results;
    }
}
