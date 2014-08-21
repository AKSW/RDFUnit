package org.aksw.rdfunit.tests.executors;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import org.aksw.rdfunit.Utils.SparqlUtils;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.QueryGenerationFactory;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.StatusTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.util.Arrays;
import java.util.Collection;


/**
 * Test Executor that reports only a status (Success, Fail, Timeout or Error) for every test case and nothing more
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:49 PM
 */
public class StatusTestExecutor extends TestExecutor {
    /**
     * Instantiates a new StatusTestExecutor.
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public StatusTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    @Override
    protected Collection<TestCaseResult> executeSingleTest(Source source, TestCase testCase) throws TestCaseExecutionException {

        TestCaseResultStatus status = TestCaseResultStatus.Error;
        QueryExecution qe = null;

        try {
            qe = source.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase));
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
            else {
                status = TestCaseResultStatus.Error;
            }

        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return Arrays.<TestCaseResult>asList(new StatusTestCaseResult(testCase, status));
    }

}
