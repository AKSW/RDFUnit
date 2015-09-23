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
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class RdfUnitJunitRunner extends ParentRunner<RdfUnitJunitTestCase> {

    private final List<RdfUnitJunitTestCase> testCases = new ArrayList<>();
    private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor = new RdfUnitJunitStatusTestExecutor();
    private Model controlledVocabulary = ModelFactory.createDefaultModel();
    private Model ontologyModel;
    private Object testCaseInstance;

    public RdfUnitJunitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        checkOntologyAnnotation();
        checkInputModelAnnotatedMethods();

        createOntologyModel();

        setControlledVocabulary();

        generateRdfUnitTestCases();
    }

    private void setControlledVocabulary() throws InitializationError {
        final List<FrameworkMethod> annotatedMethods = getTestClass().getAnnotatedMethods(ControlledVocabulary.class);
        if (annotatedMethods.isEmpty()) {
            return;
        }
        if (annotatedMethods.size() > 1) {
            throw new InitializationError("Only one method annotated with @ControlledVocabulary allowed!");
        }
        try {
            final FrameworkMethod controlledVocabularyMethod = annotatedMethods.get(0);
            if (!controlledVocabularyMethod.getReturnType().equals(Model.class)) {
                throw new InitializationError(
                        String.format(
                                "Method annotated with @ControlledVocabulary must return a %s!",
                                Model.class.getCanonicalName()
                        )
                );
            }
            controlledVocabulary = (Model) controlledVocabularyMethod.invokeExplosively(getTestCaseInstance());
            if(controlledVocabulary == null) {
                throw new InitializationError(
                        String.format("@ControlledVocabulary: %s returned null!", controlledVocabularyMethod
                                .getMethod().getName())
                );
            }
        } catch (Throwable e) {
            throw new InitializationError(e);
        }
    }

    private void checkOntologyAnnotation() throws InitializationError {
        if (getTestClass().getJavaClass().isAnnotationPresent(Ontology.class)) {
            return;
        }
        throw new InitializationError("@Ontology annotation is required!");
    }

    private void checkInputModelAnnotatedMethods() throws InitializationError {
        for (FrameworkMethod m : getInputModelMethods()) {
            if (!m.getReturnType().equals(Model.class)) {
                throw new InitializationError("Methods marked @InputModel must return com.hp.hpl.jena.rdf.model.Model");
            }
        }
    }

    private List<FrameworkMethod> getInputModelMethods() throws InitializationError {
        List<FrameworkMethod> inputModelMethods = getTestClass().getAnnotatedMethods(InputModel.class);
        if (inputModelMethods.isEmpty()) {
            throw new InitializationError("At least one method with @InputModel annotation is required!");
        }
        return inputModelMethods;
    }

    private void generateRdfUnitTestCases() throws InitializationError {
        final SchemaSource schemaSourceFromOntology = createSchemaSourceFromOntology();
        final Collection<TestCase> testCases = createTestCases();
        for (Map.Entry<FrameworkMethod, Model> e : getInputModels().entrySet()) {
            for (TestCase t : testCases) {
                this.testCases.add(new RdfUnitJunitTestCase(t, schemaSourceFromOntology, e.getValue(), e.getKey()));
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

    private Map<FrameworkMethod, Model> getInputModels() throws InitializationError {
        final Map<FrameworkMethod, Model> inputModels = new LinkedHashMap<>();
        for (FrameworkMethod m : getInputModelMethods()) {
            try {
                final Model inputModel = (Model) m.getMethod().invoke(getTestCaseInstance());
                if (inputModel == null) {
                    throw new InitializationError(
                            String.format("@InputModel: %s returned null!", m.getMethod().getName())
                    );
                }
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

    private Ontology getOntology() {
        return getTestClass().getAnnotation(Ontology.class);
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

