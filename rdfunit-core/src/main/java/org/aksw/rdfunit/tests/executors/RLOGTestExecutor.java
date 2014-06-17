package org.aksw.rdfunit.tests.executors;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import org.aksw.rdfunit.Utils.SparqlUtils;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.RLOGTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.util.ArrayList;


/**
 * User: Dimitris Kontokostas
 * Executes results for RLOGTestCaseResult sets
 * Created: 2/2/14 4:25 PM
 */
public class RLOGTestExecutor extends TestExecutor {

    @Override
    protected java.util.Collection<TestCaseResult> executeSingleTest(Source source, TestCase testCase) throws TestCaseExecutionException {

        java.util.Collection<TestCaseResult> testCaseResults = new ArrayList<>();

        QueryExecution qe = null;
        try {
            qe = source.getExecutionFactory().createQueryExecution(testCase.getSparqlAnnotatedQuery());
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                testCaseResults.add(generateSingleResult(results.next(), testCase));

            }
        } catch (QueryExceptionHTTP e) {
            if (SparqlUtils.checkStatusForTimeout(e)) {
                throw new TestCaseExecutionException(TestCaseResultStatus.Timeout, e);
            } else {
                throw new TestCaseExecutionException(TestCaseResultStatus.Error, e);
            }
        } catch (Exception e) {
            throw new TestCaseExecutionException(TestCaseResultStatus.Error, e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return testCaseResults;

    }

    protected TestCaseResult generateSingleResult(QuerySolution qs, TestCase testCase) {
        String resource = qs.get("resource").toString();
        String message = testCase.getResultMessage();
        if (qs.contains("message")) {
            message = qs.get("message").toString();
        }
        RLOGLevel logLevel = testCase.getLogLevel();

        return new RLOGTestCaseResult(testCase, resource, message, logLevel);

    }
}
