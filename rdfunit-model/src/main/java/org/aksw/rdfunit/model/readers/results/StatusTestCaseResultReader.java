package org.aksw.rdfunit.model.readers.results;

import static com.google.common.base.Preconditions.checkNotNull;

import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.impl.results.StatusTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.readers.ElementReader;
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
public final class StatusTestCaseResultReader implements ElementReader<StatusTestCaseResult> {

  private StatusTestCaseResultReader() {
  }

  public static StatusTestCaseResultReader create() {
    return new StatusTestCaseResultReader();
  }


  @Override
  public StatusTestCaseResult read(final Resource resource) {
    checkNotNull(resource);

    TestCaseResult test = TestCaseResultReader
        .create(DCTerms.description, RDFUNITv.testCaseLogLevel).read(resource);

    TestCaseResultStatus status = null;
    for (Statement smt : resource.listProperties(RDFUNITv.resultStatus).toList()) {
      status = TestCaseResultStatus.resolve(smt.getObject().asResource().getURI());
    }
    checkNotNull(status);

    return new StatusTestCaseResultImpl(resource, test.getTestCaseUri(), test.getSeverity(),
        test.getMessage(), test.getTimestamp(), status);
  }
}
