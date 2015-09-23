package org.aksw.rdfunit.junit;

import java.lang.reflect.InvocationTargetException;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.elements.interfaces.TestCase;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.sources.TestSource;
import org.junit.runners.model.FrameworkMethod;

import static com.google.common.base.Preconditions.checkNotNull;

final class RdfUnitJunitTestCase {

    private final TestCase testCase;
    private final RDFReader combinedReader;
    private final FrameworkMethod frameworkMethod;
    private final TestSource modelSource;
    private final Model testInputModel;

    RdfUnitJunitTestCase(TestCase testCase, RDFReader combinedReader, FrameworkMethod frameworkMethod, TestSource modelSource, Model testInputModel) {
        this.combinedReader = combinedReader;
        this.frameworkMethod = frameworkMethod;
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

    public FrameworkMethod getFrameworkMethod() {
        return frameworkMethod;
    }

    public TestSource getModelSource() {
        return modelSource;
    }

    public Model getTestInputModel() {
        return testInputModel;
    }
}
