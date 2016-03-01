package org.aksw.rdfunit.tests.executors;


import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.impl.results.SimpleShaclTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.SparqlUtils;
import org.aksw.rdfunit.utils.StringUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

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


        try (QueryExecution qe = testSource.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase)))
        {

            qe.execSelect().forEachRemaining( qs -> {

                String resource = qs.get("this").toString();
                if (qs.get("this").isLiteral()) {
                    resource = StringUtils.getHashFromString(resource);
                }
                String message = testCase.getResultMessage();
                if (qs.contains("message")) {
                    message = qs.get("message").toString();
                }
                RLOGLevel logLevel = testCase.getLogLevel();

                testCaseResults.add(new SimpleShaclTestCaseResultImpl(testCase.getTestURI(), logLevel, message, resource));
            });
        } catch (QueryExceptionHTTP e) {
            checkQueryResultStatus(e);
        }

        return testCaseResults;

    }

    /**
     * <p>checkQueryResultStatus.</p>
     *
     * @param e a {@link org.apache.jena.sparql.engine.http.QueryExceptionHTTP} object.
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
