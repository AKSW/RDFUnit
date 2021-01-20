package org.aksw.rdfunit.utils;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.writer.RdfWriter;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.aksw.rdfunit.model.impl.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.PatternBasedTestCase;
import org.aksw.rdfunit.model.readers.BatchTestCaseReader;
import org.aksw.rdfunit.model.writers.TestCaseWriter;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;


/**
 * Various utility test functions for tests
 *
 * @author Dimitris Kontokostas
 * @since 9/24/13 10:59 AM
 */
@Slf4j
public final class TestUtils {

  private TestUtils() {
  }

  public static Collection<GenericTestCase> instantiateTestsFromModel(Model model) {
    return instantiateTestsFromModel(model, false);
  }

  public static Collection<GenericTestCase> instantiateTestsFromModel(Model model, boolean strict) {
    return BatchTestCaseReader.create().getTestCasesFromModel(model);
  }

  public static void writeTestsToFile(Collection<GenericTestCase> tests, RdfWriter testCache) {
    writeTestsToFile(tests, testCache, false);
  }

  public static void writeTestsToFile(Collection<GenericTestCase> tests, RdfWriter testCache,
      boolean addExplicitWhere) {
    Model model = ModelFactory.createDefaultModel();
    for (GenericTestCase t : tests) {
      Resource testCaseResource = TestCaseWriter.create(t).write(model);

      if (addExplicitWhere) {
        if (t instanceof PatternBasedTestCaseImpl) {
          testCaseResource.addProperty(RDFUNITv.sparqlWhere, ((PatternBasedTestCase) t).getSparqlWhere());
        }
      }
    }
    try {
      org.aksw.rdfunit.services.PrefixNSService.setNSPrefixesInModel(model);
      testCache.write(model);
    } catch (RdfWriterException e) {
      log.error("Cannot cache tests", e);
    }
  }

  public static String generateTestURI(String sourcePrefix, Pattern pattern,
      Collection<Binding> bindings, String generatorURI) {
    String testURI =
        org.aksw.rdfunit.services.PrefixNSService.getNSFromPrefix("rutt") + sourcePrefix + "-"
            + pattern.getId() + "-";
    StringBuilder string2hash = new StringBuilder(generatorURI);
    for (Binding binding : bindings) {
      string2hash.append(binding.getValueAsString());
    }
    String md5Hash = StringUtils.getHashFromString(string2hash.toString());
    if (md5Hash.isEmpty()) {
      return JenaUtils.getUniqueIri(testURI);
    } else {
      return testURI + md5Hash;
    }
  }


}
