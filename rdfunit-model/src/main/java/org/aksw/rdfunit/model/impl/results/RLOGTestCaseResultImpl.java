package org.aksw.rdfunit.model.impl.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;

@Deprecated
public class RLOGTestCaseResultImpl extends BaseTestCaseResultImpl implements RLOGTestCaseResult {

    private final String resource;

    public RLOGTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource) {
        super(testCaseUri, severity, message);
        this.resource = resource;
    }

    public RLOGTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, String resource) {
        super(element, testCaseUri, severity, message, timestamp);
        this.resource = resource;
    }

    public String getFailingResource() {
        return resource;
    }
}
