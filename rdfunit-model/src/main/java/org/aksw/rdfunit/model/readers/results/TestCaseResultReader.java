package org.aksw.rdfunit.model.readers.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM

 */
public final class TestCaseResultReader implements ElementReader<TestCaseResult> {

    private final Property messageProperty;
    private final Property severityProperty;

    private TestCaseResultReader(Property messageProperty, Property severityProperty){
        this.messageProperty = messageProperty;
        this.severityProperty = severityProperty;
    }

    public static TestCaseResultReader create(Property messageProperty, Property severityProperty) { return new TestCaseResultReader(messageProperty, severityProperty);}


    @Override
    public TestCaseResult read(final Resource resource) {
        checkNotNull(resource);

        Resource testCaseUri = null;
        for (Statement smt : resource.listProperties(RDFUNITv.testCase).toList()) {
            testCaseUri = smt.getObject().asResource();
        }
        checkNotNull(testCaseUri);

        RLOGLevel severity = null;
        for (Statement smt : resource.listProperties(severityProperty).toList()) {
            severity = RLOGLevel.resolve(smt.getObject().asResource().getURI());
        }
        checkNotNull(severity);

        String message = null;
        for (Statement smt : resource.listProperties(messageProperty).toList()) {
            message = smt.getObject().asLiteral().getLexicalForm();
        }
        checkNotNull(message);

        XSDDateTime timestamp = null;
        for (Statement smt : resource.listProperties(DCTerms.date).toList()) {
            timestamp = (XSDDateTime) XSDDatatype.XSDdateTime.parse(smt.getObject().asLiteral().getLexicalForm());
        }
        checkNotNull(timestamp);

        final Resource uriCopy = testCaseUri;
        final RLOGLevel severityCopy = severity;
        final String messageCopy = message;
        final XSDDateTime timestampCopy = timestamp;
        return new TestCaseResult() {

            @Override
            public Resource getElement() {
                return resource;
            }

            @Override
            public Resource getTestCaseUri() {
                return uriCopy;
            }

            @Override
            public RLOGLevel getSeverity() {
                return severityCopy;
            }

            @Override
            public String getMessage() {
                return messageCopy;
            }

            @Override
            public XSDDateTime getTimestamp() {
                return timestampCopy;
            }
        };
    }
}
