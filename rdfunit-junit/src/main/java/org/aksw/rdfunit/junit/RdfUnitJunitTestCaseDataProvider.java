package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.io.reader.RDFMultipleReader;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.apache.jena.rdf.model.Model;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.InvocationTargetException;

import static java.util.Arrays.asList;

final class RdfUnitJunitTestCaseDataProvider {

    private final FrameworkMethod testInputMethod;
    private final Object testCaseInstance;
    private final SchemaSource schemaSource;
    private final RDFReader additionalData;
    private boolean initialized = false;
    private Model testInputModel;
    private TestSource modelSource;

    /**
     * <p>Constructor for RdfUnitJunitTestCaseDataProvider.</p>
     *
     * @param testInputMethod a {@link org.junit.runners.model.FrameworkMethod} object.
     * @param testCaseInstance a {@link java.lang.Object} object.
     * @param schemaSource a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     * @param additionalData a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public RdfUnitJunitTestCaseDataProvider(
            FrameworkMethod testInputMethod,
            Object testCaseInstance,
            SchemaSource schemaSource,
            RDFReader additionalData
    ) {
        this.testInputMethod = testInputMethod;
        this.testCaseInstance = testCaseInstance;
        this.schemaSource = schemaSource;
        this.additionalData = additionalData;
    }

    void initialize() throws InitializationError {
        if (initialized) {
            return;
        }

        final RDFReader testInputReader;
        try {
            testInputReader = (RDFReader) testInputMethod.getMethod().invoke(testCaseInstance);
        } catch (IllegalAccessException | InvocationTargetException e1) {
            throw new InitializationError(e1);
        }
        if (testInputReader == null) {
            throw new NullPointerException(String.format(
                    "@%s: %s returned null!",
                    TestInput.class.getSimpleName(),
                    testInputMethod.getMethod().getName()
            ));
        }

        try {
            testInputModel = testInputReader.read();
        } catch (RDFReaderException readerException) {
            throw new RuntimeException(readerException);
        }
        modelSource = new TestSourceBuilder()
                // FIXME why do we need at least one source config? If we omit this, it will break...
                .setPrefixUri("custom", "rdfunit")
                .setInMemReader(
                        new RDFMultipleReader(asList(testInputReader, additionalData))
                )
                .setReferenceSchemata(schemaSource)
                .build();

        initialized = true;
    }

    /**
     * <p>Getter for the field <code>testInputModel</code>.</p>
     *
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     */
    public Model getTestInputModel() {
        return testInputModel;
    }

    /**
     * <p>Getter for the field <code>modelSource</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.sources.TestSource} object.
     */
    public TestSource getModelSource() {
        return modelSource;
    }

    /**
     * <p>Getter for the field <code>testInputMethod</code>.</p>
     *
     * @return a {@link org.junit.runners.model.FrameworkMethod} object.
     */
    public FrameworkMethod getTestInputMethod() {
        return testInputMethod;
    }
}
