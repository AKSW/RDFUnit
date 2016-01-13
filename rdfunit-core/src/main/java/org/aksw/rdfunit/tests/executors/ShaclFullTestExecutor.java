package org.aksw.rdfunit.tests.executors;


import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.impl.results.ShaclTestCaseResultImpl;
import org.aksw.rdfunit.model.impl.results.SimpleShaclTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.ExtendedTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 2 /2/14 6:13 PM
 * @version $Id: $Id
 */
public class ShaclFullTestExecutor extends ExtendedTestExecutor {

    /**
     * Instantiates a new ExtendedTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public ShaclFullTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    @Override
    protected Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase) throws TestCaseExecutionException {
        Collection<TestCaseResult> results = super.executeSingleTest(testSource, testCase);
        Collection<TestCaseResult> resultsCopy = new ArrayList<>();
        for (TestCaseResult res: results) {
            if (res instanceof ExtendedTestCaseResult) {
                resultsCopy.add(
                        new ShaclTestCaseResultImpl(
                                res.getTestCaseUri(),
                                res.getSeverity(),
                                res.getMessage(),
                                ((ExtendedTestCaseResult) res).getFailingResource(),
                                ((ExtendedTestCaseResult) res).getResultAnnotations()));
                                //((ExtendedTestCaseResult) res).getResource(),
                                //((ExtendedTestCaseResult) res).getResultAnnotations()));
            }
            else {
                resultsCopy.add(res);
            }
        }
        return resultsCopy;
    }
}
