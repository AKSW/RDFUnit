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
import org.aksw.rdfunit.tests.results.RLOGTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.aksw.rdfunit.validate.wrappers.RDFUnitTestSuiteGenerator;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import static org.hamcrest.MatcherAssert.assertThat;

public class RdfUnitJunitRunner extends ParentRunner<RdfUnitJunitTestCase> {

    private final List<RdfUnitJunitTestCase> testCases = new ArrayList<>();
    private final RdfUnitJunitStatusTestExecutor rdfUnitJunitStatusTestExecutor = new RdfUnitJunitStatusTestExecutor();
    private Model ontologyModel;

    public RdfUnitJunitRunner(Class<?> testClass) throws InitializationError {
        super(testClass);

        checkOntologyAnnotation();
        checkInputModelAnnotatedMethods();

        createOntologyModel();
        generateRdfUnitTestCases();
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
        final Map<FrameworkMethod, Model> inputModels = getInputModels();
        final SchemaSource schemaSourceFromOntology = createSchemaSourceFromOntology();
        for (TestCase t : createTestCases()) {
            for (Map.Entry<FrameworkMethod, Model> e : inputModels.entrySet()) {
                testCases.add(new RdfUnitJunitTestCase(t, schemaSourceFromOntology, e.getValue(), e.getKey()));
            }
        }
    }

    private void createOntologyModel() {
        ontologyModel = ModelFactory.createDefaultModel().read(getOntology().uri());
    }

    private Object createTestCaseInstance() throws InitializationError {
        final Object testInstance;
        try {
            testInstance = getTestClass().getJavaClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InitializationError(e);
        }
        return testInstance;
    }

    private Map<FrameworkMethod, Model> getInputModels() throws InitializationError {
        final Map<FrameworkMethod, Model> inputModels = new LinkedHashMap<>();
        for (FrameworkMethod m : getInputModelMethods()) {
            try {
                inputModels.put(m, (Model) m.getMethod().invoke(createTestCaseInstance()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InitializationError(e);
            }
        }
        return inputModels;
    }

    private SchemaSource createSchemaSourceFromOntology() {
        final String uri = getOntology().uri();
        return SchemaSourceFactory.createSchemaSourceSimple("custom", uri, getRdfReaderForOntology());
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
        final Statement statement = new Statement() {

            @Override
            public void evaluate() throws Throwable {
                final Collection<TestCaseResult> testCaseResults = rdfUnitJunitStatusTestExecutor.runTest(child);
                final StringBuilder b = new StringBuilder();
                b.append(child.getTestCase().getResultMessage()).append(":\n");
                for (TestCaseResult t : testCaseResults) {
                    RLOGTestCaseResult r = (RLOGTestCaseResult) t;
                    b.append("\t").append(r.getResource()).append("\n");
                }
                assertThat(b.toString(), testCaseResults.isEmpty());
            }
        };
        this.runLeaf(statement, describeChild(child), notifier);
    }

}
