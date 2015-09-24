package org.aksw.rdfunit.junit;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.io.reader.RDFMultipleReader;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.InvocationTargetException;

import static java.util.Arrays.asList;
import static org.aksw.rdfunit.junit.InitializationSupport.checkNotNull;

final class RdfUnitJunitTestCaseDataProvider {

    private boolean initialized = false;
    private FrameworkMethod testInputMethod;
    private final Object testCaseInstance;
    private final SchemaSource schemaSource;
    private final RDFReader additionalData;
    private RDFReader testInputReader;
    private Model testInputModel;
    private RDFMultipleReader combinedReader;
    private TestSource modelSource;

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
        if (initialized)
            return;

        try {
            testInputReader = checkNotNull(
                    (RDFReader) testInputMethod.getMethod().invoke(testCaseInstance),
                    "@%s: %s returned null!",
                    TestInput.class.getSimpleName(),
                    testInputMethod.getMethod().getName()
            );
        } catch (IllegalAccessException | InvocationTargetException e1) {
            throw new InitializationError(e1);
        }
        try {
            testInputModel = testInputReader.read();
        } catch (RDFReaderException e1) {
            throw new InitializationError(e1);
        }
        combinedReader = new RDFMultipleReader(asList(testInputReader,
                additionalData));
        modelSource = new TestSourceBuilder()
                // FIXME why do we need at least one source config? If we omit this, it will break...
                .setPrefixUri("custom", "rdfunit")
                .setInMemReader(combinedReader)
                .setReferenceSchemata(schemaSource)
                .build();

        initialized = true;
    }

    public Model getTestInputModel() {
        return testInputModel;
    }

    public TestSource getModelSource() {
        return modelSource;
    }

    public FrameworkMethod getTestInputMethod() {
        return testInputMethod;
    }
}
