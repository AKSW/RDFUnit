package org.aksw.rdfunit.junit;

import java.util.ArrayList;
import java.util.Collection;

import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.rdfunit.tests.results.RLOGTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.junit.runners.model.Statement;

import static org.hamcrest.MatcherAssert.assertThat;

class RLOGStatement extends Statement {

    private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor;
    private final RdfUnitJunitTestCase testCase;

    RLOGStatement(RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor, RdfUnitJunitTestCase testCase) {
        this.rdfUnitJunitStatusTestExecutor = rdfUnitJunitStatusTestExecutor;
        this.testCase = testCase;
    }

    @Override
    public void evaluate() throws Throwable {
        final Collection<TestCaseResult> testCaseResults = rdfUnitJunitStatusTestExecutor.runTest(testCase);
        final Collection<RLOGTestCaseResult> remainingResults = new ArrayList<>();
        for (TestCaseResult t : testCaseResults) {
            RLOGTestCaseResult r = (RLOGTestCaseResult) t;
            boolean resourceIsPartOfInputModel = testCase.getInputModel().contains(
                    ResourceFactory.createResource(r.getResource()), null);
            if (!resourceIsPartOfInputModel) {
                continue;
            }
            remainingResults.add(r);
        }
        final StringBuilder b = new StringBuilder();
        b.append(testCase.getTestCase().getResultMessage()).append(":\n");
        for (RLOGTestCaseResult r : remainingResults) {
            b.append("\t").append(r.getResource()).append("\n");
        }
        assertThat(b.toString(), remainingResults.isEmpty());
    }

}
