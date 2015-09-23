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
import org.aksw.rdfunit.io.reader.RDFReader;
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

import static org.aksw.rdfunit.junit.InitializationSupport.checkNotNull;
import static org.aksw.rdfunit.junit.InitializationSupport.checkState;

public class RdfUnitJunitRunner extends ParentRunner<RdfUnitJunitTestCase> {

    public static final Class<Model> INPUT_DATA_RETURN_TYPE = Model.class;
    private final List<RdfUnitJunitTestCase> testCases = new ArrayList<>();
    private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor = new RdfUnitJunitStatusTestExecutor();
    private Model controlledVocabulary = ModelFactory.createDefaultModel();
    private Model ontologyModel;
    private Object testCaseInstance;

    public RdfUnitJunitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        checkSchemaAnnotation();
        checkTestInputAnnotatedMethods();

        createOntologyModel();

        setControlledVocabulary();

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
        final SchemaSource schemaSourceFromOntology = createSchemaSourceFromOntology();
        final Collection<TestCase> testCases = createTestCases();
        for (Map.Entry<FrameworkMethod, Model> e : getInputModels().entrySet()) {
            final TestSource modelSource = new TestSourceBuilder()
                    // FIXME why do we need at least one source config? If we omit this, it will break...
                    .setPrefixUri("custom", "rdfunit")
                    .setInMemReader(new RDFModelReader(e.getValue()))
                    .setReferenceSchemata(schemaSourceFromOntology)
                    .build();
            for (TestCase t : testCases) {
                this.testCases.add(new RdfUnitJunitTestCase(t, e.getValue(), e.getKey(), modelSource));
            }
        }
    }

    private void createOntologyModel() {
        ontologyModel = ModelFactory.createDefaultModel().read(getOntology().uri());
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

    private void setControlledVocabulary() throws InitializationError {
        final List<FrameworkMethod> controlledVocabularyAnnotatedMethods =
                getTestClass().getAnnotatedMethods(ControlledVocabulary.class);
        if (controlledVocabularyAnnotatedMethods.isEmpty()) {
            return;
        }
        checkState(
                controlledVocabularyAnnotatedMethods.size() <= 1,
                "At most one method annotated with @%s allowed!",
                ControlledVocabulary.class.getSimpleName()
        );
        try {
            final FrameworkMethod controlledVocabularyMethod = controlledVocabularyAnnotatedMethods.get(0);
            checkState(
                    controlledVocabularyMethod.getReturnType().equals(INPUT_DATA_RETURN_TYPE),
                    "Method annotated with @%s must return a %s!",
                    ControlledVocabulary.class.getSimpleName(),
                    INPUT_DATA_RETURN_TYPE.getCanonicalName()
            );
            controlledVocabulary =
                    checkNotNull(
                            (Model) controlledVocabularyMethod.invokeExplosively(getTestCaseInstance()),
                            "Method %s annotated with @%s returned null!",
                            controlledVocabularyMethod.getMethod().getName(),
                            ControlledVocabulary.class.getSimpleName()
                    );
        } catch (Throwable e) {
            throw new InitializationError(e);
        }
    }

    private Map<FrameworkMethod, Model> getInputModels() throws InitializationError {
        final Map<FrameworkMethod, Model> inputModels = new LinkedHashMap<>();
        for (FrameworkMethod m : getTestInputMethods()) {
            try {
                final Model inputModel = checkNotNull(
                        (Model) m.getMethod().invoke(getTestCaseInstance()),
                        "@%s: %s returned null!",
                        TestInput.class.getSimpleName(),
                        m.getMethod().getName()
                );
                inputModels.put(m, ModelFactory.createDefaultModel().add(inputModel).add(controlledVocabulary));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InitializationError(e);
            }
        }
        return inputModels;
    }

    private SchemaSource createSchemaSourceFromOntology() {
        return SchemaSourceFactory.createSchemaSourceSimple("custom", getOntology().uri(), getRdfReaderForOntology());
    }

    private Collection<TestCase> createTestCases() throws InitializationError {
        return new RDFUnitTestSuiteGenerator.Builder()
                .addSchemaURI("custom", getOntology().uri(), getRdfReaderForOntology())
                .enableAutotests()
                .build().getTestSuite().getTestCases();
    }

    private RDFReader getRdfReaderForOntology() {
        return new RDFModelReader(ontologyModel);
    }

    private Schema getOntology() {
        return getTestClass().getAnnotation(Schema.class);
    }

    Model getControlledVocabularyModel() {
        return controlledVocabulary;
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


