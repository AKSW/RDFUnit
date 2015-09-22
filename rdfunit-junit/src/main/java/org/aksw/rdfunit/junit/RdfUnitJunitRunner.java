package org.aksw.rdfunit.junit;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.elements.interfaces.TestCase;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.StatusTestExecutor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

public class RdfUnitJunitRunner extends ParentRunner<RdfUnitJunitRunner.RdfUnitJunitTestCase> {

    private final List<RdfUnitJunitRunner.RdfUnitJunitTestCase> testCases = new ArrayList<>();

    public RdfUnitJunitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        checkOntologyAnnotation();
        checkInputModelAnnotatedMethods();

        generateRdfUnitTestCases();
    }

    private void checkOntologyAnnotation() throws InitializationError {
        if (getTestClass().getJavaClass().isAnnotationPresent(Ontology.class)) {
            return;
        }
        throw new InitializationError("@Ontology annotation is required!");
    }

    private void checkInputModelAnnotatedMethods() throws InitializationError {
        List<FrameworkMethod> inputModelMethods = getTestClass().getAnnotatedMethods(InputModel.class);
        if (inputModelMethods.isEmpty()) {
            throw new InitializationError("At least one method with @InputModel annotation is required!");
        }

        for (FrameworkMethod m : inputModelMethods) {
            if (!m.getReturnType().equals(Model.class)) {
                throw new InitializationError("Methods marked @InputModel must return com.hp.hpl.jena.rdf.model.Model");
            }
        }
    }

    private void generateRdfUnitTestCases() throws InitializationError {
        final List<FrameworkMethod> annotatedMethods = getTestClass().getAnnotatedMethods(InputModel.class);
        for (TestCase t : createTestCases()) {
            for (FrameworkMethod m : annotatedMethods) {
                testCases.add(new RdfUnitJunitTestCase(m, t));
            }
        }
    }

    private Collection<TestCase> createTestCases() throws InitializationError {
        final RDFReader ontologyReader = getRdfReaderForOntology();
        return new RDFUnitTestSuiteGenerator.Builder()
                .addSchemaURI("custom", getOntology().uri(), ontologyReader)
                .enableAutotests()
                .build().getTestSuite().getTestCases();
    }

    private RDFReader getRdfReaderForOntology() throws InitializationError {
        final RDFReader ontologyReader;

        try (final InputStream in = getOntologyUrl().openStream()) {
            ontologyReader = new RDFModelReader(ModelFactory.createDefaultModel().read(in, getOntology
                    ()
                    .format()));
        } catch (IOException e) {
            throw new InitializationError(e);
        }
        return ontologyReader;
    }

    private URL getOntologyUrl() throws InitializationError {
        final URL url;
        try {
            url = URI.create(getOntology().uri()).toURL();
        } catch (MalformedURLException e) {
            throw new InitializationError(e);
        }
        return url;
    }

    private Ontology getOntology() {
        return getTestClass().getAnnotation(Ontology.class);
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
        try {
            final Object testInstance = getTestClass().getJavaClass().newInstance();
            new RdfUnitJunitStatusTestExecutor().runTest(
                    child.getTestCase(),
                    child.getInputModel(testInstance)
            );
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class RdfUnitJunitStatusTestExecutor extends StatusTestExecutor {

        public RdfUnitJunitStatusTestExecutor() {
            super(new QueryGenerationSelectFactory());
        }

        public void runTest(TestCase testCase, Model inputModel) {
            final TestSource modelSource = new TestSourceBuilder()
                    .setPrefixUri("custom", "rdfunit")
                    .setInMemReader(new RDFModelReader(inputModel))
                    .build();

            final boolean success = this.execute(
                    modelSource,
                    new TestSuite(Collections.singleton(testCase))
            );
            assertTrue(success);
        }

    }

    public static final class RdfUnitJunitTestCase {
        private final FrameworkMethod inputModelProvider;
        private final TestCase testCase;

        public RdfUnitJunitTestCase(FrameworkMethod inputModelProvider, TestCase testCase) {
            this.inputModelProvider = checkNotNull(inputModelProvider);
            this.testCase = checkNotNull(testCase);
        }

        public FrameworkMethod getInputModelProvider() {
            return inputModelProvider;
        }

        public TestCase getTestCase() {
            return testCase;
        }

        public Model getInputModel(Object instance) throws IllegalAccessException, InvocationTargetException {
            return (Model) getInputModelProvider().getMethod().invoke(instance);
        }
    }

}
