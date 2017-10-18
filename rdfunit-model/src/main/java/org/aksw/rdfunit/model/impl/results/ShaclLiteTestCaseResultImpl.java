package org.aksw.rdfunit.model.impl.results;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;

/**
 * The Simplified SHACL result that contains only message, severity and focusNode
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:28 PM
 * @version $Id: $Id
 */
@ToString
@EqualsAndHashCode(exclude = "element", callSuper = false)
public class ShaclLiteTestCaseResultImpl extends BaseTestCaseResultImpl implements ShaclLiteTestCaseResult {

    private final String resource;

    public ShaclLiteTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource) {
        super(testCaseUri, severity, message);
        this.resource = resource;
    }

    public ShaclLiteTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, String resource) {
        super(element, testCaseUri, severity, message, timestamp);
        this.resource = resource;
    }

    @Override
    public String getFailingResource() {
        return resource;
    }

}
