package org.aksw.databugger.tests.executors;

import com.hp.hpl.jena.query.QuerySolution;
import org.aksw.databugger.tests.TestCase;
import org.aksw.databugger.tests.results.ExtendedTestCaseResult;
import org.aksw.databugger.tests.results.RLOGTestCaseResult;
import org.aksw.databugger.tests.results.TestCaseResult;

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
