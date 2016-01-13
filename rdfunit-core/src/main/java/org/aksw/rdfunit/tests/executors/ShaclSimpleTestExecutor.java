package org.aksw.rdfunit.tests.executors;


import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.impl.results.SimpleShaclTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;

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
public class ShaclSimpleTestExecutor extends RLOGTestExecutor {

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
        Collection<TestCaseResult> results = super.executeSingleTest(testSource, testCase);
        Collection<TestCaseResult> resultsCopy = new ArrayList<>();
        for (TestCaseResult res: results) {
            if (res instanceof RLOGTestCaseResult ) {
                resultsCopy.add(
                        new SimpleShaclTestCaseResultImpl(
                                res.getTestCaseUri(),
                                res.getSeverity(),
                                res.getMessage(),
                                ((RLOGTestCaseResult) res).getFailingResource()));
            }
            else {
                resultsCopy.add(res);
            }
        }
        return resultsCopy;
    }

}
