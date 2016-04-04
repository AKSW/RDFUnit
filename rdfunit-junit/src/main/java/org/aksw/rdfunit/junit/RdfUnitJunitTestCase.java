package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.TestSource;
import org.apache.jena.rdf.model.Model;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Michael Leuthold
 * @version $Id: $Id
 */
final class RdfUnitJunitTestCase {

    private final TestCase testCase;
    private final RdfUnitJunitTestCaseDataProvider rdfUnitJunitTestCaseDataProvider;

    RdfUnitJunitTestCase(TestCase testCase, RdfUnitJunitTestCaseDataProvider rdfUnitJunitTestCaseDataProvider) {
        this.rdfUnitJunitTestCaseDataProvider = rdfUnitJunitTestCaseDataProvider;
        this.testCase = checkNotNull(testCase);
    }

    public TestCase getTestCase() {
        return testCase;
    }

    /**
     * <p>Getter for the field <code>testInputMethod</code>.</p>
     *
     * @return a {@link org.junit.runners.model.FrameworkMethod} object.
     */
    public FrameworkMethod getTestInputMethod() {
        return this.rdfUnitJunitTestCaseDataProvider.getTestInputMethod();
    }

    /**
     * <p>Getter for the field <code>modelSource</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.TestSource} object.
     */
    public TestSource getModelSource() {
        return this.rdfUnitJunitTestCaseDataProvider.getModelSource();
    }

    /**
     * <p>Getter for the field <code>testInputModel</code>.</p>
     *
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     */
    public Model getTestInputModel() {
        return this.rdfUnitJunitTestCaseDataProvider.getTestInputModel();
    }

    /**
     * <p>prepareForExecution.</p>
     */
    public void prepareForExecution() {
        try {
            this.rdfUnitJunitTestCaseDataProvider.initialize();
        } catch (InitializationError initializationError) {
            throw new IllegalArgumentException(initializationError);
        }

    }
}
