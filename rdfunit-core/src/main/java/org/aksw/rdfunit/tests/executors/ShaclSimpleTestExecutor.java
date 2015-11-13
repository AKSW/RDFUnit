package org.aksw.rdfunit.tests.executors;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.results.SimpleShaclTestCaseResult;
import org.aksw.rdfunit.model.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.SparqlUtils;
import org.aksw.rdfunit.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;


/**
 * The Simple SHACL Executor returns violation instances and for every instance it generates an sd:Violation:Entry
 * See the rlog vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 4:25 PM
 * @version $Id: $Id
 */
public class ShaclSimpleTestExecutor extends TestExecutor {

    /**
     * Instantiates a new SimpleShaclTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public ShaclSimpleTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    /** {@inheritDoc} */
    @Override
    protected Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase) throws TestCaseExecutionException {

        Collection<TestCaseResult> testCaseResults = new ArrayList<>();

        QueryExecution qe = null;
        try {
            qe = testSource.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase));
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {

                QuerySolution qs = results.next();

                String resource = qs.get("resource").toString();
                if (qs.get("resource").isLiteral()) {
                    resource = StringUtils.getHashFromString(resource);
                }
                String message = testCase.getResultMessage();
                if (qs.contains("message")) {
                    message = qs.get("message").toString();
                }
                RLOGLevel logLevel = testCase.getLogLevel();

                testCaseResults.add(new SimpleShaclTestCaseResult(testCase, resource, message, logLevel));
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
     * @param e a {@link QueryExceptionHTTP} object.
     * @throws TestCaseExecutionException if any.
     */
    protected void checkQueryResultStatus(QueryExceptionHTTP e) throws TestCaseExecutionException {
        if (SparqlUtils.checkStatusForTimeout(e)) {
            throw new TestCaseExecutionException(TestCaseResultStatus.Timeout, e);
        } else {
            throw new TestCaseExecutionException(TestCaseResultStatus.Error, e);
        }
    }
}
