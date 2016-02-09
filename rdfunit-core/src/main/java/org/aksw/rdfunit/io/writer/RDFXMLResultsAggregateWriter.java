package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import java.io.OutputStream;

/**
 * <p>RDFXMLResultsAggregateWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Description
 * @since 4/23/14 8:55 AM
 * @version $Id: $Id
 */
public class RDFXMLResultsAggregateWriter extends RDFXMLResultsStatusWriter {
    /**
     * <p>Constructor for RDFHTMLResultsAggregateWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public RDFXMLResultsAggregateWriter(String filename) {
        super(filename);
    }

    /**
     * <p>Constructor for RDFHTMLResultsAggregateWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public RDFXMLResultsAggregateWriter(OutputStream outputStream) {
        super(outputStream);
    }

    /** {@inheritDoc} */
    @Override
    protected StringBuffer getResultsList(QueryExecutionFactory qef, String testExecutionURI) throws RDFWriterException {
        StringBuffer results = new StringBuffer();
        String template = "\t<testcase name=\"%s\" classname=\""+testExecutionURI+"\">\n";

        String sparql = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?resultStatus ?level ?testcase ?description ?resultCount ?resultPrevalence WHERE {" +
                " ?s a rut:AggregatedTestResult ; " +
                "    rut:resultStatus ?resultStatus ; " +
                "    rut:testCaseLogLevel ?level ; " +
                "    rut:testCase ?testcase ;" +
                "    dcterms:description ?description ;" +
                "    rut:resultCount ?resultCount ; " +
                "    rut:resultPrevalence ?resultPrevalence . " +
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
                String resultCount = qs.get("resultCount").asLiteral().getValue().toString();
                String resultPrevalence = qs.get("resultPrevalence").asLiteral().getValue().toString();
                String level = qs.get("level").toString();

                String statusShort = resultStatus.replace(PrefixNSService.getNSFromPrefix("rut") + "ResultStatus", "");
                String levelShort = PrefixNSService.getLocalName(level, "rlog");

                String testcaseElement = String.format(template,
                		testcase.replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:"));
                
                results.append(testcaseElement);
                if(statusShort.equals("Fail")) {
                	results.append("\t\t<failure message=\""+description+"\" type=\""+levelShort+"\"/>\n");
                	results.append("\t\t<system-out>Errors:"+resultCount+" Prevalence:"+resultPrevalence+"</system-out>\n");
                } else if(statusShort.equals("Error")||statusShort.equals("Timeout")) {
                	results.append("\t\t<error message=\""+description+"\" type=\""+statusShort+"\"/>\n");
                }
                results.append("\t</testcase>\n");
                	
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
}
