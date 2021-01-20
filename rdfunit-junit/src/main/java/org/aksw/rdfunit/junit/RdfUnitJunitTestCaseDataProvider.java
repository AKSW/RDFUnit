package org.aksw.rdfunit.junit;

import static java.util.Arrays.asList;

import java.lang.reflect.InvocationTargetException;
import org.aksw.rdfunit.io.reader.RdfMultipleReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.apache.jena.rdf.model.Model;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

final class RdfUnitJunitTestCaseDataProvider {

  private final FrameworkMethod testInputMethod;
  private final Object testCaseInstance;
  private final SchemaSource schemaSource;
  private final RdfReader additionalData;
  private boolean initialized = false;
  private Model testInputModel;
  private TestSource modelSource;

  public RdfUnitJunitTestCaseDataProvider(
      FrameworkMethod testInputMethod,
      Object testCaseInstance,
      SchemaSource schemaSource,
      RdfReader additionalData
  ) {
    this.testInputMethod = testInputMethod;
    this.testCaseInstance = testCaseInstance;
    this.schemaSource = schemaSource;
    this.additionalData = additionalData;
  }

  public void initialize() throws InitializationError {
    if (initialized) {
      return;
    }

    final RdfReader testInputReader;
    try {
      testInputReader = (RdfReader) testInputMethod.getMethod().invoke(testCaseInstance);
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
    } catch (RdfReaderException readerException) {
      throw new IllegalArgumentException(readerException);
    }
    modelSource = new TestSourceBuilder()
        // FIXME why do we need at least one source config? If we omit this, it will break...
        .setPrefixUri("custom", "rdfunit")
        .setInMemReader(
            new RdfMultipleReader(asList(testInputReader, additionalData))
        )
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
