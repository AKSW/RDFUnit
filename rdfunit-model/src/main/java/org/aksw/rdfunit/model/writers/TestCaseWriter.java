package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.impl.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

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
     * <p>create.</p>
     *
     * @param testCase a {@link org.aksw.rdfunit.model.interfaces.TestCase} object.
     * @return a {@link org.aksw.rdfunit.model.writers.TestCaseWriter} object.
     */
    public static TestCaseWriter create(TestCase testCase) {return new TestCaseWriter(testCase);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {

        if (testCase instanceof ManualTestCaseImpl) {
            return ManualTestCaseWriter.create((ManualTestCaseImpl) testCase).write(model);
        }

        if (testCase instanceof PatternBasedTestCaseImpl) {
            return PatternBasedTestCaseWriter.create((PatternBasedTestCaseImpl) testCase).write(model);
        }

        throw new UnsupportedOperationException("Writer of test case not implemented: " + testCase.toString());
    }
}
