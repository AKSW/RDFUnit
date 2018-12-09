package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.impl.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM

 */
public final class TestCaseWriter implements ElementWriter {

    private final GenericTestCase testCase;

    private TestCaseWriter(GenericTestCase testCase) {
        this.testCase = testCase;
    }

    public static TestCaseWriter create(GenericTestCase testCase) {return new TestCaseWriter(testCase);}


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
