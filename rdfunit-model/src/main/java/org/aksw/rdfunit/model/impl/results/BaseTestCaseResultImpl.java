package org.aksw.rdfunit.model.impl.results;

import java.util.Calendar;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * An abstract Test Case Result.
 *
 * @author Dimitris Kontokostas
 * @since 1 /2/14 3:44 PM
 */

@ToString
@EqualsAndHashCode(exclude = "element")
class BaseTestCaseResultImpl implements TestCaseResult {

  private final Resource element;
  private final Resource testCaseUri;
  private final RLOGLevel severity;
  private final String message;
  private final XSDDateTime timestamp;

  /**
   * Constructor
   *
   * @param testCase the test case this result is related with
   */
  protected BaseTestCaseResultImpl(TestCase testCase) {
    this(testCase.getElement(), testCase.getLogLevel(), testCase.getResultMessage());
  }

  protected BaseTestCaseResultImpl(Resource testCaseUri, RLOGLevel severity, String message) {
    this(ResourceFactory.createResource(), testCaseUri, severity, message,
        new XSDDateTime(Calendar.getInstance()));
  }

  protected BaseTestCaseResultImpl(Resource element, Resource testCaseUri, RLOGLevel severity,
      String message, XSDDateTime timestamp) {
    this.element = element;
    this.testCaseUri = testCaseUri;
    this.severity = severity;
    this.message = message;
    this.timestamp = timestamp;
  }

  @Override
  public Resource getElement() {
    return element;
  }

  @Override
  public Resource getTestCaseUri() {
    return testCaseUri;
  }

  @Override
  public XSDDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public RLOGLevel getSeverity() {
    return severity;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
