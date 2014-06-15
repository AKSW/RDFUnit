package org.aksw.rdfunit.tests.executors;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import org.aksw.rdfunit.Utils.SparqlUtils;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.StatusTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.util.Arrays;


/**
 * User: Dimitris Kontokostas
 * Executes results for StatusTestCaseResult sets
 * Created: 2/2/14 3:49 PM
 */
public class StatusTestExecutor extends TestExecutor {
    @Override
    protected java.util.Collection<TestCaseResult> executeSingleTest(Source source, TestCase testCase) throws TestCaseExecutionException {

        TestCaseResultStatus status = TestCaseResultStatus.Error;
        QueryExecution qe = null;

        try {
            qe = source.getExecutionFactory().createQueryExecution(testCase.getSparqlAsAskQuery());
            boolean fail = qe.execAsk();

            if (fail) {
                status = TestCaseResultStatus.Fail;
            } else {
                status = TestCaseResultStatus.Success;
            }

        } catch (QueryExceptionHTTP e) {
            // No need to throw exception here, class supports status
            if (SparqlUtils.checkStatusForTimeout(e)) {
                status = TestCaseResultStatus.Timeout;
            }

        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return Arrays.<TestCaseResult>asList(new StatusTestCaseResult(testCase, status));
    }

}
