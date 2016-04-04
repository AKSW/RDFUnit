package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>RdfUnitJunitRunner class.</p>
 *
 * @author Michael Leuthold
 * @version $Id: $Id
 */
public class RdfUnitJunitRunner extends ParentRunner<RdfUnitJunitTestCase> {

    /**
     * Constant <code>INPUT_DATA_RETURN_TYPE</code>
     */
    public static final Class<?> INPUT_DATA_RETURN_TYPE = RdfReader.class;
    private final List<RdfUnitJunitTestCase> testCases = new ArrayList<>();
    private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor = new RdfUnitJunitStatusTestExecutor();
    private RdfReader additionalData = new RdfModelReader(ModelFactory.createDefaultModel());
    private RdfReader schemaReader;
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

        errors.addAll(testInputMethods.stream()
                .filter(m -> !m.getReturnType().equals(INPUT_DATA_RETURN_TYPE))
                .map(m -> new Exception(String.format(
                    "Method %s marked @%s must return %s",
                    m.getName(),
                    TestInput.class.getSimpleName(),
                    INPUT_DATA_RETURN_TYPE.getCanonicalName()
        ))).collect(Collectors.toList()));
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

        errors.addAll(additionalDataAnnotatedMethods.stream()
                .filter(additionalDataMethod -> !additionalDataMethod.getReturnType().equals(INPUT_DATA_RETURN_TYPE))
                .map(additionalDataMethod -> new Exception(
                        String.format(
                                "Method %s annotated with @%s must return a %s!",
                                additionalDataMethod.getName(),
                                AdditionalData.class.getSimpleName(),
                                INPUT_DATA_RETURN_TYPE.getCanonicalName()
                        )
        )).collect(Collectors.toList()));
    }

    private List<FrameworkMethod> getTestInputMethods() {
        return getTestClass().getAnnotatedMethods(TestInput.class);
    }

    private void generateRdfUnitTestCases() throws InitializationError {
        final SchemaSource schemaSource = createSchemaSourceFromSchema();
        final Object testCaseInstanceLocal = getTestCaseInstance();
        final Collection<TestCase> testCasesLocal = createTestCases();
        for (FrameworkMethod testInputMethod : getTestInputMethods()) {
            final RdfUnitJunitTestCaseDataProvider rdfUnitJunitTestCaseDataProvider = new
                    RdfUnitJunitTestCaseDataProvider(testInputMethod, testCaseInstanceLocal, schemaSource,
                    additionalData);
            this.testCases.addAll(
                    testCasesLocal.stream()
                        .map(t -> new RdfUnitJunitTestCase(t, rdfUnitJunitTestCaseDataProvider))
                        .collect(Collectors.toList()));
        }
    }

    private void setUpSchemaReader() throws InitializationError {
        try {
            schemaReader = new RdfModelReader(RdfReaderFactory.createResourceOrFileOrDereferenceReader(getSchema().uri()).read());
        } catch (RdfReaderException e) {
            throw new InitializationError(e);
        }
    }

    private Object getTestCaseInstance() throws InitializationError {
        synchronized (this) {
            if (testCaseInstance == null) {
                try {
                    testCaseInstance = getTestClass().getJavaClass().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new InitializationError(e);
                }
            }
            return testCaseInstance;
        }
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
                            (RdfReader) additionalDataMethod.invokeExplosively(getTestCaseInstance()),
                            "Method %s annotated with @%s returned null!",
                            additionalDataMethod.getMethod().getName(),
                            AdditionalData.class.getSimpleName()
                    );
        } catch (Throwable e) {
            throw new InitializationError(e);
        }
    }

    private SchemaSource createSchemaSourceFromSchema() throws InitializationError {
        return SchemaSourceFactory.createSchemaSourceSimple(getSchema().uri(), getSchemaReader());
    }

    private Collection<TestCase> createTestCases() throws InitializationError {
        return new RDFUnitTestSuiteGenerator.Builder()
                .addSchemaURI("custom", getSchema().uri(), getSchemaReader())
                .enableAutotests()
                .build().getTestSuite().getTestCases();
    }

    private RdfReader getSchemaReader() {
        return schemaReader;
    }

    private Schema getSchema() throws InitializationError {
        Schema schema = getTestClass().getAnnotation(Schema.class);
        if (schema == null) {
            throw new InitializationError("Cannot read Schema");
        }
        return schema;
    }

    protected RdfReader getAdditionalDataModel() {
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
                        getTestInputBaseName(child.getTestInputMethod()),
                        child.getTestCase().getAbrTestURI()
                )
        );
    }

    private String getTestInputBaseName(FrameworkMethod method) {
        final String basename = method.getAnnotation(TestInput.class).name();
        return basename.isEmpty() ? method.getName() : basename;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isIgnored(RdfUnitJunitTestCase child) {
        return child.getTestInputMethod().getAnnotation(Ignore.class) != null;
    }

    /** {@inheritDoc} */
    @Override
    protected void runChild(final RdfUnitJunitTestCase child, RunNotifier notifier) {
        if(isIgnored(child)) {
            notifier.fireTestIgnored(describeChild(child));
        } else {
            this.runLeaf(new ShaclResultStatement(rdfUnitJunitStatusTestExecutor, child), describeChild(child), notifier);
        }
    }

}


