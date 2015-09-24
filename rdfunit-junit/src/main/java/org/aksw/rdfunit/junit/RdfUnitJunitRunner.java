package org.aksw.rdfunit.junit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFMultipleReader;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.model.interfaces.TestCase;
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

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Arrays.asList;

import static org.aksw.rdfunit.junit.InitializationSupport.checkNotNull;

/**
 * <p>RdfUnitJunitRunner class.</p>
 *
 * @author Michael Leuthold
 * @version $Id: $Id
 */
public class RdfUnitJunitRunner extends ParentRunner<RdfUnitJunitTestCase> {

    /** Constant <code>INPUT_DATA_RETURN_TYPE</code> */
    public static final Class<?> INPUT_DATA_RETURN_TYPE = RDFReader.class;
    private final List<RdfUnitJunitTestCase> testCases = new ArrayList<>();
    private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor = new RdfUnitJunitStatusTestExecutor();
    private RDFReader additionalData = new RDFModelReader(ModelFactory.createDefaultModel());
    private RDFReader schemaReader;
    private Object testCaseInstance;

    /**
     * <p>Constructor for RdfUnitJunitRunner.</p>
     *
     * @param testClass a {@link java.lang.Class} object.
     * @throws org.junit.runners.model.InitializationError if any.
     */
    public RdfUnitJunitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        setUpSchemaReader();
        setAdditionalData();
        generateRdfUnitTestCases();
    }

    /** {@inheritDoc} */
    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.collectInitializationErrors(errors);

        verifySchemaAnnotation(errors);
        verifyTestInputAnnotatedMethods(errors);
        verifyAtMostOneAdditionalDataMethodWithMatchingReturnType(errors);
    }

    private void verifySchemaAnnotation(List<Throwable> errors) {
        if (!getTestClass().getJavaClass().isAnnotationPresent(Schema.class)) {
            errors.add(new Exception(
                    String.format(
                            "@%s annotation is required!",
                            Schema.class.getSimpleName())));
        }
    }

    private void verifyTestInputAnnotatedMethods(List<Throwable> errors) {
        List<FrameworkMethod> testInputMethods = getTestInputMethods();

        if (testInputMethods.isEmpty()) {
            errors.add(new Exception(String.format(
                    "At least one method with @%s annotation is required!",
                    TestInput.class.getSimpleName()
            )));
        }

        for (FrameworkMethod m : testInputMethods) {
            if (!m.getReturnType().equals(INPUT_DATA_RETURN_TYPE)) {
                errors.add(new Exception(String.format(
                        "Method %s marked @%s must return %s",
                        m.getName(),
                        TestInput.class.getSimpleName(),
                        INPUT_DATA_RETURN_TYPE.getCanonicalName()
                )));
            }
        }
    }

    private void verifyAtMostOneAdditionalDataMethodWithMatchingReturnType(List<Throwable> errors) {
        final List<FrameworkMethod> additionalDataAnnotatedMethods = getTestClass().getAnnotatedMethods
                (AdditionalData.class);
        if (additionalDataAnnotatedMethods.isEmpty()) {
            return;
        }
        if (additionalDataAnnotatedMethods.size() > 1) {
            errors.add(new Exception(
                            String.format(
                                    "At most one method annotated with @%s allowed!",
                                    AdditionalData.class.getSimpleName()
                            )
                    )
            );
        }

        for (FrameworkMethod additionalDataMethod : additionalDataAnnotatedMethods) {
            if (!additionalDataMethod.getReturnType().equals(INPUT_DATA_RETURN_TYPE)) {
                errors.add(
                        new Exception(
                                String.format(
                                        "Method %s annotated with @%s must return a %s!",
                                        additionalDataMethod.getName(),
                                        AdditionalData.class.getSimpleName(),
                                        INPUT_DATA_RETURN_TYPE.getCanonicalName()
                                )
                        )
                );
            }
        }
    }

    private List<FrameworkMethod> getTestInputMethods() {
        return getTestClass().getAnnotatedMethods(TestInput.class);
    }

    private void generateRdfUnitTestCases() throws InitializationError {
        final Map<FrameworkMethod, RDFReader> testInputReaders = new LinkedHashMap<>();
        for (FrameworkMethod m : getTestInputMethods()) {
            try {
                final RDFReader testInputReader = checkNotNull(
                        (RDFReader) m.getMethod().invoke(getTestCaseInstance()),
                        "@%s: %s returned null!",
                        TestInput.class.getSimpleName(),
                        m.getMethod().getName()
                );
                testInputReaders.put(m, testInputReader);
            } catch (IllegalAccessException | InvocationTargetException e1) {
                throw new InitializationError(e1);
            }
        }
        final SchemaSource schemaSource = createSchemaSourceFromSchema();
        final Collection<TestCase> testCases = createTestCases();
        final Map<FrameworkMethod, RDFReader> combinedReaders = new LinkedHashMap<>();
        for (Map.Entry<FrameworkMethod, RDFReader> e1 : testInputReaders.entrySet()) {
            combinedReaders.put(e1.getKey(), new RDFMultipleReader(asList(e1.getValue(), additionalData)));
        }
        for (Map.Entry<FrameworkMethod, RDFReader> e : combinedReaders.entrySet()) {
            final TestSource modelSource = new TestSourceBuilder()
                    // FIXME why do we need at least one source config? If we omit this, it will break...
                    .setPrefixUri("custom", "rdfunit")
                    .setInMemReader(e.getValue())
                    .setReferenceSchemata(schemaSource)
                    .build();
            Model result;
            try {
                result = testInputReaders.get(e.getKey()).read();
            } catch (RDFReaderException e1) {
                throw new InitializationError(e1);
            }
            final Model testInputModel = result;
            for (TestCase t : testCases) {
                this.testCases.add(new RdfUnitJunitTestCase(t, e.getValue(), e.getKey(), modelSource, testInputModel));
            }
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
        try {
            final List<FrameworkMethod> annotatedMethods = getTestClass().getAnnotatedMethods(AdditionalData.class);
            if (annotatedMethods.isEmpty()) {
                return;
            }
            final FrameworkMethod additionalDataMethod = annotatedMethods.get(0);
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

    /** {@inheritDoc} */
    @Override
    protected List<RdfUnitJunitTestCase> getChildren() {
        return Collections.unmodifiableList(testCases);
    }

    /** {@inheritDoc} */
    @Override
    protected Description describeChild(RdfUnitJunitTestCase child) {
        return Description.createTestDescription(
                child.getTestInputMethod().getDeclaringClass(),
                String.format(
                        "[%s] %s",
                        child.getTestInputMethod().getMethod().getName(),
                        child.getTestCase().getAbrTestURI()
                )
        );
    }

    /** {@inheritDoc} */
    @Override
    protected void runChild(final RdfUnitJunitTestCase child, RunNotifier notifier) {
        this.runLeaf(new RLOGStatement(rdfUnitJunitStatusTestExecutor, child), describeChild(child), notifier);
    }

}


