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
import org.aksw.rdfunit.tests.QueryGenerationFactory;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.RLOGTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.util.ArrayList;
import java.util.Collection;


/**
 * The RLOG Executor returns violation instances and for every instance it generates an rlog:Entry
 * See the rlog vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 4:25 PM
 * @version $Id: $Id
 */
public class RLOGTestExecutor extends TestExecutor {

    /**
     * Instantiates a new RLOGTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public RLOGTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    /** {@inheritDoc} */
    @Override
    protected Collection<TestCaseResult> executeSingleTest(Source source, TestCase testCase) throws TestCaseExecutionException {

        Collection<TestCaseResult> testCaseResults = new ArrayList<>();

        QueryExecution qe = null;
        try {
            qe = source.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase));
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {

                QuerySolution qs = results.next();

                String resource = qs.get("resource").toString();
                String message = testCase.getResultMessage();
                if (qs.contains("message")) {
                    message = qs.get("message").toString();
                }
                RLOGLevel logLevel = testCase.getLogLevel();

                testCaseResults.add(new RLOGTestCaseResult(testCase, resource, message, logLevel));
            }
        } catch (QueryExceptionHTTP e) {
            checkQueryResultStatus(e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return testCaseResults;

    }

    /**
     * <p>checkQueryResultStatus.</p>
     *
     * @param e a {@link com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP} object.
     * @throws org.aksw.rdfunit.exceptions.TestCaseExecutionException if any.
     */
    protected void checkQueryResultStatus(QueryExceptionHTTP e) throws TestCaseExecutionException {
        if (SparqlUtils.checkStatusForTimeout(e)) {
            throw new TestCaseExecutionException(TestCaseResultStatus.Timeout, e);
        } else {
            throw new TestCaseExecutionException(TestCaseResultStatus.Error, e);
        }
    }
}
