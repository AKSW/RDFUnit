package org.aksw.databugger.tests.executors;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.TestCase;
import org.aksw.databugger.tests.results.RLOGTestCaseResult;
import org.aksw.databugger.tests.results.TestCaseResult;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Executes results for RLOGTestCaseResult sets
 * Created: 2/2/14 4:25 PM
 */
public class RLOGTestExecutor extends TestExecutor {

    @Override
    protected List<TestCaseResult> executeSingleTest(Source source, TestCase testCase) {

        List<TestCaseResult> testCaseResults = new ArrayList<TestCaseResult>();

        QueryExecution qe = null;
        try {
            qe = source.getExecutionFactory().createQueryExecution(testCase.getSparqlAnnotatedQuery());
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                testCaseResults.add(generateSingleResult(results.next(), testCase));

            }
        } catch (Exception e) {
            // TODO check what to do
        } finally {
            if (qe != null)
                qe.close();
        }

        return testCaseResults;

    }

    protected TestCaseResult generateSingleResult(QuerySolution qs, TestCase testCase) {
        String resource = qs.get("resource").toString();
        String message = testCase.getResultMessage();
        if (qs.contains("message"))
            message = qs.get("message").toString();
        String logLevel = testCase.getLogLevel();

        return new RLOGTestCaseResult(testCase, resource, message, logLevel);

    }
}
