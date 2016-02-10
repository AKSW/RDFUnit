package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.SimpleShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.OutputStream;

/**
 * <p>JunitXMLResultsShaclWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class JunitXmlResultsShaclWriter extends JunitXmlResultsWriter {

    public JunitXmlResultsShaclWriter(TestExecution testExecution, String filename) {
    	super(testExecution, filename);
    }

    public JunitXmlResultsShaclWriter(TestExecution testExecution, OutputStream outputStream) {
    	super(testExecution, outputStream);
    }

    @Override
    protected StringBuffer getResultsList() {
        StringBuffer results = new StringBuffer();
        String template = "\t<testcase name=\"%s\" classname=\"%s\">\n";

        testExecution.getTestCaseResults().stream()
                .map(SimpleShaclTestCaseResult.class::cast)
                .forEach( result -> {
                    String testcaseElement = String.format(template,
                            result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"),
                            result.getFailingResource());
                    results.append(testcaseElement);

                    if(result.getSeverity().equals(RLOGLevel.ERROR)||
                            result.getSeverity().equals(RLOGLevel.FATAL)) {
                        results.append("\t\t<failure message=\""+result.getMessage()+"\" type=\""+result.getSeverity().name()+"\"/>\n");
                    }
                    results.append("\t</testcase>\n");
                });

        return results;
    }

}
