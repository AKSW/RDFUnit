package org.aksw.rdfunit.junit;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.TestSource;
import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.InvocationTargetException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author Michael Leuthold
 * @version $Id: $Id
 */
final class RdfUnitJunitTestCase {

    private final TestCase testCase;
    private final RDFReader combinedReader;
    private final FrameworkMethod testInputMethod;
    private final TestSource modelSource;
    private final Model testInputModel;

    RdfUnitJunitTestCase(TestCase testCase, RDFReader combinedReader, FrameworkMethod testInputMethod, TestSource modelSource, Model testInputModel) {
        this.combinedReader = combinedReader;
        this.testInputMethod = testInputMethod;
        this.modelSource = modelSource;
        this.testInputModel = testInputModel;
        this.testCase = checkNotNull(testCase);
    }

    TestCase getTestCase() {
        return testCase;
    }

    RDFReader getInputReader() throws IllegalAccessException, InvocationTargetException {
        return combinedReader;
    }

    /**
     * <p>Getter for the field <code>testInputMethod</code>.</p>
     *
     * @return a {@link org.junit.runners.model.FrameworkMethod} object.
     */
    public FrameworkMethod getTestInputMethod() {
        return testInputMethod;
    }

    /**
     * <p>Getter for the field <code>modelSource</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.TestSource} object.
     */
    public TestSource getModelSource() {
        return modelSource;
    }

    /**
     * <p>Getter for the field <code>testInputModel</code>.</p>
     *
     * @return a {@link com.hp.hpl.jena.rdf.model.Model} object.
     */
    public Model getTestInputModel() {
        return testInputModel;
    }
}
