package org.aksw.rdfunit.tests.executors;


import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.impl.results.ShaclLiteTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.SparqlUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.util.ArrayList;
import java.util.Collection;


/**
 * The Simple SHACL Executor returns violation instances and for every instance it generates an sd:Violation:Entry
 * See the rlog vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 4:25 PM

 */
public class ShaclSimpleTestExecutor extends TestExecutor {

    @Override
    TestCaseExecutionType getExecutionType() {
        return TestCaseExecutionType.shaclLiteTestCaseResult;
    }

    /**
     * Instantiates a new SimpleShaclTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public ShaclSimpleTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }


    @Override
    protected Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase) throws TestCaseExecutionException {

        Collection<TestCaseResult> testCaseResults = new ArrayList<>();


        try (QueryExecution qe = testSource.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase)))
        {

            qe.execSelect().forEachRemaining( qs -> {

                RDFNode focusNode = qs.get("this");
                String message = testCase.getResultMessage();
                if (qs.contains("message")) {
                    message = qs.get("message").toString();
                }
                RLOGLevel logLevel = testCase.getLogLevel();

                testCaseResults.add(new ShaclLiteTestCaseResultImpl(testCase.getElement(), logLevel, message, focusNode));
            });
        } catch (QueryExceptionHTTP e) {
            checkQueryResultStatus(e);
        }

        return testCaseResults;

    }

    protected void checkQueryResultStatus(QueryExceptionHTTP e) throws TestCaseExecutionException {
        if (SparqlUtils.checkStatusForTimeout(e)) {
            throw new TestCaseExecutionException(TestCaseResultStatus.Timeout, e);
        } else {
            throw new TestCaseExecutionException(TestCaseResultStatus.Error, e);
        }
    }
}
