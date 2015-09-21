package org.aksw.rdfunit.junit;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.elements.interfaces.TestCase;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import static com.google.common.base.Preconditions.checkNotNull;

public class RdfUnitJunitRunner extends ParentRunner<RdfUnitJunitRunner.RdfUnitJunitTestCase> {

    public static final class RdfUnitJunitTestCase {
        private final FrameworkMethod frameworkMethod;
        private final TestCase testCase;

        public RdfUnitJunitTestCase(FrameworkMethod frameworkMethod, TestCase testCase) {
            this.frameworkMethod = checkNotNull(frameworkMethod);
            this.testCase = checkNotNull(testCase);
        }

        public FrameworkMethod getFrameworkMethod() {
            return frameworkMethod;
        }

        public TestCase getTestCase() {
            return testCase;
        }
    }

    private final List<RdfUnitJunitRunner.RdfUnitJunitTestCase> testCases = new ArrayList<>();

    public RdfUnitJunitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        if (!testClass.isAnnotationPresent(Ontology.class)) {
            throw new InitializationError("@Ontology annotation is required!");
        }

        final Ontology ontology = testClass.getAnnotation(Ontology.class);
        final RDFReader ontologyReader;
        try {
            final URL url = URI.create(ontology.uri()).toURL();
            ontologyReader = new RDFModelReader(ModelFactory.createDefaultModel().read(url.openStream(), ontology
                    .format()));
        } catch (IOException e) {
            throw new InitializationError(e);
        }
        final Collection<TestCase> testCaseCollection = new RDFUnitTestSuiteGenerator.Builder()
                .addSchemaURI("custom", ontology.uri(), ontologyReader)
                .enableAutotests()
                .build().getTestSuite().getTestCases();
        final List<FrameworkMethod> annotatedMethods = getTestClass().getAnnotatedMethods(InputModel.class);
        for (TestCase t : testCaseCollection) {
            for (FrameworkMethod m : annotatedMethods) {
                testCases.add(new RdfUnitJunitTestCase(m, t));
            }
        }
    }

    @Override
    protected List<RdfUnitJunitRunner.RdfUnitJunitTestCase> getChildren() {
        return Collections.unmodifiableList(testCases);
    }

    @Override
    protected Description describeChild(RdfUnitJunitRunner.RdfUnitJunitTestCase child) {
        return Description.createTestDescription(this.getTestClass().getJavaClass(), "RdfUnitJunitRunner");
    }

    @Override
    protected void runChild(RdfUnitJunitRunner.RdfUnitJunitTestCase child, RunNotifier notifier) {

    }

}
