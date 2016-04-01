package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.impl.results.StatusTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.SparqlUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.util.Collection;
import java.util.Collections;


/**
 * Test Executor that reports only a status (Success, Fail, Timeout or Error) for every test case and nothing more
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:49 PM
 * @version $Id: $Id
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

    /** {@inheritDoc} */
    @Override
    protected Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase) throws TestCaseExecutionException {

        TestCaseResultStatus status = TestCaseResultStatus.Error;


        try (QueryExecution qe = testSource.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase))) {
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
            } else {
                status = TestCaseResultStatus.Error;
            }

        }

        return Collections.singletonList(new StatusTestCaseResultImpl(testCase, status));
    }

}
