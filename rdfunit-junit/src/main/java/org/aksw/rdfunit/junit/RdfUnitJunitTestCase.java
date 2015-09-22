package org.aksw.rdfunit.junit;

import java.lang.reflect.InvocationTargetException;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.elements.interfaces.TestCase;
import org.aksw.rdfunit.sources.SchemaSource;

import static com.google.common.base.Preconditions.checkNotNull;

final class RdfUnitJunitTestCase {

    private final TestCase testCase;
    private final SchemaSource schemaSource;
    private final Model inputModel;

    public RdfUnitJunitTestCase(TestCase testCase, SchemaSource schemaSource, Model inputModel) {
        this.schemaSource = schemaSource;
        this.inputModel = inputModel;
        this.testCase = checkNotNull(testCase);
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public Model getInputModel() throws IllegalAccessException, InvocationTargetException {
        return inputModel;
    }

    public SchemaSource getSchemaSource() {
        return schemaSource;
    }

}
