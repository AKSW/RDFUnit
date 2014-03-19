package org.aksw.rdfunit.tests.executors;

import com.hp.hpl.jena.query.QuerySolution;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.ExtendedTestCaseResult;
import org.aksw.rdfunit.tests.results.RLOGTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;

/**
 * User: Dimitris Kontokostas
 * Executes results for ExtendedTestCaseResult sets
 * Created: 2/2/14 6:13 PM
 */
public class ExtendedTestExecutor extends RLOGTestExecutor {

    @Override
    protected TestCaseResult generateSingleResult(QuerySolution qs, TestCase testCase) {
        RLOGTestCaseResult rlogResult = (RLOGTestCaseResult) super.generateSingleResult(qs, testCase);
        return new ExtendedTestCaseResult(testCase, rlogResult);
    }
}
