package org.aksw.rdfunit.io;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Dimitris Kontokostas
 * Writes results in HTML format
 * Created: 11/14/13 1:04 PM
 */
public abstract class HTMLResultsWriter extends DataWriter {
    private final String filename;


    public HTMLResultsWriter(String filename) {
        this.filename = filename;
    }

    public static HTMLResultsWriter create(TestCaseExecutionType type, String filename) {
        switch (type) {
            //case statusTestCaseResult:
            //case aggregatedTestCaseResult:
            case rlogTestCaseResult:
                return new HTMLResultsRlogWriter(filename);
            //case extendedTestCaseResult:
            default:
                return null;
        }
    }

    @Override
    public void write(Model model) {
        QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);
        final Collection<String> testExecutionURIs = getTestExecutionURI(qef);

        File file = new File("path/to/file.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            writer.append(getHeader());

            for (String te: testExecutionURIs){
                writer.append(getTestExecutionStats(qef, te));
                writer.append(getTestExecutionResults(qef, te));
                break; // For now print only one (this is the case at the moment)
            }

            writer.append(getFooter());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract StringBuffer getResultsHeader();
    protected abstract StringBuffer getResultsList(QueryExecutionFactory qef, String testExecutionURI);

    private StringBuffer getHeader() {
        StringBuffer header = new StringBuffer();
        header.append("<!DOCTYPE html><html><head>");
        header.append("<link href=\"http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\" rel=\"stylesheet\">");
        header.append("</head><body>");
        return header;
    }

    private StringBuffer getFooter() {
        return new StringBuffer("</body></html>");
    }

    private Collection<String> getTestExecutionURI(QueryExecutionFactory qef) {
        ArrayList<String> executionURIs = new ArrayList<String>();
        String sparql =
                RDFUnitUtils.getAllPrefixes() +
                " SELECT DISTINCT ?testExecution WHERE { ?testExecution a rut:TestExecution } ";

        QueryExecution qe = null;

        try {
            qe = qef.createQueryExecution(sparql);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();
                String te = qs.get("testExecution").toString();
                executionURIs.add(te);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qe != null)
                qe.close();
        }
        return executionURIs;
    }

    private StringBuffer getTestExecutionStats(QueryExecutionFactory qef, String testExecution) {
        StringBuffer stats = new StringBuffer();
        stats.append("<h1>TestExecution: " + testExecution + "</h1>");

        return stats;
    }

    private StringBuffer getTestExecutionResults(QueryExecutionFactory qef, String testExecution) {
        StringBuffer results = new StringBuffer();
        results.append("<h2>Results</h2>");
        results.append("<table>");
        results.append(getResultsHeader());
        results.append(getResultsList(qef,testExecution));
        results.append("</table>");
        return results;
    }
}
