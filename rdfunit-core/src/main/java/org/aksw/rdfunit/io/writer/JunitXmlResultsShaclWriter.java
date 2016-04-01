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
public class JunitXmlResultsShaclWriter extends AbstractJunitXmlResultsWriter {

    public JunitXmlResultsShaclWriter(TestExecution testExecution, OutputStream outputStream) {
    	super(testExecution, outputStream);
    }

    @Override
    protected StringBuilder getResultsList() {
        StringBuilder results = new StringBuilder();
        String template = "\t<testcase name=\"%s\" classname=\"%s\">\n";

        testExecution.getTestCaseResults().stream()
                .map(SimpleShaclTestCaseResult.class::cast)
                .forEach( result -> printResult(results, template, result));

        return results;
    }

    private void printResult(StringBuilder results, String template, SimpleShaclTestCaseResult result) {
        String testcaseElement = String.format(template,
                result.getTestCaseUri().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"),
                result.getFailingResource());
        results.append(testcaseElement);

        if(result.getSeverity().equals(RLOGLevel.ERROR)||
                result.getSeverity().equals(RLOGLevel.FATAL)) {
            results.append("\t\t<failure message=\"").append(result.getMessage()).append("\" type=\"").append(result.getSeverity().name()).append("\"/>\n");
        }
        results.append("\t</testcase>\n");
    }

}
