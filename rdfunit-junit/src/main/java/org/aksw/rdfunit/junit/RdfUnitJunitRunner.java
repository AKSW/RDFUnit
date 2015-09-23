package org.aksw.rdfunit.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.elements.interfaces.TestCase;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFMultipleReader;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import static java.util.Arrays.asList;

import static org.aksw.rdfunit.junit.InitializationSupport.checkNotNull;
import static org.aksw.rdfunit.junit.InitializationSupport.checkState;

public class RdfUnitJunitRunner extends ParentRunner<RdfUnitJunitTestCase> {

    public static final Class<?> INPUT_DATA_RETURN_TYPE = RDFReader.class;
    private final List<RdfUnitJunitTestCase> testCases = new ArrayList<>();
    private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor = new RdfUnitJunitStatusTestExecutor();
    private RDFReader additionalData = new RDFModelReader(ModelFactory.createDefaultModel());
    private RDFReader schemaReader;
    private Object testCaseInstance;
    private Map<FrameworkMethod, RDFReader> testInputReaders;

    public RdfUnitJunitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        checkSchemaAnnotation();
        checkTestInputAnnotatedMethods();

        setUpTestInputReaders();

        setUpSchemaReader();

        setAdditionalData();

        generateRdfUnitTestCases();
    }

    private void checkSchemaAnnotation() throws InitializationError {
        checkState(
                getTestClass().getJavaClass().isAnnotationPresent(Schema.class),
                "@%s annotation is required!",
                Schema.class.getSimpleName()
        );
    }

    private void checkTestInputAnnotatedMethods() throws InitializationError {
        for (FrameworkMethod m : getTestInputMethods()) {
            checkState(
                    m.getReturnType().equals(INPUT_DATA_RETURN_TYPE),
                    "Methods marked @%s must return %s",
                    TestInput.class.getSimpleName(),
                    INPUT_DATA_RETURN_TYPE.getCanonicalName()
            );
        }
    }

    private List<FrameworkMethod> getTestInputMethods() throws InitializationError {
        final List<FrameworkMethod> testInputMethods = getTestClass().getAnnotatedMethods(TestInput.class);
        checkState(
                !testInputMethods.isEmpty(),
                "At least one method with @%s annotation is required!",
                TestInput.class.getSimpleName()
        );
        return testInputMethods;
    }

    private void generateRdfUnitTestCases() throws InitializationError {
        final SchemaSource schemaSource = createSchemaSourceFromSchema();
        final Collection<TestCase> testCases = createTestCases();
        for (Map.Entry<FrameworkMethod, RDFReader> e : getCombinedReaders().entrySet()) {
            final TestSource modelSource = new TestSourceBuilder()
                    // FIXME why do we need at least one source config? If we omit this, it will break...
                    .setPrefixUri("custom", "rdfunit")
                    .setInMemReader(e.getValue())
                    .setReferenceSchemata(schemaSource)
                    .build();
            final Model testInputModel = getTestInputModel(e.getKey());
            for (TestCase t : testCases) {
                this.testCases.add(new RdfUnitJunitTestCase(t, e.getValue(), e.getKey(), modelSource, testInputModel));
            }
        }
    }

    private Model getTestInputModel(FrameworkMethod method) throws InitializationError {
        try {
            return testInputReaders.get(method).read();
        } catch (RDFReaderException e1) {
            throw new InitializationError(e1);
        }
    }

    private void setUpSchemaReader() {
        schemaReader = new RDFModelReader(ModelFactory.createDefaultModel().read(getSchema().uri()));
    }

    private synchronized Object getTestCaseInstance() throws InitializationError {
        if (testCaseInstance == null) {
            try {
                testCaseInstance = getTestClass().getJavaClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new InitializationError(e);
            }
        }
        return testCaseInstance;
    }

    private void setAdditionalData() throws InitializationError {
        final List<FrameworkMethod> additionalDataAnnotatedMethods =
                getTestClass().getAnnotatedMethods(AdditionalData.class);
        if (additionalDataAnnotatedMethods.isEmpty()) {
            return;
        }
        checkState(
                additionalDataAnnotatedMethods.size() <= 1,
                "At most one method annotated with @%s allowed!",
                AdditionalData.class.getSimpleName()
        );
        try {
            final FrameworkMethod additionalDataMethod = additionalDataAnnotatedMethods.get(0);
            checkState(
                    additionalDataMethod.getReturnType().equals(INPUT_DATA_RETURN_TYPE),
                    "Method annotated with @%s must return a %s!",
                    AdditionalData.class.getSimpleName(),
                    INPUT_DATA_RETURN_TYPE.getCanonicalName()
            );
            additionalData =
                    checkNotNull(
                            (RDFReader) additionalDataMethod.invokeExplosively(getTestCaseInstance()),
                            "Method %s annotated with @%s returned null!",
                            additionalDataMethod.getMethod().getName(),
                            AdditionalData.class.getSimpleName()
                    );
        } catch (Throwable e) {
            throw new InitializationError(e);
        }
    }

    private Map<FrameworkMethod, RDFReader> getCombinedReaders() throws InitializationError {
        final Map<FrameworkMethod, RDFReader> multiReaders = new LinkedHashMap<>();
        for (Map.Entry<FrameworkMethod, RDFReader> e : testInputReaders.entrySet()) {
            multiReaders.put(e.getKey(), new RDFMultipleReader(asList(e.getValue(), additionalData)));
        }
        return multiReaders;
    }

    private void setUpTestInputReaders() throws InitializationError {
        testInputReaders = new LinkedHashMap<>();
        for (FrameworkMethod m : getTestInputMethods()) {
            try {
                final RDFReader testInputReader = checkNotNull(
                        (RDFReader) m.getMethod().invoke(getTestCaseInstance()),
                        "@%s: %s returned null!",
                        TestInput.class.getSimpleName(),
                        m.getMethod().getName()
                );
                testInputReaders.put(m, testInputReader);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InitializationError(e);
            }
        }
    }

    private SchemaSource createSchemaSourceFromSchema() {
        return SchemaSourceFactory.createSchemaSourceSimple("custom", getSchema().uri(), getSchemaReader());
    }

    private Collection<TestCase> createTestCases() throws InitializationError {
        return new RDFUnitTestSuiteGenerator.Builder()
                .addSchemaURI("custom", getSchema().uri(), getSchemaReader())
                .enableAutotests()
                .build().getTestSuite().getTestCases();
    }

    private RDFReader getSchemaReader() {
        return schemaReader;
    }

    private Schema getSchema() {
        return getTestClass().getAnnotation(Schema.class);
    }

    RDFReader getAdditionalDataModel() {
        return additionalData;
    }

    @Override
    protected List<RdfUnitJunitTestCase> getChildren() {
        return Collections.unmodifiableList(testCases);
    }

    @Override
    protected Description describeChild(RdfUnitJunitTestCase child) {
        return Description.createTestDescription(
                child.getFrameworkMethod().getDeclaringClass(),
                String.format(
                        "[%s] %s",
                        child.getFrameworkMethod().getMethod().getName(),
                        child.getTestCase().getAbrTestURI()
                )
        );
    }

    @Override
    protected void runChild(final RdfUnitJunitTestCase child, RunNotifier notifier) {
        this.runLeaf(new RLOGStatement(rdfUnitJunitStatusTestExecutor, child), describeChild(child), notifier);
    }

}


