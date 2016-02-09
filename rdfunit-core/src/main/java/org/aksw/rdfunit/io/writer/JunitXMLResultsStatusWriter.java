package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.io.OutputStream;

/**
 * <p>RDFHTMLResultsStatusWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class JunitXMLResultsStatusWriter extends JunitXMLResultsWriter {

    /**
     * <p>Constructor for RDFHTMLResultsStatusWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public JunitXMLResultsStatusWriter(String filename) {
        super(filename);
    }

    /**
     * <p>Constructor for RDFHTMLResultsStatusWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public JunitXMLResultsStatusWriter(OutputStream outputStream) {
        super(outputStream);
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuffer getResultsHeader() {
		return null;
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuffer getResultsList(QueryExecutionFactory qef, String testExecutionURI) throws RDFWriterException {
        StringBuffer results = new StringBuffer();
        String template = " <testcase name=\"%s\" classname=\""+testExecutionURI+"\">";

        String sparql = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?resultStatus ?testcase ?level ?description WHERE {" +
                " ?s a rut:StatusTestCaseResult ; " +
                "    rut:resultStatus ?resultStatus ; " +
                "    rut:testCaseLogLevel ?level ; " +
                "    rut:testCase ?testcase ;" +
                "    dcterms:description ?description ;" +
                "} ";

        QueryExecution qe = null;

        try {
            qe = qef.createQueryExecution(sparql);
            ResultSet rs = qe.execSelect();

            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String resultStatus = qs.get("resultStatus").toString();
                String testcase = qs.get("testcase").toString();
                String description = qs.get("description").toString();
                String level = qs.get("level").toString();

                String statusShort = resultStatus.replace(PrefixNSService.getNSFromPrefix("rut") + "ResultStatus", "");
                String levelShort = PrefixNSService.getLocalName(level, "rlog");
      
                String testcaseElement = String.format(template,
                		testcase.replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"));
                
                results.append(testcaseElement);
                if(statusShort.equals("Fail")) {
                	results.append("  <failure message=\""+description+"\" type=\""+levelShort+"\"/>\n");
                } else if(statusShort.equals("Error")||statusShort.equals("Timeout")) {
                	results.append("\t\t<error message=\""+description+"\" type=\""+statusShort+"\"/>\n");
                }
                results.append(" </testcase>\n");
                
            }

        } catch (Exception e) {
            throw new RDFWriterException(e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return results;
    }

    /**
     * <p>getStatusClass.</p>
     *
     * @param status a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected String getStatusClass(String status) {

        switch (status) {
            case "Success":
                return "success";
            case "Fail":
                return "danger";
            case "Timeout":
                return "warning";
            case "Error":
                return "warning";
            default:
                return "";
        }
    }

}
