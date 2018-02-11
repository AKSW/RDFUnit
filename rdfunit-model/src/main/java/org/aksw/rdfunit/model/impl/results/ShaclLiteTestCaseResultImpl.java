package org.aksw.rdfunit.model.impl.results;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * The Simplified SHACL result that contains only message, severity and focusNode
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:28 PM

 */
@ToString
@EqualsAndHashCode(exclude = "element", callSuper = false)
public class ShaclLiteTestCaseResultImpl extends BaseTestCaseResultImpl implements ShaclLiteTestCaseResult {

    private final RDFNode focusNode;

    public ShaclLiteTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, RDFNode focusNode) {
        super(testCaseUri, severity, message);
        this.focusNode = focusNode;
    }

    public ShaclLiteTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, RDFNode focusNode) {
        super(element, testCaseUri, severity, message, timestamp);
        this.focusNode = focusNode;
    }

    @Override
    public RDFNode getFailingNode() {
        return focusNode;
    }

}
