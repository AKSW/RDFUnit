package org.aksw.rdfunit.model.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.impl.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
public final class TestCaseWriter implements ElementWriter {

    private final TestCase testCase;

    private TestCaseWriter(TestCase testCase) {
        this.testCase = testCase;
    }

    /**
     * <p>createTestCaseWriter.</p>
     *
     * @param testCase a {@link org.aksw.rdfunit.model.interfaces.TestCase} object.
     * @return a {@link org.aksw.rdfunit.model.writers.TestCaseWriter} object.
     */
    public static TestCaseWriter createTestCaseWriter(TestCase testCase) {return new TestCaseWriter(testCase);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {

        if (testCase instanceof ManualTestCaseImpl) {
            return ManualTestCaseWriter.createManualTestCaseWriter( (ManualTestCaseImpl) testCase).write(model);
        }

        if (testCase instanceof PatternBasedTestCaseImpl) {
            return PatternBasedTestCaseWriter.createPatternBasedTestCaseWriter((PatternBasedTestCaseImpl) testCase).write(model);
        }

        throw new RuntimeException("Writer of test case not implemented: " + testCase.toString());
    }
}
