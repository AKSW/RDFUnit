package org.aksw.rdfunit.model.readers;

import static com.google.common.base.Preconditions.checkArgument;
import static lombok.Lombok.checkNotNull;

import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 */
public final class ManualTestCaseReader implements ElementReader<TestCase> {

  private ManualTestCaseReader() { }

  public static ManualTestCaseReader create() {
    return new ManualTestCaseReader();
  }

  @Override
  public TestCase read(Resource resource) {
    checkNotNull(resource, "Cannot read a ManualTestCase from a null resource");

    ManualTestCaseImpl.ManualTestCaseImplBuilder builder = ManualTestCaseImpl.builder();

    builder.element(resource);

    int count; // used to count duplicates

    count = 0;
    for (Statement smt : resource.listProperties(RDFUNITv.sparqlWhere).toList()) {
      checkArgument(++count == 1, "Cannot have more than one rut:sparqlWhere in Test %s",
          resource.getURI());
      builder.sparqlWhere(smt.getObject().asLiteral().getLexicalForm());
    }

    count = 0;
    for (Statement smt : resource.listProperties(RDFUNITv.sparqlPrevalence).toList()) {
      checkArgument(++count == 1, "Cannot have more than one rut:sparqlPrevalence in Test %s",
          resource.getURI());
      builder.sparqlPrevalence(smt.getObject().asLiteral().getLexicalForm());
    }

    builder.testCaseAnnotation(TestCaseAnnotationReader.create().read(resource));

    return builder.build();
  }
}
