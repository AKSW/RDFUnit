package org.aksw.rdfunit.io.writer;

import java.io.OutputStream;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

/**
 * <p>JunitXMLResultsRlogWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class JunitXmlResultsRlogWriter extends JunitXmlResultsWriter {
	
	public JunitXmlResultsRlogWriter(TestExecution testExecution, String filename) {
    	super(testExecution, filename);
    }

    public JunitXmlResultsRlogWriter(TestExecution testExecution, OutputStream outputStream) {
    	super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsList() {
        StringBuffer results = new StringBuffer();
        String template = "\t<testcase name=\"%s\" classname=\"%s\">\n";
        
        for(TestCaseResult result : testExecution.getTestCaseResults()) {
        	RLOGTestCaseResult rlogResult = (RLOGTestCaseResult) result;
        	String testcaseElement = String.format(template,
        			rlogResult.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"),
        			rlogResult.getFailingResource());
            results.append(testcaseElement);
        
            if(rlogResult.getSeverity().equals(RLOGLevel.ERROR)||
            		rlogResult.getSeverity().equals(RLOGLevel.FATAL)) {
            	results.append("\t\t<failure message=\""+rlogResult.getMessage()+"\" type=\""+rlogResult.getSeverity().name()+"\"/>\n");
            }
            results.append("\t</testcase>\n");
        }

        return results;
    }

}
