package org.aksw.rdfunit.model.readers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 */
public final class TestCaseAnnotationReader implements ElementReader<TestCaseAnnotation> {

  private TestCaseAnnotationReader() {
  }

  public static TestCaseAnnotationReader create() {
    return new TestCaseAnnotationReader();
  }


  @Override
  public TestCaseAnnotation read(Resource resource) {
    checkNotNull(resource, "Cannot read a TestCaseAnnotation from a null resource");

    String description = null;
    TestAppliesTo appliesTo = null;
    TestGenerationType generated = null;
    String source = null;
    RLOGLevel testCaseLogLevel = null;//RLOGLevel.resolve(qs.get("testCaseLogLevel").toString());
    String testGenerator = null;

    int count = 0; // used to count duplicates

    //description
    for (Statement smt : resource.listProperties(DCTerms.description).toList()) {
      description = smt.getObject().asLiteral().getLexicalForm();
      checkArgument(++count == 1,
          "Cannot have more than one descriptions '%s' in TestCaseAnnotation %s", description,
          resource.getURI());
    }

    count = 0;
    for (Statement smt : resource.listProperties(RDFUNITv.appliesTo).toList()) {
      checkArgument(++count == 1, "Cannot have more than one rut:appliesTo TestCaseAnnotation %s",
          resource.getURI());
      appliesTo = TestAppliesTo.resolve(smt.getObject().asResource().getURI());
    }

    count = 0;
    for (Statement smt : resource.listProperties(RDFUNITv.source).toList()) {
      checkArgument(++count == 1, "Cannot have more than one rut:source TestCaseAnnotation %s",
          resource.getURI());
      source = smt.getObject().asResource().getURI();
    }

    count = 0;
    for (Statement smt : resource.listProperties(RDFUNITv.generated).toList()) {
      checkArgument(++count == 1, "Cannot have more than one rut:generated TestCaseAnnotation %s",
          resource.getURI());
      generated = TestGenerationType.resolve(smt.getObject().asResource().getURI());
    }

    count = 0;
    for (Statement smt : resource.listProperties(RDFUNITv.testCaseLogLevel).toList()) {
      checkArgument(++count == 1,
          "Cannot have more than one rut:testCaseLogLevel TestCaseAnnotation %s",
          resource.getURI());
      testCaseLogLevel = RLOGLevel.resolve(smt.getObject().asResource().getURI());
    }

    count = 0;
    for (Statement smt : resource.listProperties(RDFUNITv.testGenerator).toList()) {
      checkArgument(++count == 1,
          "Cannot have more than one rut:testGenerator in TestCaseAnnotation %s",
          resource.getURI());
      testGenerator = smt.getObject().asResource().getURI();
    }

    //references
    Set<String> referencesLst = new HashSet<>(
        resource.listProperties(RDFUNITv.references).toList().stream()
            .map(smt -> smt.getObject().asResource().getURI())
            .collect(Collectors.toList()));

    //annotations
    Set<ResultAnnotation> testAnnotations = new HashSet<>(
        resource.listProperties(RDFUNITv.resultAnnotation).toList().stream()
            .map(smt -> ResultAnnotationReader.create().read(smt.getResource()))
            .collect(Collectors.toList()));

    return new TestCaseAnnotation(
        resource, generated,
        testGenerator,
        appliesTo,
        source,
        referencesLst,
        description,
        testCaseLogLevel,
        testAnnotations
    );

    //return TestGeneratorImpl.createTAG(resource, description, query, pattern, generatorAnnotations);
  }
}
