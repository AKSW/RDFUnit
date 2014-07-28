package org.aksw.rdfunit.tests.executors;

import com.hp.hpl.jena.query.QuerySolution;
import org.aksw.rdfunit.tests.QueryGenerationFactory;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.ExtendedTestCaseResult;
import org.aksw.rdfunit.tests.results.RLOGTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;

/**
 * The Extended Test Executor extends RLOG Executor but provides richer error metadata
 * TODO: At the moment this is partially
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 6:13 PM
 */
public class ExtendedTestExecutor extends RLOGTestExecutor {

    /**
     * Instantiates a new ExtendedTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public ExtendedTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    @Override
    protected TestCaseResult generateSingleResult(QuerySolution qs, TestCase testCase) {
        RLOGTestCaseResult rlogResult = (RLOGTestCaseResult) super.generateSingleResult(qs, testCase);
        return new ExtendedTestCaseResult(testCase, rlogResult);
    }
}
