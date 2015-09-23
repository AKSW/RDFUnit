package org.aksw.rdfunit.junit;

import java.lang.reflect.InvocationTargetException;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.elements.interfaces.TestCase;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.junit.runners.model.FrameworkMethod;

import static com.google.common.base.Preconditions.checkNotNull;

final class RdfUnitJunitTestCase {

    private final TestCase testCase;
    private final SchemaSource schemaSource;
    private final Model inputModel;
    private final FrameworkMethod frameworkMethod;
    private final TestSource modelSource;

    RdfUnitJunitTestCase(TestCase testCase, SchemaSource schemaSource, Model inputModel, FrameworkMethod frameworkMethod, TestSource modelSource) {
        this.schemaSource = schemaSource;
        this.inputModel = inputModel;
        this.frameworkMethod = frameworkMethod;
        this.modelSource = modelSource;
        this.testCase = checkNotNull(testCase);
    }

    TestCase getTestCase() {
        return testCase;
    }

    Model getInputModel() throws IllegalAccessException, InvocationTargetException {
        return inputModel;
    }

    SchemaSource getSchemaSource() {
        return schemaSource;
    }

    public FrameworkMethod getFrameworkMethod() {
        return frameworkMethod;
    }

    public TestSource getModelSource() {
        return modelSource;
    }
}
