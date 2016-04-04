package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.model.interfaces.results.SimpleShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Michael Leuthold
 * @version $Id: $Id
 */
class ShaclResultStatement extends Statement {

    private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor;
    private final RdfUnitJunitTestCase testCase;

    ShaclResultStatement(RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor, RdfUnitJunitTestCase testCase) {
        super();
        this.rdfUnitJunitStatusTestExecutor = rdfUnitJunitStatusTestExecutor;
        this.testCase = testCase;
    }

    /** {@inheritDoc} */
    @Override
    public void evaluate() throws Throwable {
        final Collection<TestCaseResult> testCaseResults = rdfUnitJunitStatusTestExecutor.runTest(testCase);
        final Collection<SimpleShaclTestCaseResult> remainingResults = new ArrayList<>();
        for (TestCaseResult t : testCaseResults) {
            SimpleShaclTestCaseResult r = (SimpleShaclTestCaseResult) t;
            if (!resourceIsPartOfInputModel(r)) {
                continue;
            }
            remainingResults.add(r);
        }
        final StringBuilder b = new StringBuilder();
        b.append(testCase.getTestCase().getResultMessage()).append(":\n");
        for (SimpleShaclTestCaseResult r : remainingResults) {
            b.append('\t').append(r.getFailingResource()).append('\n');
        }
        assertThat(b.toString(), remainingResults.isEmpty());
    }

    private boolean resourceIsPartOfInputModel(SimpleShaclTestCaseResult r) {
        return testCase.getTestInputModel().contains(
                ResourceFactory.createResource(r.getFailingResource()), null);
    }

}
