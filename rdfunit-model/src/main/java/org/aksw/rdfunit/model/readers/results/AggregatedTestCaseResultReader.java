package org.aksw.rdfunit.model.readers.results;

import static com.google.common.base.Preconditions.checkNotNull;

import org.aksw.rdfunit.model.impl.results.AggregatedTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.StatusTestCaseResult;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 */
public final class AggregatedTestCaseResultReader implements
    ElementReader<AggregatedTestCaseResult> {

  private AggregatedTestCaseResultReader() {
  }

  public static AggregatedTestCaseResultReader create() {
    return new AggregatedTestCaseResultReader();
  }


  @Override
  public AggregatedTestCaseResult read(final Resource resource) {
    checkNotNull(resource);

    StatusTestCaseResult test = StatusTestCaseResultReader.create().read(resource);

    Long resultCount = null;
    for (Statement smt : resource.listProperties(RDFUNITv.resultCount).toList()) {
      resultCount = smt.getObject().asLiteral().getLong();
    }
    checkNotNull(resultCount);

    Long resultPrevalence = null;
    for (Statement smt : resource.listProperties(RDFUNITv.resultPrevalence).toList()) {
      resultPrevalence = smt.getObject().asLiteral().getLong();
    }
    checkNotNull(resultPrevalence);

    return new AggregatedTestCaseResultImpl(resource, test.getTestCaseUri(), test.getSeverity(),
        test.getMessage(), test.getTimestamp(), test.getStatus(), resultCount, resultPrevalence);
  }
}
