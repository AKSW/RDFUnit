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
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.StatusTestExecutor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationAskFactory;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

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
                testCases.add(new RdfUnitJunitTestCase(m, t, getOntology().uri()));
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
            ontologyReader = new RDFModelReader(ModelFactory.createDefaultModel().read(
                    in,
                    getOntology().format())
            );
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
    protected void runChild(final RdfUnitJunitRunner.RdfUnitJunitTestCase child, RunNotifier notifier) {
        try {
            final Object testInstance = getTestClass().getJavaClass().newInstance();
            final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor = new RdfUnitJunitStatusTestExecutor();
            final Statement statement = new Statement() {

                @Override
                public void evaluate() throws Throwable {
                    assertThat(
                            child.getTestCase().getResultMessage(),
                            child.runTest(testInstance, rdfUnitJunitStatusTestExecutor)
                    );
                }
            };

            this.runLeaf(statement, describeChild(child), notifier);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class RdfUnitJunitStatusTestExecutor extends StatusTestExecutor {

        public RdfUnitJunitStatusTestExecutor() {
            super(new QueryGenerationAskFactory());
        }

        public boolean runTest(TestCase testCase, Model inputModel, String uri) {

            RDFReader ontologyReader = new RDFModelReader(ModelFactory.createDefaultModel().read(uri));
            final SchemaSource schemaSource =
                    SchemaSourceFactory.createSchemaSourceSimple("custom", uri, ontologyReader);

            final TestSource modelSource = new TestSourceBuilder()
                    .setPrefixUri("custom", "rdfunit")
                    .setInMemReader(new RDFModelReader(inputModel))
                    .setReferenceSchemata(schemaSource)
                    .build();

            final boolean success = this.execute(
                    modelSource,
                    new TestSuite(Collections.singleton(testCase))
            );
            return success;
        }

    }

    public static final class RdfUnitJunitTestCase {
        private final FrameworkMethod inputModelProvider;
        private final TestCase testCase;
        private final String uri;
        private Description description;

        public RdfUnitJunitTestCase(FrameworkMethod inputModelProvider, TestCase testCase, String uri) {
            this.uri = uri;
            this.inputModelProvider = checkNotNull(inputModelProvider);
            this.testCase = checkNotNull(testCase);
        }

        public FrameworkMethod getInputModelProvider() {
            return inputModelProvider;
        }

        public TestCase getTestCase() {
            return testCase;
        }

        public String getUri() {
            return uri;
        }

        public Model getInputModel(Object instance) throws IllegalAccessException, InvocationTargetException {
            return (Model) getInputModelProvider().getMethod().invoke(instance);
        }

        private boolean runTest(Object testInstance, RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor)
                throws IllegalAccessException, InvocationTargetException {
            return rdfUnitJunitStatusTestExecutor.runTest(
                    getTestCase(),
                    getInputModel(testInstance),
                    getUri()
            );
        }

    }

}
