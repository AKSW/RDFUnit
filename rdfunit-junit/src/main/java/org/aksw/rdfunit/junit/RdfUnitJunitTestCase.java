package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.TestSource;
import org.apache.jena.rdf.model.Model;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Michael Leuthold

 */
final class RdfUnitJunitTestCase {

    private final GenericTestCase testCase;
    private final RdfUnitJunitTestCaseDataProvider rdfUnitJunitTestCaseDataProvider;

    RdfUnitJunitTestCase(GenericTestCase testCase, RdfUnitJunitTestCaseDataProvider rdfUnitJunitTestCaseDataProvider) {
        this.rdfUnitJunitTestCaseDataProvider = rdfUnitJunitTestCaseDataProvider;
        this.testCase = checkNotNull(testCase);
    }

    public GenericTestCase getTestCase() {
        return testCase;
    }

    public FrameworkMethod getTestInputMethod() {
        return this.rdfUnitJunitTestCaseDataProvider.getTestInputMethod();
    }

    public TestSource getModelSource() {
        return this.rdfUnitJunitTestCaseDataProvider.getModelSource();
    }

    public Model getTestInputModel() {
        return this.rdfUnitJunitTestCaseDataProvider.getTestInputModel();
    }

    public void prepareForExecution() {
        try {
            this.rdfUnitJunitTestCaseDataProvider.initialize();
        } catch (InitializationError initializationError) {
            throw new IllegalArgumentException(initializationError);
        }

    }
}
