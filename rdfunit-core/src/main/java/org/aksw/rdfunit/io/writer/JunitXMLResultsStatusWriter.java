package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * <p>JunitXMLResultsStatusWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class JunitXMLResultsStatusWriter extends JunitXMLResultsWriter {

    public JunitXMLResultsStatusWriter(TestExecution testExecution, String filename) {
    	super(testExecution, filename);
    }

    public JunitXMLResultsStatusWriter(TestExecution testExecution, OutputStream outputStream) {
    	super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsList() {
        StringBuffer results = new StringBuffer();
        String template = "\t<testcase name=\"%s\" classname=\""+testExecution.getTestExecutionUri()+"\">\n";
        
        for(TestCaseResult result : testExecution.getTestCaseResults()) {
        	AggregatedTestCaseResult aggregatedResult = (AggregatedTestCaseResult) result;
        	String testcaseElement = String.format(template,
        			result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"));
            results.append(testcaseElement);

            if(aggregatedResult.getStatus().equals(TestCaseResultStatus.Fail)) {
            	results.append("\t\t<failure message=\""+aggregatedResult.getMessage()+"\" type=\""+aggregatedResult.getSeverity().name()+"\"/>\n");
            } else if(aggregatedResult.getStatus().equals(TestCaseResultStatus.Error)||aggregatedResult.getStatus().equals(TestCaseResultStatus.Timeout)) {
            	results.append("\t\t<error message=\""+aggregatedResult.getMessage()+"\" type=\""+aggregatedResult.getStatus().name()+"\"/>\n");
            }
            results.append("\t</testcase>\n");
        }

        return results;
    }

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
